package com.spleefleague.core.listeners;

import com.comphenix.packetwrapper.WrapperPlayClientBlockDig;
import com.comphenix.packetwrapper.WrapperPlayServerMapChunk;
import com.comphenix.packetwrapper.WrapperPlayServerUnloadChunk;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.events.FakeBlockBreakEvent;
import com.spleefleague.core.utils.fakeblock.ChunkPacketUtil;
import com.spleefleague.core.utils.fakeblock.FakeArea;
import com.spleefleague.core.utils.fakeblock.FakeBlock;
import com.spleefleague.core.utils.fakeblock.FakeBlockCache;
import com.spleefleague.core.utils.fakeblock.MultiBlockChangeUtil;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_11_R1.EntityPlayer;
import net.minecraft.server.v1_11_R1.Packet;
import net.minecraft.server.v1_11_R1.PacketPlayOutWorldEvent;
import net.minecraft.server.v1_11_R1.SoundEffectType;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_11_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class FakeBlockHandler implements Listener {

    private PacketAdapter chunkData;
    private PacketAdapter unloadChunk;
    private PacketAdapter breakController;
    private PacketAdapter placeController;
    private static final ProtocolManager manager = SpleefLeague.getProtocolManager();
    private static final Map<UUID, Set<FakeArea>> fakeAreas = new HashMap<UUID, Set<FakeArea>>();
    private static final Map<UUID, FakeBlockCache> fakeBlockCache = new HashMap<UUID, FakeBlockCache>();
    private static FakeBlockHandler instance;
    private static final HashMap<Material, SoundEffectType> breakSounds;

    private FakeBlockHandler() {
        this.initPacketListeners();
        Bukkit.getOnlinePlayers().stream().filter(player -> !fakeAreas.containsKey(player.getUniqueId())).forEach(player -> {
            fakeAreas.put(player.getUniqueId(), new HashSet());
            fakeBlockCache.put(player.getUniqueId(), new FakeBlockCache());
        });
    }

    public static void stop() {
        if (instance != null) {
            manager.removePacketListener(FakeBlockHandler.instance.chunkData);
            manager.removePacketListener(FakeBlockHandler.instance.unloadChunk);
            manager.removePacketListener(FakeBlockHandler.instance.breakController);
            manager.removePacketListener(FakeBlockHandler.instance.placeController);
            HandlerList.unregisterAll((Listener) instance);
            instance = null;
        }
    }

    public static void init() {
        if (instance == null) {
            instance = new FakeBlockHandler();
            Bukkit.getPluginManager().registerEvents((Listener) instance, (Plugin) SpleefLeague.getInstance());
        }
    }

    private void initPacketListeners() {
        this.chunkData = new PacketAdapter((Plugin) SpleefLeague.getInstance(), ListenerPriority.NORMAL, new PacketType[]{PacketType.Play.Server.MAP_CHUNK}) {

            @Override
            public void onPacketReceiving(PacketEvent event) {
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerMapChunk wpsmc = new WrapperPlayServerMapChunk(event.getPacket());
                Bukkit.getScheduler().runTask((Plugin) SpleefLeague.getInstance(), () -> {
                    Chunk chunk = event.getPlayer().getWorld().getChunkAt(wpsmc.getChunkX(), wpsmc.getChunkZ());
                    MultiBlockChangeUtil.addChunk(event.getPlayer(), chunk);
                }
                );
                Set<FakeBlock> blocks = FakeBlockHandler.getFakeBlocksForChunk(event.getPlayer(), wpsmc.getChunkX(), wpsmc.getChunkZ());
                if (true && blocks != null) {
                    ChunkPacketUtil.setBlocksPacketMapChunk(event.getPlayer().getWorld(), event.getPacket(), blocks);
                }
            }
        };
        this.unloadChunk = new PacketAdapter((Plugin) SpleefLeague.getInstance(), ListenerPriority.NORMAL, new PacketType[]{PacketType.Play.Server.UNLOAD_CHUNK}) {

            @Override
            public void onPacketReceiving(PacketEvent event) {
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerUnloadChunk wpsuc = new WrapperPlayServerUnloadChunk(event.getPacket());
                Bukkit.getScheduler().runTask((Plugin) SpleefLeague.getInstance(), () -> {
                    Chunk chunk = event.getPlayer().getWorld().getChunkAt(wpsuc.getChunkX(), wpsuc.getChunkZ());
                    MultiBlockChangeUtil.removeChunk(event.getPlayer(), chunk);
                }
                );
            }
        };
        this.breakController = new PacketAdapter((Plugin) SpleefLeague.getInstance(), ListenerPriority.NORMAL, new PacketType[]{PacketType.Play.Client.BLOCK_DIG}) {

            @Override
            public void onPacketReceiving(PacketEvent event) {
                WrapperPlayClientBlockDig wrapper = new WrapperPlayClientBlockDig(event.getPacket());
                if (wrapper.getStatus() != EnumWrappers.PlayerDigType.START_DESTROY_BLOCK && wrapper.getStatus() != EnumWrappers.PlayerDigType.ABORT_DESTROY_BLOCK && wrapper.getStatus() != EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK) {
                    return;
                }
                if (event.getPlayer().getLocation().multiply(0.0).add(wrapper.getLocation().toVector()).getBlock().getType() == Material.AIR && (wrapper.getStatus() == EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK || wrapper.getStatus() == EnumWrappers.PlayerDigType.START_DESTROY_BLOCK)) {
                    Location loc = wrapper.getLocation().toVector().toLocation(event.getPlayer().getWorld());
                    FakeBlock broken = null;
                    Chunk chunk = loc.getChunk();
                    Set<FakeBlock> fakeBlocks = FakeBlockHandler.getFakeBlocksForChunk(event.getPlayer(), chunk.getX(), chunk.getZ());
                    if (fakeBlocks != null) {
                        for (FakeBlock block : fakeBlocks) {
                            if (blockEqual(loc, block.getLocation())) {
                                broken = block;
                                break;
                            }
                        }
                    }
                    if (broken != null) {
                        if (wrapper.getStatus() == EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK || broken.getType() == Material.SNOW_BLOCK && event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() == Material.DIAMOND_SPADE || event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                            FakeBlockBreakEvent fbbe = new FakeBlockBreakEvent(broken, event.getPlayer());
                            Bukkit.getPluginManager().callEvent((Event) fbbe);
                            if (fbbe.isCancelled()) {
                                event.getPlayer().sendBlockChange(fbbe.getBlock().getLocation(), fbbe.getBlock().getType(), (byte)0);
                            } else {
                                broken.setType(Material.AIR);
                                for (Player subscriber : FakeBlockHandler.getSubscribers(broken)) {
                                    if (subscriber == event.getPlayer()) {
                                        continue;
                                    }
                                    subscriber.sendBlockChange(fbbe.getBlock().getLocation(), Material.AIR, (byte)0);
                                    sendBreakParticles(subscriber, broken);
                                    sendBreakSound(subscriber, broken);
                                }
                            }
                        } else {
                            event.setCancelled(true);
                        }
                    }
                }
            }

            @Override
            public void onPacketSending(PacketEvent event) {
            }
        };
        manager.addPacketListener(this.chunkData);
        manager.addPacketListener(this.unloadChunk);
        manager.addPacketListener(this.breakController);
    }

    private void sendBreakParticles(Player p, FakeBlock block) {
        PacketPlayOutWorldEvent packet = new PacketPlayOutWorldEvent(2001, new net.minecraft.server.v1_11_R1.BlockPosition(block.getX(), block.getY(), block.getZ()), 80, false);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket((Packet) packet);
    }

    private void sendBreakSound(Player p, FakeBlock b) {
        EntityPlayer entity = ((CraftPlayer) p).getHandle();
        SoundEffectType effectType = breakSounds.get(b.getType());
        entity.a(effectType.d(), effectType.a() * 0.15f, effectType.b());
    }

    private boolean blockEqual(Location loc1, Location loc2) {
        if ((loc1.getX() + 0.5) / 1.0 == (loc2.getX() + 0.5) / 1.0 && (loc1.getZ() + 0.5) / 1.0 == (loc2.getZ() + 0.5) / 1.0 && loc1.getY() / 1.0 == loc2.getY() / 1.0) {
            return true;
        }
        return false;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!fakeAreas.containsKey(event.getPlayer().getUniqueId())) {
            fakeAreas.put(event.getPlayer().getUniqueId(), new HashSet());
            fakeBlockCache.put(event.getPlayer().getUniqueId(), new FakeBlockCache());
        }
    }

    public static /* varargs */ void addArea(FakeArea area, Player... players) {
        FakeBlockHandler.addArea(area, true, players);
    }

    public static /* varargs */ void addArea(FakeArea area, boolean update, Player... players) {
        for (Player player : players) {
            fakeAreas.get(player.getUniqueId()).add(area);
            fakeBlockCache.get(player.getUniqueId()).addArea(area);
        }
        if (update) {
            MultiBlockChangeUtil.changeBlocks(area.getBlocks().toArray(new FakeBlock[0]), players);
        }
    }

    public static /* varargs */ void removeArea(FakeArea area, Player... players) {
        FakeBlockHandler.removeArea(area, true, players);
    }

    public static /* varargs */ void removeArea(FakeArea area, boolean update, Player... players) {
        for (Player player : players) {
            fakeAreas.get(player.getUniqueId()).remove(area);
            FakeBlockHandler.recalculateCache(player);
        }
        if (update) {
            Collection<FakeBlock> fblocks = area.getBlocks();
            Block[] blocks = new Block[fblocks.size()];
            int i = 0;
            for (FakeBlock fblock : fblocks) {
                blocks[i++] = fblock.getLocation().getBlock();
            }
            MultiBlockChangeUtil.changeBlocks(blocks, Material.AIR, players);
        }
    }

    public static Collection<Player> getSubscribers(FakeBlock block) {
        HashSet<Player> players = new HashSet<Player>();
        block0:
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (FakeArea area : fakeAreas.get(player.getUniqueId())) {
                if (!area.getBlocks().contains(block)) {
                    continue;
                }
                players.add(player);
                continue block0;
            }
        }
        return players;
    }

    public static /* varargs */ void addBlock(FakeBlock block, Player... players) {
        FakeBlockHandler.addBlock(block, true, players);
    }

    public static /* varargs */ void addBlock(FakeBlock block, boolean update, Player... players) {
        for (Player player : players) {
            fakeAreas.get(player.getUniqueId()).add(block);
            fakeBlockCache.get(player.getUniqueId()).addBlocks(block);
            if (!update) {
                continue;
            }
            player.sendBlockChange(block.getLocation(), block.getType(), (byte)0);
        }
    }

    public static /* varargs */ void removeBlock(FakeBlock block, Player... players) {
        FakeBlockHandler.removeBlock(block, true, players);
    }

    public static /* varargs */ void removeBlock(FakeBlock block, boolean update, Player... players) {
        for (Player player : players) {
            fakeAreas.get(player.getUniqueId()).remove(block);
            FakeBlockHandler.recalculateCache(player);
            if (!update) {
                continue;
            }
            player.sendBlockChange(block.getLocation(), Material.AIR, (byte)0);
        }
    }

    public static void update(FakeArea area) {
        HashSet<Player> players = new HashSet<>();
        for (Map.Entry<UUID, Set<FakeArea>> entry : fakeAreas.entrySet()) {
            Player player;
            if (!entry.getValue().contains(area) || (player = Bukkit.getPlayer((UUID) entry.getKey())) == null) {
                continue;
            }
            players.add(player);
        }
        MultiBlockChangeUtil.changeBlocks(area.getBlocks().toArray(new FakeBlock[0]), players.toArray(new Player[players.size()]));
    }

    private static /* varargs */ void recalculateCache(Player... players) {
        for (Player player : players) {
            FakeBlockCache cache = fakeBlockCache.get(player.getUniqueId());
            cache.clear();
            for (FakeArea area : fakeAreas.get(player.getUniqueId())) {
                if (area instanceof FakeBlock) {
                    cache.addBlocks((FakeBlock) area);
                    continue;
                }
                cache.addArea(area);
            }
        }
    }

    public static Set<FakeBlock> getFakeBlocksForChunk(Player player, int x, int z) {
        return fakeBlockCache.get(player.getUniqueId()).getBlocks(x, z);
    }

    public static Set<FakeBlock> getFakeBlocksForChunks(Player player, int[] x, int[] z) {
        return fakeBlockCache.get(player.getUniqueId()).getBlocks(x, z);
    }

    private static void initBreakSounds() {
        for (net.minecraft.server.v1_11_R1.Block block : net.minecraft.server.v1_11_R1.Block.REGISTRY) {
            try {
                Field effectField = net.minecraft.server.v1_11_R1.Block.class.getDeclaredField("stepSound");
                effectField.setAccessible(true);
                SoundEffectType effectType = (SoundEffectType) effectField.get((Object) block);
                breakSounds.put(CraftMagicNumbers.getMaterial((net.minecraft.server.v1_11_R1.Block) block), effectType);
            } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException ex) {
                Logger.getLogger(FakeBlockHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    static {
        breakSounds = new HashMap();
        FakeBlockHandler.initBreakSounds();
    }
}