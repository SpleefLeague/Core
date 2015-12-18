package com.spleefleague.core.utils;

import com.comphenix.packetwrapper.WrapperPlayServerMapChunk;
import com.comphenix.packetwrapper.WrapperPlayServerMapChunkBulk;
import com.comphenix.protocol.events.PacketContainer;
import com.spleefleague.core.listeners.FakeBlockHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk.ChunkMap;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunkBulk;

/**
 *
 * @author Jonas
 */
public class ChunkPacketUtil {

    public static byte[] before, after;
    public static boolean bulk;
    
    public static void setBlocksPacketMapChunk(PacketContainer packet, FakeBlock... unverified) {
        if (packet.getHandle() instanceof PacketPlayOutMapChunk) {
            WrapperPlayServerMapChunk wpsmc = new WrapperPlayServerMapChunk(packet);
            ChunkMap map = (ChunkMap) wpsmc.getChunkMap();
            int x = wpsmc.getChunkX();
            int z = wpsmc.getChunkZ();
            Map<Integer, Collection<FakeBlock>> verified = toVerifiedSectionMap(unverified, x, z);
            if (verified.size() > 0) {
                modify(map, verified);
            }
        }
        else if (packet.getHandle() instanceof PacketPlayOutMapChunkBulk) {
            WrapperPlayServerMapChunkBulk wpsmc = new WrapperPlayServerMapChunkBulk(packet);
            int[] x = wpsmc.getChunksX();
            int[] z = wpsmc.getChunksZ();
            for (int i = 0; i < x.length && i < z.length; i++) {
                Map<Integer, Collection<FakeBlock>> verified = toVerifiedSectionMap(unverified, x[i], z[i]);
                if (verified.size() > 0) {
                    ChunkMap map = (ChunkMap) wpsmc.getChunks()[i];
                    modify(map, verified);
                }
            }
        }
    }

    public static void setBlocksPacketMapChunk(PacketContainer packet, Collection<FakeBlock> unverified) {
        if (packet.getHandle() instanceof PacketPlayOutMapChunk) {
            WrapperPlayServerMapChunk wpsmc = new WrapperPlayServerMapChunk(packet);
            ChunkMap map = (ChunkMap) wpsmc.getChunkMap();
            int x = wpsmc.getChunkX();
            int z = wpsmc.getChunkZ();
            Map<Integer, Collection<FakeBlock>> verified = toVerifiedSectionMap(unverified, x, z);
            if (verified.size() > 0) {
                if(x == -47 && z == -5) {
                    if(before == null) {
                        before = map.a;
                        bulk = false;
                    }
                }
                modify(map, verified);
                if(x == -47 && z == -5) {
                    if(after == null) after = map.a;
                }
            }
        }
        else if (packet.getHandle() instanceof PacketPlayOutMapChunkBulk) {
            WrapperPlayServerMapChunkBulk wpsmc = new WrapperPlayServerMapChunkBulk(packet);
            int[] x = wpsmc.getChunksX();
            int[] z = wpsmc.getChunksZ();
            for (int i = 0; i < x.length && i < z.length; i++) {
                Map<Integer, Collection<FakeBlock>> verified = toVerifiedSectionMap(unverified, x[i], z[i]);
                if (verified.size() > 0) {
                    ChunkMap map = (ChunkMap) wpsmc.getChunks()[i];
                    if(x[i] == -47 && z[i] == -5) {
                        if(before == null) {
                            before = map.a;
                            bulk = true;
                        }
                    }
                    modify(map, verified);
                    if(x[i] == -47 && z[i] == -5) {
                        if(after == null) after = map.a;
                    }
                }
            }
        }
    }

    private static Map<Integer, Collection<FakeBlock>> toVerifiedSectionMap(FakeBlock[] unverified, int x, int z) {
        Map<Integer, Collection<FakeBlock>> verified = new HashMap<>();
        for (FakeBlock fb : unverified) {
            if (fb.getChunk().getX() == x && fb.getChunk().getZ() == z) {
                int section = fb.getY() / 16;
                Collection<FakeBlock> blocks;
                if (!verified.containsKey(section)) {
                    blocks = new ArrayList<>();
                    verified.put(section, blocks);
                }
                else {
                    blocks = verified.get(section);
                }
                blocks.add(fb);
            }
        }
        return verified;
    }

    private static Map<Integer, Collection<FakeBlock>> toVerifiedSectionMap(Collection<FakeBlock> unverified, int x, int z) {
        Map<Integer, Collection<FakeBlock>> verified = new HashMap<>();
        for (FakeBlock fb : unverified) {
            if (fb.getChunk().getX() == x && fb.getChunk().getZ() == z) {
                int section = fb.getY() / 16;
                Collection<FakeBlock> blocks;
                if (!verified.containsKey(section)) {
                    blocks = new ArrayList<>();
                    verified.put(section, blocks);
                }
                else {
                    blocks = verified.get(section);
                }
                blocks.add(fb);
            }
        }
        return verified;
    }

    private static void modify(ChunkMap map, Map<Integer, Collection<FakeBlock>> verified) {
        int changedSections = createBitmask(verified.keySet());
        int newSections = changedSections & ~map.b;
        int fullBitmask = map.b | changedSections;
        boolean biome = map.a.length % 10240 != 0; //Is biome data sent?
        boolean skylight = map.a.length > setBits(map.b) * 10240 + (biome ? 256 : 0); //Is skylight data sent?
        //Adding new sections (if necessary)
        if (newSections != 0) {
            byte[] newBytes = new byte[map.a.length + (10240 + (skylight ? 2048 : 0)) * setBits(newSections)];
            System.arraycopy(map.a, 0, newBytes, 0, map.a.length);
            Arrays.fill(newBytes, 8192 * setBits(fullBitmask), newBytes.length, (byte)255); //Filling light data with light value 16 in case I don't set it so it's not dark
            int sectionStart = 0;
            int lightStart = setBits(fullBitmask) * 8192;
            int skylightStart = lightStart + setBits(fullBitmask) * 2048;
            for (int section = 0; section < 16; section++) {
                if ((fullBitmask & 1 << section) != 0) {
                    if ((newSections & 1 << section) != 0) {
                        System.arraycopy(newBytes, sectionStart, newBytes, sectionStart + 8192, newBytes.length - (sectionStart + 8192));
                        Arrays.fill(newBytes, sectionStart, sectionStart + 8192, (byte)0);
                        System.arraycopy(newBytes, lightStart, newBytes, lightStart + 2048, newBytes.length - (lightStart + 2048));
                        Arrays.fill(newBytes, lightStart, lightStart + 2048, (byte)0);
                        if (skylight) {
                            System.arraycopy(newBytes, skylightStart, newBytes, skylightStart + 2048, newBytes.length - (skylightStart + 2048));
                            Arrays.fill(newBytes, skylightStart, skylightStart + 2048, (byte)0);
                        }
                    }
                    sectionStart += 8192;
                    lightStart += 2048;
                    skylightStart += 2048;
                }
            }
            map.a = newBytes;
        }
        //Writing blocks to section
        int sstart = 0;
        for (int s = 0; s < 16; s++) {
            if ((fullBitmask & 1 << s) != 0) {
                if ((changedSections & 1 << s) != 0) {
                    for (FakeBlock fb : verified.get(s)) {
                        char blockId = (char) (fb.getType().getId() << 4);
                        int index = sstart + ((fb.getX() & 15) + 16 * ((fb.getZ() & 15) + 16 * (fb.getY() & 15))) * 2;
                        map.a[index] = (byte) (blockId & 255);
                        map.a[index + 1] = (byte) (blockId >> 8 & 255);
                    }
                }
                sstart += 8192;
            }
        }
        map.b = fullBitmask;
    }

    private static int setBits(int i) {
        return Integer.bitCount(i);
    }

    private static int createBitmask(Collection<Integer> sections) {
        int i = 0;
        for (int section : sections) {
            i |= 1 << section;
        }
        return i;
    }
}
