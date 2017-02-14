package com.spleefleague.core.command.commands;

import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.io.Closer;
import com.sk89q.worldedit.world.registry.WorldData;
import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.io.Settings;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.FlattenedClipboardTransform;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.io.*;

/**
 * Created by Josh on 10/08/2016.
 */
public class globalschematic extends BasicCommand {

    public globalschematic(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.SENIOR_MODERATOR, Rank.BUILDER);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if(!Settings.getBoolean("global_schematics_enabled").orElse(false) || !Settings.hasKey("global_schematics_save_location")) {
            error(p, "This command is currently disabled!");
            return;
        }
        if(args.length < 1) {
            sendUsage(p);
            return;
        }
        switch (args[0].toLowerCase()) {
            case "save": {
                if(args.length != 2) {
                    sendUsage(p);
                    return;
                }
                if(args[1].contains(".")) {
                    error(p, "Invalid schematic name!");
                    return;
                }

                File saveLocation = new File(Settings.getString("global_schematics_save_location").get());
                File saveFile = new File(saveLocation, args[1] + ".schematic");

                ClipboardFormat format = ClipboardFormat.findByAlias("schematic");

                ClipboardHolder holder;
                try {
                    holder = WorldEdit.getInstance().getSession(p.getName()).getClipboard();
                } catch (EmptyClipboardException e) {
                    error(slp, "You need to copy an area first!");
                    return;
                }
                Clipboard clipboard = holder.getClipboard();
                Transform transform = holder.getTransform();
                Clipboard target;

                // If we have a transform, bake it into the copy
                if (!transform.isIdentity()) {
                    FlattenedClipboardTransform result = FlattenedClipboardTransform.transform(clipboard, transform, holder.getWorldData());
                    target = new BlockArrayClipboard(result.getTransformedRegion());
                    target.setOrigin(clipboard.getOrigin());
                    try {
                        Operations.completeLegacy(result.copyTo(target));
                    } catch (MaxChangedBlocksException e) {
                        error(slp, "Too many blocks changed in one session! Please contact a developer.");
                        return;
                    }
                } else {
                    target = clipboard;
                }

                Closer closer = Closer.create();
                try {
                    FileOutputStream fos = closer.register(new FileOutputStream(saveFile));
                    BufferedOutputStream bos = closer.register(new BufferedOutputStream(fos));
                    ClipboardWriter writer = closer.register(format.getWriter(bos));
                    writer.write(target, holder.getWorldData());
                    success(p, "Schematic saved! You can now load it on any server using /globalschematic load " + args[1] + "!");
                } catch (IOException e) {
                    error(slp, "Unable to write to schematic file - please contact a developer!");
                } finally {
                    try {
                        closer.close();
                    } catch (IOException ignored) {}
                }
                break;
            }
            case "load": {
                if(args.length != 2) {
                    sendUsage(p);
                    return;
                }
                if(args[1].contains(".")) {
                    error(p, "Invalid schematic name!");
                    return;
                }

                File loadLocation = new File(Settings.getString("global_schematics_save_location").get());
                File loadFile = new File(loadLocation, args[1] + ".schematic");

                if(!loadFile.exists()) {
                    error(slp, "That schematic doesn't seem to exist!");
                    return;
                }

                ClipboardFormat format = ClipboardFormat.findByAlias("schematic");

                Closer closer = Closer.create();
                try {
                    FileInputStream fis = closer.register(new FileInputStream(loadFile));
                    BufferedInputStream bis = closer.register(new BufferedInputStream(fis));
                    ClipboardReader reader = format.getReader(bis);

                    WorldData worldData = BukkitUtil.getLocalWorld(p.getWorld()).getWorldData();
                    Clipboard clipboard = reader.read(worldData);
                    WorldEdit.getInstance().getSession(p.getName()).setClipboard(new ClipboardHolder(clipboard, worldData));
                    success(p, "Schematic loaded - you can now paste it using //paste.");
                } catch (IOException | NullPointerException e) {
                    error(p, "The schematic could not be read - please contact a developer!");
                    return;
                } finally {
                    try {
                        closer.close();
                    } catch (IOException ignored) {}
                }
                break;
            }
            case "list": {
                File schematicLocation = new File(File.separator + "home" + File.separator + "schematics" + File.separator);
                if(!schematicLocation.exists() || !schematicLocation.isDirectory() || schematicLocation.listFiles().length == 0) {
                    error(p, "No schematics found!");
                    return;
                }
                p.sendMessage(Theme.INFO.buildTheme(true) + "Global schematics:");
                p.sendMessage(Theme.INFO.buildTheme(true) + StringUtils.join(schematicLocation.list(), ", ").replace(".schematic", "") + ".");
                break;
            }
            default: {
                sendUsage(p);
                break;
            }
        }
    }

}
