/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.command;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.SpleefLeague;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.SimplePluginManager;

/**
 *
 * @author Jonas
 */
public class CommandLoader {
    
    private final static String VANILLA = "*VANILLA*";

    private final ConcurrentMap<String, LoadedCommand> loadedCommands = new ConcurrentHashMap<>();
    private final CorePlugin plugin;
    private final String commandPackage;

    private CommandLoader(CorePlugin plugin, String commandPackage) {
        this.plugin = plugin;
        this.commandPackage = commandPackage;
        registerCommands();
    }

    private void registerCommands() {
        PluginDescriptionFile pdf = plugin.getDescription();
        Set<OverridenVanillaCommand> vanillaCmds = new HashSet<>();
        for (String command : pdf.getCommands().keySet()) {
            Map<String, Object> cmd = pdf.getCommands().get(command);
            Object descriptionO = cmd.get("description");
            String description = null;
            if (descriptionO != null) {
                description = (String) descriptionO;
                if(description.startsWith(VANILLA)) {
                    description = description.substring(VANILLA.length());
                    vanillaCmds.add(new OverridenVanillaCommand(plugin, command, (List<String>) cmd.get("aliases")));
                }
            }
            Object usageO = cmd.get("usage");
            String usage = null;
            if (usageO != null) {
                usage = (String) usageO;
            }
            registerCommand(command, description, usage);
        }
        handleVanillaCommands(vanillaCmds);
    }

    private void registerCommand(String command, String description, String usage) {
        try {
            command = command.toLowerCase();
            Class commandExecutorClass = plugin.getClass().getClassLoader().loadClass(commandPackage + "." + command);
            Constructor<BasicCommand> commandExecutorConstructor = commandExecutorClass.getConstructor(CorePlugin.class, String.class, String.class);
            BasicCommand commandExecutor = commandExecutorConstructor.newInstance(plugin, command, usage);
            if (!loadedCommands.containsKey(command)) {
                loadedCommands.put(command, new LoadedCommand(plugin, commandExecutor, command, description, usage));
            } else {
                throw new CommandExistsException(command);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | CommandExistsException e) {
            logCommandLoadError(command, e);
        }
    }
    
    private void handleVanillaCommands(Collection<OverridenVanillaCommand> cmds) {
        SimplePluginManager spm = (SimplePluginManager) Bukkit.getPluginManager();
        SimpleCommandMap commandMap;
        try {
            Field f = spm.getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (SimpleCommandMap) f.get(spm);
            f.setAccessible(false);
            Map<String, Command> knownCommands;
            f = commandMap.getClass().getDeclaredField("knownCommands");
            f.setAccessible(true);
            knownCommands = (Map<String, Command>) f.get(commandMap);
            cmds.forEach(c -> {
                Set<String> set = new HashSet<>();
                set.add(c.name);
                if(c.aliases != null)
                    set.addAll(c.aliases);
                Command cmd = c.bukkitCommand;
                set.forEach(s -> knownCommands.put(s, cmd));
            });
            f.setAccessible(false);
        }catch(Exception ex) {
            SpleefLeague.LOG.log(Level.WARNING, "Can not override vanilla commands!");
        }
    }

    private void logCommandLoadError(String command, Exception e) {
        SpleefLeague.LOG.log(Level.WARNING, "{0} Failed to load command \"{1}\": {2}", new Object[]{SpleefLeague.getInstance().getPrefix(), command, e.getMessage()});
    }

    public LoadedCommand getCommand(String command) {
        return loadedCommands.get(command);
    }

    public static CommandLoader loadCommands(CorePlugin plugin, String commandPackage) {
        return new CommandLoader(plugin, commandPackage);
    }
}
