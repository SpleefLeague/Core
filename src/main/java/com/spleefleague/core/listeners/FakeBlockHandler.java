/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.listeners;

import com.comphenix.packetwrapper.WrapperPlayClientBlockDig;
import com.comphenix.packetwrapper.WrapperPlayClientBlockPlace;
import com.comphenix.packetwrapper.WrapperPlayServerMapChunk;
import com.comphenix.packetwrapper.WrapperPlayServerMapChunkBulk;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.events.FakeBlockBreakEvent;
import com.spleefleague.core.utils.ChunkPacketUtil;
import com.spleefleague.core.utils.FakeArea;
import com.spleefleague.core.utils.FakeBlock;
import com.spleefleague.core.utils.MultiBlockChangeUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk.ChunkMap;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldEvent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftSound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author Jonas
 */
public class FakeBlockHandler implements Listener {

    private PacketAdapter chunk, chunkBulk, breakController, placeController;
    
    private FakeBlockHandler() {
        initPacketListeners();
        Bukkit.getOnlinePlayers().stream().filter((player) -> (!fakeAreas.containsKey(player.getUniqueId()))).forEach((player) -> {
            fakeAreas.put(player.getUniqueId(), new HashSet<>());
        });
    }

    public static void stop() {
        if (instance != null) {
            manager.removePacketListener(instance.chunk);
            manager.removePacketListener(instance.chunkBulk);
            manager.removePacketListener(instance.breakController);
            manager.removePacketListener(instance.placeController);
            HandlerList.unregisterAll(instance);
            instance = null;
        }
    }

    public static void init() {
        if (instance == null) {
            instance = new FakeBlockHandler();
            Bukkit.getPluginManager().registerEvents(instance, SpleefLeague.getInstance());
        }
    }

    private void initPacketListeners() {
        chunk = new PacketAdapter(SpleefLeague.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.MAP_CHUNK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {

            }

            @Override
            public void onPacketSending(PacketEvent event) {
                Bukkit.getScheduler().runTask(SpleefLeague.getInstance(), () -> {
                    WrapperPlayServerMapChunk wpsmc = new WrapperPlayServerMapChunk(event.getPacket());
                    Chunk chunk = event.getPlayer().getWorld().getChunkAt(wpsmc.getChunkX(), wpsmc.getChunkZ());
                    if (((ChunkMap) wpsmc.getChunkMap()).b == 0 && wpsmc.getGroundUpContinuous()) {
                        MultiBlockChangeUtil.removeChunk(event.getPlayer(), chunk);
                    }
                    else {
                        MultiBlockChangeUtil.addChunk(event.getPlayer(), chunk);
                    }
                });
                Set<FakeBlock> blocks = getFakeBlocks(event.getPlayer());
                if (blocks != null) {
                    ChunkPacketUtil.setBlocksPacketMapChunk(event.getPacket(), blocks);
                }
            }
        };
        chunkBulk = new PacketAdapter(SpleefLeague.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.MAP_CHUNK_BULK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {

            }

            @Override
            public void onPacketSending(PacketEvent event) {
                Bukkit.getScheduler().runTask(SpleefLeague.getInstance(), () -> {
                    WrapperPlayServerMapChunkBulk wpsmc = new WrapperPlayServerMapChunkBulk(event.getPacket());
                    for (int i = 0; i < wpsmc.getChunksX().length; i++) {
                        Chunk chunk = event.getPlayer().getWorld().getChunkAt(wpsmc.getChunksX()[i], wpsmc.getChunksZ()[i]);
                        if (((ChunkMap) wpsmc.getChunks()[i]).b == 0) {
                            MultiBlockChangeUtil.removeChunk(event.getPlayer(), chunk);
                        }
                        else {
                            MultiBlockChangeUtil.addChunk(event.getPlayer(), chunk);
                        }
                    }
                });
                Set<FakeBlock> blocks = getFakeBlocks(event.getPlayer());
                if (blocks != null) {
                    ChunkPacketUtil.setBlocksPacketMapChunk(event.getPacket(), blocks);
                }
            }
        };
        breakController = new PacketAdapter(SpleefLeague.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                WrapperPlayClientBlockDig wrapper = new WrapperPlayClientBlockDig(event.getPacket());
                if (event.getPlayer().getLocation().multiply(0).add(wrapper.getLocation().toVector()).getBlock().getType() == Material.AIR) {//Avoiding async world access
                    if (wrapper.getStatus() == EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK || wrapper.getStatus() == EnumWrappers.PlayerDigType.START_DESTROY_BLOCK) {
                        Location loc = wrapper.getLocation().toVector().toLocation(event.getPlayer().getWorld());
                        FakeBlock broken = null;
                        Set<FakeBlock> blocks = getFakeBlocks(event.getPlayer());
                        if (blocks != null) {
                            for (FakeBlock block : blocks) {
                                if (blockEqual(loc, block.getLocation())) {
                                    broken = block;
                                    break;
                                }
                            }
                        }
                        if (broken != null) {
                            if (wrapper.getStatus() == EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK || (broken.getType() == Material.SNOW_BLOCK && event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() == Material.DIAMOND_SPADE) || event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                                FakeBlockBreakEvent fbbe = new FakeBlockBreakEvent(broken, event.getPlayer());
                                Bukkit.getPluginManager().callEvent(fbbe);
                                if (fbbe.isCancelled()) {
                                    event.getPlayer().sendBlockChange(fbbe.getBlock().getLocation(), fbbe.getBlock().getType(), (byte) 0);
                                }
                                else {
                                    broken.setType(Material.AIR);
                                    for(Player subscriber : getSubscribers(broken)) {
                                        if(subscriber != event.getPlayer()) {
                                            subscriber.sendBlockChange(fbbe.getBlock().getLocation(), Material.AIR, (byte) 0);
                                            sendBreakParticles(subscriber, broken);
                                            sendBreakSound(subscriber, broken);
                                        }
                                    }
                                }
                            }
                            else {
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }

            @Override
            public void onPacketSending(PacketEvent event) {

            }
        };
        placeController = new PacketAdapter(SpleefLeague.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_PLACE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                WrapperPlayClientBlockPlace wrapper = new WrapperPlayClientBlockPlace(event.getPacket());
                Location loc = wrapper.getLocation().toVector().toLocation(event.getPlayer().getWorld());
                Set<FakeBlock> fakeBlocks = getFakeBlocks(event.getPlayer());
                if(fakeBlocks != null) {
                    for (FakeBlock fakeBlock : fakeBlocks) {
                        if (blockEqual(fakeBlock.getLocation(), loc)) {
                            event.setCancelled(true);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onPacketSending(PacketEvent event) {

            }
        };
        manager.addPacketListener(chunk);
        manager.addPacketListener(chunkBulk);
        manager.addPacketListener(breakController);
        manager.addPacketListener(placeController);
    }
    
    private void sendBreakParticles(Player p, FakeBlock block) {
        PacketPlayOutWorldEvent packet = new PacketPlayOutWorldEvent(2001, new BlockPosition(block.getX(), block.getY(), block.getZ()), 80, false);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }

    private void sendBreakSound(Player p, FakeBlock b) {
        p.playSound(b.getLocation(), breakSounds.get(b.getType()), 1, 0.9f);
    }

    private boolean blockEqual(Location loc1, Location loc2) {
        if ((loc1.getX() + 0.5) / 1 == (loc2.getX() + 0.5) / 1) {
            if ((loc1.getZ() + 0.5) / 1 == (loc2.getZ() + 0.5) / 1) {
                if (loc1.getY() / 1 == loc2.getY() / 1) {
                    return true;
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(!fakeAreas.containsKey(event.getPlayer().getUniqueId())) {
            fakeAreas.put(event.getPlayer().getUniqueId(), new HashSet<>());
        }
    }

    private static final ProtocolManager manager;
    private static final Map<UUID, Set<FakeArea>> fakeAreas;
    private static FakeBlockHandler instance;
    private static final HashMap<Material, Sound> breakSounds;
    
    public static void addArea(FakeArea area, Player... players) {
        addArea(area, true, players);
    }

    public static void addArea(FakeArea area, boolean update, Player... players) {
        for (Player player : players) {
            fakeAreas.get(player.getUniqueId()).add(area);
        }
        if (update) {
            MultiBlockChangeUtil.changeBlocks(area.getBlocks().toArray(new FakeBlock[0]), players);
        }
    }

    public static void removeArea(FakeArea area, Player... players) {
        removeArea(area, true, players);
    }

    public static void removeArea(FakeArea area, boolean update, Player... players) {
        for (Player player : players) {
            fakeAreas.get(player.getUniqueId()).remove(area);
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
    
    public static Player[] getSubscribers(FakeBlock block) {
        Collection<Player> players = new ArrayList<>();
        for(Entry<UUID, Set<FakeArea>> entry : fakeAreas.entrySet()) {
            for(FakeArea f : entry.getValue()) {
                if(f.getBlocks().contains(block)) {
                    players.add(Bukkit.getPlayer(entry.getKey()));
                    break;
                }
            }
        }
        return players.toArray(new Player[players.size()]);
    }

    public static void addBlock(FakeBlock block, Player... players) {
        addBlock(block, true, players);
    }

    public static void addBlock(FakeBlock block, boolean update, Player... players) {
        for (Player player : players) {
            fakeAreas.get(player.getUniqueId()).add(block);
            if (update) {
                player.sendBlockChange(block.getLocation(), block.getType(), (byte) 0);
            }
        }
    }

    public static void removeBlock(FakeBlock block, Player... players) {
        removeBlock(block, true, players);
    }

    public static void removeBlock(FakeBlock block, boolean update, Player... players) {
        for (Player player : players) {
            fakeAreas.get(player.getUniqueId()).remove(block);
            player.sendBlockChange(block.getLocation(), Material.AIR, (byte) 0);
        }
    }

    public static void update(Player... players) {
        for (Player player : players) {
            Set<FakeBlock> fakeBlocks = getFakeBlocks(player);
            if(fakeBlocks != null) {
                MultiBlockChangeUtil.changeBlocks(fakeBlocks.toArray(new FakeBlock[fakeBlocks.size()]), player);
            }
        }
    }

    public static void update(FakeArea area) {
        Collection<Player> players = new ArrayList<>();
        for (Entry<UUID, Set<FakeArea>> entry : fakeAreas.entrySet()) {
            if (entry.getValue().contains(area)) {
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null) {
                    players.add(player);
                }
            }
        }
        MultiBlockChangeUtil.changeBlocks(area.getBlocks().toArray(new FakeBlock[0]), players.toArray(new Player[players.size()]));
    }

    public static Set<FakeBlock> getFakeBlocks(Player player) {
        if (fakeAreas.get(player.getUniqueId()).isEmpty()) {
            return null;
        }
        else {
            Set<FakeBlock> blocks = new HashSet<>();
            for (FakeArea area : fakeAreas.get(player.getUniqueId())) {
                blocks.addAll(area.getBlocks());
            }
            return blocks;
        }
    }
    
    private static void initBreakSounds() {
        for(net.minecraft.server.v1_8_R3.Block block : net.minecraft.server.v1_8_R3.Block.REGISTRY) {
            String breaksound = block.stepSound.getBreakSound();
            CraftMagicNumbers.getMaterial(block);
            breakSounds.put(CraftMagicNumbers.getMaterial(block), toSound(breaksound));
        }
    }
    
    private static Sound toSound(String mcname) {
        for(Sound s : Sound.values()) {
            if(CraftSound.getSound(s).equals(mcname)) {
                return s;
            }
        }
        return null;
    }

    static {
        manager = ProtocolLibrary.getProtocolManager();
        fakeAreas = new HashMap<>();
        breakSounds = new HashMap();
        initBreakSounds();
    }
}