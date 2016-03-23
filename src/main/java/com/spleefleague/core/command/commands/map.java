/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.command.commands;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitTask;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Jonas
 */
public class map extends BasicCommand {

    private HashMap<Short, MapView> maps = new HashMap<>();

    public map(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.DEVELOPER);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                try {
                    MapView mv = Bukkit.getServer().createMap(Bukkit.getWorlds().get(0));
                    for (MapRenderer mr : mv.getRenderers()) {
                        mv.removeRenderer(mr);
                    }
                    URL url = new URL(args[1]);
                    if (args[1].toLowerCase().endsWith(".gif")) {
                        mv.addRenderer(new GIFMapBase(url));
                    } else {
                        mv.addRenderer(new MapRenderer() {
                            Image image;

                            {
                                try {
                                    image = ImageIO.read(url).getScaledInstance(128, 128, 0);
                                } catch (IOException ex) {
                                    image = null;
                                    error(p, "Invalid URL!");
                                    Logger.getLogger(map.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            @Override
                            public void render(MapView map, MapCanvas canvas, Player player) {
                                if (image != null) {
                                    canvas.drawImage(0, 0, image);
                                } else {
                                    map.removeRenderer(this);
                                }
                            }
                        });
                    }
                    maps.put(mv.getId(), mv);
                    ItemStack mapItem = new ItemStack(Material.MAP, 1, mv.getId());
                    p.getInventory().addItem(mapItem);
                } catch (MalformedURLException ex) {
                    error(p, "Invalid URL!");
                }
            } else if (args[0].equalsIgnoreCase("destroy")) {
                try {
                    short s = Short.parseShort(args[1]);
                    MapView mv = maps.get(s);
                    if (mv != null) {
                        for (MapRenderer mr : mv.getRenderers()) {
                            if (mr instanceof GIFMapBase) {
                                ((GIFMapBase) mr).stop();
                                mv.removeRenderer(mr);
                            }
                        }
                    } else {
                        error(p, s + " is not a valid map ID.");
                    }
                } catch (Exception e) {
                    error(p, args[1] + " is not a valid map ID.");
                }
            } else {
                sendUsage(p);
            }
        } else {
            sendUsage(p);
        }
    }

    public static class GIFMapBase extends MapRenderer {

        private int picture = 0;
        private ArrayList<BufferedImage> frames;
        private HashMap<UUID, Boolean> redrawFlags = new HashMap<>();
        private BukkitTask task;
        String[] attributes = new String[]{
            "imageLeftPosition",
            "imageTopPosition",
            "imageWidth",
            "imageHeight"
        };

        public GIFMapBase(URL url) {
            frames = scaleDown(getFrames(url));
            task = Bukkit.getScheduler().runTaskTimer(SpleefLeague.getInstance(), new Runnable() {
                @Override
                public void run() {
                    picture++;
                    if (picture == frames.size()) {
                        picture = 0;
                    }
                }
            }, 0, 2);
        }

        public void stop() {
            task.cancel();
        }

        private ArrayList<BufferedImage> getFrames(URL url) {
            ImageReader reader = null;
            ImageInputStream inputStream = null;
            ArrayList<BufferedImage> localFrames = new ArrayList<>();
            try {
                reader = ImageIO.getImageReadersBySuffix("Gif").next();
                inputStream = ImageIO.createImageInputStream(url.openStream());
                reader.setInput(inputStream);
                BufferedImage master = null;
                for (int i = 0; i < reader.getNumImages(true); i++) {
                    BufferedImage image = reader.read(i);
                    IIOMetadata meta = reader.getImageMetadata(i);
                    Node nodeTree = meta.getAsTree("javax_imageio_gif_image_1.0");
                    NodeList treeChildren = nodeTree.getChildNodes();
                    for (int j = 0; j < treeChildren.getLength(); j++) {
                        Node nodeItem = treeChildren.item(j);
                        if (nodeItem.getNodeName().equals("ImageDescriptor")) {
                            Map<String, Integer> imageAttributes = new HashMap<String, Integer>();
                            for (int k = 0; k < attributes.length; k++) {
                                NamedNodeMap att = nodeItem.getAttributes();
                                Node attNode = att.getNamedItem(attributes[k]);
                                imageAttributes.put(attributes[k], Integer.valueOf(attNode.getNodeValue()));
                            }
                            if (i == 0) {
                                master = new BufferedImage(imageAttributes.get("imageWidth"), imageAttributes.get("imageHeight"), BufferedImage.TYPE_INT_ARGB);
                            }
                            master.getGraphics().drawImage(image, imageAttributes.get("imageLeftPosition"), imageAttributes.get("imageTopPosition"), null);
                        }
                    }
                    BufferedImage b = new BufferedImage(master.getWidth(), master.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics g = b.getGraphics();
                    g.drawImage(master, 0, 0, null);
                    g.dispose();
                    localFrames.add(b);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException ex) {
                        Logger.getLogger(map.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (reader != null) {
                    reader.dispose();
                }
            }
            return localFrames;
        }

        private ArrayList<BufferedImage> scaleDown(ArrayList<BufferedImage> images) {
            ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();
            for (BufferedImage image : images) {
                frames.add(MapPalette.resizeImage(image));
            }
            return frames;
        }

        @Override
        public void render(MapView map, MapCanvas canvas, Player player) {
            if (!redrawFlags.containsKey(player.getUniqueId())) {
                redrawFlags.put(player.getUniqueId(), true);
            }
            if (redrawFlags.get(player.getUniqueId())) {
                canvas.drawImage(0, 0, frames.get(picture));
                redrawFlags.put(player.getUniqueId(), false);
                player.sendMap(map);
            } else {
                redrawFlags.put(player.getUniqueId(), true);
            }
        }
    }
}
