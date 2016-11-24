package com.spleefleague.core.command.commands;

import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.UtilChat;
import com.spleefleague.core.utils.fakeentity.FakeCreature;
import com.spleefleague.core.utils.fakeentity.FakeCreaturesWorker;
import com.spleefleague.core.utils.fakeentity.FakeNpc;
import java.lang.reflect.Method;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class fakeentity extends BasicCommand {

    public fakeentity(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.DEVELOPER);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if(args.length == 0) {
            if(FakeCreaturesWorker.isPending(p) || FakeCreaturesWorker.isWorking(p)) {
                FakeCreaturesWorker.removeWorking(p);
                UtilChat.s(Theme.SUCCESS, p, "You are no longer working with fake creatures.");
            }else {
                FakeCreaturesWorker.addPending(p);
                UtilChat.s(Theme.SUCCESS, p, "Now you're working with fake creatures. Right click a fake entity to begin.");
            }
        }else switch(args[0].toLowerCase()) {
            case "spawn": {
                if(args.length < 4) {
                    UtilChat.s(Theme.ERROR, p, "Correct usage: &b/fe spawn <class> <random/null/*exact* (uuid for npcs)> <name>&c.");
                    return;
                }
                String class_name = args[1].contains(".") ? args[1] : "com.spleefleague.core.utils.fakeentity.exact." + args[1];
                String suid = args[2].toLowerCase();
                UUID uuid;
                switch (suid) {
                    case "null":
                        uuid = null;
                        break;
                    case "random":
                        uuid = UUID.randomUUID();
                        break;
                    default:
                        try {
                            uuid = UUID.fromString(suid);
                        }catch(Exception ex) {
                            UtilChat.s(Theme.ERROR, p, "Wrong uuid!");
                            return;
                        }
                        break;
                }
                String name = args[3].replace("_", " ");
                if(name.length() > 16) {
                    UtilChat.s(Theme.ERROR, p, "This name is too far long.");
                    return;
                }
                if(args.length > 4 && args.length != 5) {
                    UtilChat.s(Theme.ERROR, p, "Correct usage: &b/fe spawn <class> <random/null (uuid for npcs)> <name>&c.");
                    return;
                }
                try {
                    Class clazz = Class.forName(class_name);
                    FakeCreature creature;
                    if(FakeNpc.class.isAssignableFrom(clazz)) {
                        creature = (FakeCreature) clazz.getDeclaredConstructor(UUID.class, String.class, Location.class)
                            .newInstance(uuid, name, p.getLocation());
                    }else {
                        creature = (FakeCreature) clazz.getDeclaredConstructor(Location.class).newInstance(p.getLocation());
                        try {
                            Method m = clazz.getDeclaredMethod("setName", String.class);
                            m.setAccessible(true);
                            m.invoke(creature, name);
                            m.setAccessible(false);
                        }catch(Exception ex) {} //This creature can't have custom name.
                    }
                    creature.spawn();
                    UtilChat.s(Theme.SUCCESS, p, "Fake creature has been spawned.");
                }catch(Exception ex) {
                    UtilChat.s(Theme.ERROR, p, "Unknown fake creature type class.");
                }
                break;
            }default: {
                UtilChat.s(Theme.ERROR, p, "Unknown subcommand.");
                break;
            }
        }
    }
    
}
