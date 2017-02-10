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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_11_R1.PacketPlayOutMapChunk;
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
            if (x == 3 && z == 12/*verified.size() > 0*/) {
                try {
                    Field arrayField = packet.getClass().getDeclaredField("d");
                    arrayField.setAccessible(true);
                    byte[] bytes = (byte[]) arrayField.get(packet);
                    Field bitmaskField = packet.getClass().getDeclaredField("c");
                    bitmaskField.setAccessible(true);
                    int bitmask = bitmaskField.getInt(packet);
                    ChunkSection[] sections = splitToChunkSections(bitmask, bytes, world.getEnvironment() == Environment.NORMAL);
                    //insertFakeBlocks(sections, verified, world.getEnvironment() == Environment.NORMAL);
                    for(int i : verified.keySet()) {
                    //    bitmask |= 1 << i;
                    }
                    int i = (int) (Math.random()* 100);
                    System.out.println("===");
                    //System.out.println(i + " Raw:\n" + Arrays.toString(bytes));
                    byte[] data = toByteArray(sections);
                    //System.out.println(i + " Modified\n" + Arrays.toString(data));
                    splitToChunkSections(bitmask, data, world.getEnvironment() == Environment.NORMAL);
                    arrayField.set(packet, data);
                    bitmaskField.set(packet, bitmask);
                } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | IOException ex) {
                    Logger.getLogger(ChunkPacketUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private static byte[] toByteArray(ChunkSection[] sections) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (ChunkSection section : sections) {
            if(section != null) {
                writeChunkSectionData(baos, section);
            }
        }
        return baos.toByteArray();
    }

    private static void writeChunkSectionData(ByteArrayOutputStream baos, ChunkSection section) throws IOException {
        Set<BlockData> used = section.getContainedBlocks();
        BlockPalette palette;
        if (used == null) {
            palette = BlockPalette.GLOBAL;
        } else {
            palette = BlockPalette.createPalette(used.toArray(new BlockData[used.size()]));
        }
        for(BlockData d : used) {
            //System.out.println(d.getType() + " (" + d.getDamage() + ")");
        }
        //System.out.println("====");
        for(BlockData d : BlockPalette.createPalette(palette.getPaletteData(), palette.getBitsPerBlock()).getBlocks()) {
            //System.out.println(d.getType() + " (" + d.getDamage() + ")");
        }
        //System.out.println("New: ");
        byte bpb = (byte) palette.getBitsPerBlock();
        //System.out.println("BPB: " + bpb);
        int paletteLength = palette.getLength();
        //System.out.println("Palette length: " + paletteLength);
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
            ChunkSection section;
            if (sections[id] != null) {
                section = sections[id];
            } else {
                section = sections[id] = new ChunkSection(overworld);
            }
            for (FakeBlock block : e.getValue()) {
                BlockData data = new BlockData(block.getType(), block.getDamageValue());
                int relX = ((block.getX() % 16) + 16) % 16; //Actual positive modulo, in java % means remainder
                int relZ = ((block.getZ() % 16) + 16) % 16;
                section.setBlockRelative(data, relX, block.getY() % 16, relZ);
            }
        }
    }

    private static ChunkSection[] splitToChunkSections(int bitmask, byte[] data, boolean isOverworld) {
        int skylightLength = isOverworld ? 2048 : 0;
        ChunkSection[] sections = new ChunkSection[16];
        //System.out.println(Integer.toBinaryString(bitmask));
        ByteBuffer buffer = ByteBuffer.wrap(data);
        ByteBufferReader bbr = new ByteBufferReader(buffer);
        byte index = 0;
        for (int i = 0; i < 16; i++) {
            if ((bitmask & 0x8000 >> i) != 0) {
                //System.out.println(Arrays.toString(data));
                short bpb = (short) Byte.toUnsignedInt(buffer.get());
                int paletteLength = bbr.readVarInt();
                BlockPalette palette;
                //System.out.println("PaletteLength: " + paletteLength);
                //System.out.println("BPB: " + bpb);
                if (paletteLength != 0 || bpb < 9) {
                    int[] paletteData = new int[paletteLength];
                    for (int j = 0; j < paletteLength; j++) {
                        paletteData[j] = bbr.readVarInt();
                    }
                    //System.out.println(Arrays.toString(paletteData));
                    palette = BlockPalette.createPalette(paletteData, bpb);
                } else {
                    palette = BlockPalette.GLOBAL;
                }
                int dataLength = bbr.readVarInt();
                //System.out.println("DataLength: " + dataLength);
                byte[] blockData = new byte[dataLength * 8];
                buffer.get(blockData);
                //System.out.println("Block data:");
                //System.out.println(Arrays.toString(blockData));
                byte[] lightingData = new byte[2048 + skylightLength];
                buffer.get(lightingData);
                sections[index++] = new ChunkSection(blockData, lightingData, palette);
                StringBuilder sb = new StringBuilder();
                for(BlockData b : sections[index - 1].getBlockData()) {
                    sb.append(b.getType().getId()).append(", ");
                }
                System.out.println(sb.toString());
                //buffer.position(buffer.position() + 2048 + skylightLength);
            }
        }
        return sections;
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
