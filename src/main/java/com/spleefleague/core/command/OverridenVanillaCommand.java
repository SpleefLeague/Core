package com.spleefleague.core.command;

import com.spleefleague.core.plugin.CorePlugin;
import java.util.List;
import org.bukkit.command.Command;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public final class OverridenVanillaCommand {

    final String name;
    final List<String> aliases;
    final Command bukkitCommand;
    
    OverridenVanillaCommand(CorePlugin plugin, String name, List<String> aliases) {
        this.name = name;
        this.aliases = aliases;
        this.bukkitCommand = plugin.getCommand(name);
    }
    
}
