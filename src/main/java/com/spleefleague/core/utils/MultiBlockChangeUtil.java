/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils;

import com.comphenix.packetwrapper.WrapperPlayServerMultiBlockChange;
import com.comphenix.protocol.wrappers.ChunkCoordIntPair;
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class MultiBlockChangeUtil {

    private static class MultiBlockChangeData {
        
        private ArrayList<MultiBlockChangeInfo> data = new ArrayList<>();
        private final Chunk chunk;

        public MultiBlockChangeData(Chunk chunk) {
            this.chunk = chunk;
        }

        public void addBlock(int x, int y, int z, Material m) {
            x &= 15;
            z &= 15;
            WrappedBlockData bdata = WrappedBlockData.createData(m);
            MultiBlockChangeInfo mbci = new MultiBlockChangeInfo((short)(x << 12 | z << 8 | y), bdata, ChunkCoordIntPair.getConverter().getSpecific(chunk));
            data.add(mbci);
        }

        public MultiBlockChangeInfo[] getData() {
            return data.toArray(new MultiBlockChangeInfo[data.size()]);
        }

        public int getChunkX() {
            return chunk.getX();
        }

        public int getChunkZ() {
            return chunk.getZ();
        }
        
        public Chunk getChunk() {
            return chunk;
        }

        public short getChangedBlocks() {
            return (short)data.size();
        }
    }
    
    private static void sendMultiBlockChange(MultiBlockChangeData mbcd, List<Player> affected) {
        if(affected.isEmpty()) {
            return;
        }
        World world = affected.get(0).getWorld();
        net.minecraft.server.v1_8_R3.Chunk chunk = ((CraftChunk) world.getChunkAt(mbcd.getChunkX(), mbcd.getChunkZ())).getHandle();
        WrapperPlayServerMultiBlockChange wrapper = new WrapperPlayServerMultiBlockChange();
        wrapper.setChunk(new ChunkCoordIntPair(chunk.locX, chunk.locZ));
        wrapper.setRecords(mbcd.getData());
        for(Player player : affected) {
            wrapper.sendPacket(player);
        }
    }
    
    public static void changeBlocks(FakeBlock[] blocks, Player... affected) {
        changeBlocks(blocks, Arrays.asList(affected));
    }
    
    public static void changeBlocks(FakeBlock[] blocks, List<Player> affected) {
        HashMap<Chunk, MultiBlockChangeData> changes = new HashMap<>();
        for (FakeBlock block : blocks) {
            MultiBlockChangeData data;
            if (!changes.containsKey(block.getChunk())) {
                data = new MultiBlockChangeData(block.getChunk());
                changes.put(block.getChunk(), data);
            }
            else {
                data = changes.get(block.getChunk());
            }
            data.addBlock(block.getX(), block.getY(), block.getZ(), block.getType());
        }
        changes.values().stream().forEach((mbcd) -> {
            sendMultiBlockChange(mbcd, affected);
        });
    }
    
    public static void changeBlocks(Block[] blocks, Material to, Player... affected) {
        changeBlocks(blocks, to, Arrays.asList(affected));
    }
    
    public static void changeBlocks(Block[] blocks, Material to, List<Player> affected) {
        HashMap<Chunk, MultiBlockChangeData> changes = new HashMap<>();
        for (Block block : blocks) {
            MultiBlockChangeData data;
            if (!changes.containsKey(block.getChunk())) {
                data = new MultiBlockChangeData(block.getChunk());
                changes.put(block.getChunk(), data);
            }
            else {
                data = changes.get(block.getChunk());
            }
            data.addBlock(block.getX(), block.getY(), block.getZ(), to);
        }
        changes.values().stream().forEach((mbcd) -> {
            sendMultiBlockChange(mbcd, affected);
        });
    }
    
    public static void changeBlocks(Location pos1, Location pos2, Material to, List<Player> affected) {
        changeBlocks(getBlocksInArea(pos1, pos2).toArray(new Block[0]), to,affected);
    }
    
    public static void changeBlocks(Location pos1, Location pos2, Material to, Player... affected) {
        changeBlocks(pos1, pos2, to, affected);
    }
    
    public static void preloadChunks(FakeBlock[] blocks, Player... affected) {
        preloadChunks(blocks, Arrays.asList(affected));
    }
    
    public static void preloadChunks(FakeBlock[] blocks, List<Player> affected) {
        Set<Chunk> chunks = getChunks(blocks);
        PacketPlayOutMapChunk[] packets = new PacketPlayOutMapChunk[chunks.size()];
        int i = 0;
        for(Chunk chunk : chunks) {
            net.minecraft.server.v1_8_R3.Chunk nmsChunk = ((CraftChunk)chunk).getHandle();
            packets[i++] = new PacketPlayOutMapChunk(nmsChunk, true, '\uffff');
        }
        for(PacketPlayOutMapChunk packet : packets) {
            for(Player player : affected) {
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet); 
            }
        }
    }
    
    public static void preloadChunks(Block[] blocks, Player... affected) {
        preloadChunks(blocks, Arrays.asList(affected));
    }
    
    public static void preloadChunks(Block[] blocks, List<Player> affected) {
        Set<Chunk> chunks = getChunks(blocks);
        PacketPlayOutMapChunk[] packets = new PacketPlayOutMapChunk[chunks.size()];
        int i = 0;
        for(Chunk chunk : chunks) {
            net.minecraft.server.v1_8_R3.Chunk nmsChunk = ((CraftChunk)chunk).getHandle();
            packets[i++] = new PacketPlayOutMapChunk(nmsChunk, true, '\uffff');
        }
        for(PacketPlayOutMapChunk packet : packets) {
            for(Player player : affected) {
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet); 
            }
        }
    }
    
    public static void preloadChunks(Location pos1, Location pos2, Player... affected) {
        preloadChunks(pos1, pos2, Arrays.asList(affected));
    }
    
    public static void preloadChunks(Location pos1, Location pos2, List<Player> affected) {
        Set<Chunk> chunks = getChunks(pos1, pos2);
        PacketPlayOutMapChunk[] packets = new PacketPlayOutMapChunk[chunks.size()];
        int i = 0;
        for(Chunk chunk : chunks) {
            net.minecraft.server.v1_8_R3.Chunk nmsChunk = ((CraftChunk)chunk).getHandle();
            packets[i++] = new PacketPlayOutMapChunk(nmsChunk, true, '\uffff');
        }
        for(PacketPlayOutMapChunk packet : packets) {
            for(Player player : affected) {
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet); 
            }
        }
    }
    
    public static Set<Chunk> getChunks(Block[] blocks) {
        Set<Chunk> chunks = new HashSet<>();
        for(Block block : blocks) {
            chunks.add(block.getChunk());
        }
        return chunks;
    }
    
    public static Set<Chunk> getChunks(FakeBlock[] blocks) {
        Set<Chunk> chunks = new HashSet<>();
        for(FakeBlock block : blocks) {
            chunks.add(block.getChunk());
        }
        return chunks;
    }
    
    public static Set<Chunk> getChunks(Location pos1, Location pos2) {
        Set<Chunk> chunks = new HashSet<>();
        Location low = new Location(pos1.getWorld(), Math.min(pos1.getBlockX(), pos2.getBlockX()), Math.min(pos1.getBlockY(), pos2.getBlockY()), Math.min(pos1.getBlockZ(), pos2.getBlockZ()));
        Location high = new Location(pos1.getWorld(), Math.max(pos1.getBlockX(), pos2.getBlockX()), Math.max(pos1.getBlockY(), pos2.getBlockY()), Math.max(pos1.getBlockZ(), pos2.getBlockZ()));
        for (int x = low.getBlockX(); x <= high.getBlockX(); x++) {
            for (int z = low.getBlockZ(); z <= high.getBlockZ(); z++) {
                chunks.add(new Location(low.getWorld(), x, 0, z).getChunk());
            }
        }
        return chunks;
    }
    
    public static HashSet<Block> getBlocksInArea(Location pos1, Location pos2) {
        Location low = new Location(pos1.getWorld(), Math.min(pos1.getBlockX(), pos2.getBlockX()), Math.min(pos1.getBlockY(), pos2.getBlockY()), Math.min(pos1.getBlockZ(), pos2.getBlockZ()));
        Location high = new Location(pos1.getWorld(), Math.max(pos1.getBlockX(), pos2.getBlockX()), Math.max(pos1.getBlockY(), pos2.getBlockY()), Math.max(pos1.getBlockZ(), pos2.getBlockZ()));
        HashSet<Block> blocks = new HashSet<>();
        for (int x = low.getBlockX(); x <= high.getBlockX(); x++) {
            for (int y = low.getBlockY(); y <= high.getBlockY(); y++) {
                for (int z = low.getBlockZ(); z <= high.getBlockZ(); z++) {
                    blocks.add(new Location(pos1.getWorld(), x, y, z).getBlock());
                }
            }
        }
        return blocks;
    }
}
