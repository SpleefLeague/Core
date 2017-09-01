package com.spleefleague.core.utils.fakeblock;

import com.comphenix.packetwrapper.WrapperPlayServerMapChunk;
import com.comphenix.protocol.events.PacketContainer;
import com.spleefleague.core.utils.ByteBufferReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_12_R1.PacketPlayOutMapChunk;
import org.bukkit.World;
import org.bukkit.World.Environment;

/**
 *
 * @author Jonas
 */
public class ChunkPacketUtil {
    
    public static void setBlocksPacketMapChunk(World world, PacketContainer packetContainer, Collection<FakeBlock> unverified) {
        if (packetContainer.getHandle() instanceof PacketPlayOutMapChunk) {
            PacketPlayOutMapChunk packet = (PacketPlayOutMapChunk) packetContainer.getHandle();
            WrapperPlayServerMapChunk wpsmc = new WrapperPlayServerMapChunk(packetContainer);
            int x = wpsmc.getChunkX();
            int z = wpsmc.getChunkZ();
            Map<Integer, Collection<FakeBlock>> verified = toVerifiedSectionMap(unverified, x, z);
            if (verified.size() > 0) {
                try {
                    Field arrayField = packet.getClass().getDeclaredField("d");
                    arrayField.setAccessible(true);
                    byte[] bytes = (byte[]) arrayField.get(packet);
                    Field bitmaskField = packet.getClass().getDeclaredField("c");
                    bitmaskField.setAccessible(true);
                    int bitmask = bitmaskField.getInt(packet);
                    int originalMask = bitmask;
                    for (int i : verified.keySet()) {
                        bitmask |= 1 << i;
                    }
                    ChunkData chunkData = splitToChunkSections(bitmask, originalMask, bytes, world.getEnvironment() == Environment.NORMAL);
                    insertFakeBlocks(chunkData.getSections(), verified, world.getEnvironment() == Environment.NORMAL);

                    byte[] data = toByteArray(chunkData);
                    arrayField.set(packet, data);
                    bitmaskField.set(packet, bitmask);
                } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | IOException e) {
                    Logger.getLogger(ChunkPacketUtil.class.getName()).log(Level.SEVERE, null, e);
                } catch (NullPointerException e) {
                    Logger.getLogger(ChunkPacketUtil.class.getName()).log(Level.SEVERE, null, e);
                    Logger.getLogger(ChunkPacketUtil.class.getName()).log(Level.SEVERE, "Debug info: ({0}|{1})", new Object[]{x, z});
                }
            }
        }
    }

    private static byte[] toByteArray(ChunkData data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (ChunkSection section : data.getSections()) {
            if (section != null) {
                writeChunkSectionData(baos, section);
            }
        }
        baos.write(data.getAdditionalData());
        return baos.toByteArray();
    }

    private static void writeChunkSectionData(ByteArrayOutputStream baos, ChunkSection section) throws IOException {
        BlockData[] used = section.getContainedBlocks();
        BlockPalette palette;
        if (used == null) {
            palette = BlockPalette.GLOBAL;
        } else {
            palette = BlockPalette.createPalette(used);
        }
        byte bpb = (byte) palette.getBitsPerBlock();
        int paletteLength = palette.getLength();
        int[] paletteInfo;
        if (paletteLength == 0) {
            paletteInfo = new int[0];
        } else {
            paletteInfo = palette.getPaletteData();
        }
        byte[] blockdata = palette.encode(section.getBlockData());
        byte[] lightingData = section.getLightingData();
        baos.write(bpb);
        ByteBufferReader.writeVarIntToByteArrayOutputStream(paletteLength, baos);
        for (int p : paletteInfo) {
            ByteBufferReader.writeVarIntToByteArrayOutputStream(p, baos);
        }
        ByteBufferReader.writeVarIntToByteArrayOutputStream(blockdata.length / 8/*it's represented as a long array*/, baos);
        baos.write(blockdata);
        baos.write(lightingData);
    }

    private static void insertFakeBlocks(ChunkSection[] sections, Map<Integer, Collection<FakeBlock>> blocks, boolean overworld) {
        for (Entry<Integer, Collection<FakeBlock>> e : blocks.entrySet()) {
            int id = e.getKey();
            ChunkSection section = sections[id];
            for (FakeBlock block : e.getValue()) {
                BlockData data = new BlockData(block.getType(), block.getDamageValue());
                int relX = ((block.getX() % 16) + 16) % 16; //Actual positive modulo, in java % means remainder
                int relZ = ((block.getZ() % 16) + 16) % 16;
                section.setBlockRelative(data, relX, block.getY() % 16, relZ);
            }
        }
    }

    private static ChunkData splitToChunkSections(int bitmask, int originalMask, byte[] data, boolean isOverworld) {
        int skylightLength = isOverworld ? 2048 : 0;
        ChunkSection[] sections = new ChunkSection[16];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        ByteBufferReader bbr = new ByteBufferReader(buffer);
        for (int i = 0; i < 16; i++) {
            if ((bitmask & 0x8000 >> (15 - i)) != 0) {
                if ((originalMask & 0x8000 >> (15 - i)) != 0) {
                    short bpb = (short) Byte.toUnsignedInt(buffer.get());
                    int paletteLength = bbr.readVarInt();
                    BlockPalette palette;
                    if (paletteLength != 0 || bpb < 9) {
                        int[] paletteData = new int[paletteLength];
                        for (int j = 0; j < paletteLength; j++) {
                            paletteData[j] = bbr.readVarInt();
                        }
                        palette = BlockPalette.createPalette(paletteData, bpb);
                    } else {
                        palette = BlockPalette.GLOBAL;
                    }
                    int dataLength = bbr.readVarInt();
                    byte[] blockData = new byte[dataLength * 8];
                    buffer.get(blockData);
                    byte[] lightingData = new byte[2048 + skylightLength];
                    buffer.get(lightingData);
                    sections[i] = new ChunkSection(blockData, lightingData, palette);
                }
                else {
                    sections[i] = new ChunkSection(isOverworld);
                }
            }
        }
        byte[] additional = new byte[data.length - buffer.position()];
        buffer.get(additional);
        return new ChunkData(sections, additional);
    }

    private static Map<Integer, Collection<FakeBlock>> toVerifiedSectionMap(Collection<FakeBlock> unverified, int x, int z) {
        Map<Integer, Collection<FakeBlock>> verified = new HashMap<>();
        for (FakeBlock fb : unverified) {
            if (fb.getChunkX() == x && fb.getChunkZ() == z) {
                int section = fb.getY() / 16;
                Collection<FakeBlock> blocks;
                if (!verified.containsKey(section)) {
                    blocks = new HashSet<>();
                    verified.put(section, blocks);
                } else {
                    blocks = verified.get(section);
                }
                blocks.add(fb);
            }
        }
        return verified;
    }
}
