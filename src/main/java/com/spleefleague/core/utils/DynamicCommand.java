package com.spleefleague.core.utils;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface DynamicCommand {
    
    public String getName();
    
    public Usage getUsage();
    
    public void run(Player p, SLPlayer sp, String[] args);
    
    public default Rank getRequiredRank() {
        return Rank.DEVELOPER;
    }
    
    public default boolean canExecute(SLPlayer p) {
        return (p.getRank().hasPermission(this.getRequiredRank()));
    }
    
    public default void error(CommandSender cs, String message) {
        cs.sendMessage(SpleefLeague.getInstance().getChatPrefix() + " " + Theme.ERROR.buildTheme(false) + message);
    }

    public default void success(CommandSender cs, String message) {
        cs.sendMessage(SpleefLeague.getInstance().getChatPrefix() + " " + Theme.SUCCESS.buildTheme(false) + message);
    }

    public default void sendUsage(CommandSender cs) {
        cs.sendMessage(SpleefLeague.getInstance().getChatPrefix() + " " + Theme.ERROR.buildTheme(false) + "Correct Usage: ");
        String[] u = (this.getUsage() == null) ? new String[0] : this.getUsage().getUsageLines();
        for (String m : u) {
            cs.sendMessage(SpleefLeague.getInstance().getChatPrefix() + " " + Theme.INCOGNITO.buildTheme(false) + m);
        }
    }
    
    public static class LoadedDynamicCommand {
        
        private File file;
        private String name;
        private DynamicCommand cmd;
        
        public LoadedDynamicCommand(File file, DynamicCommand cmd, String name) {
            this.file = file;
            this.cmd = cmd;
            this.name = name;
        }
        
        public DynamicCommand getCommand() {
            return this.cmd;
        }
        
        public File getFile() {
            return this.file;
        }
        
        public String getName() {
            return this.name;
        }
        
    }
    
    public static class Usage {
        
        private List<String> usage;
        
        public Usage() {
            this.usage = new ArrayList();
        }
        
        public Usage(String usage) {
            this();
            this.usage.add(usage);
        }
        
        public Usage(String... usageLines) {
            this();
            this.usage.addAll(Arrays.asList(usageLines));
        }
        
        public String[] getUsageLines() {
            return this.usage.toArray(new String[this.usage.size()]);
        }
    }
    
}
