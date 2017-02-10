package com.spleefleague.core.utils.fakeentity;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.utils.Task;
import com.spleefleague.core.utils.UtilChat;
import com.spleefleague.core.utils.UtilReflect;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class FakeCreaturesWorker implements Listener {

    private final static Map<Player, Integer> working = new HashMap<>();
    private final static Set<Player> pending = new HashSet<>();
    private final static Map<Player, Object> lastObjects = new HashMap<>();
    
    static void removeWorkingWith(FakeCreature creature) {
        int id = creature.getId();
        working.values().removeAll(Collections.singleton(id));
    }
    
    public static boolean isWorking(Player p) {
        return working.containsKey(p);
    }
    
    public static boolean isPending(Player p) {
        return pending.contains(p);
    }
    
    public static Integer getWorkingWithId(Player p) {
        return working.get(p);
    }
    
    public static FakeCreature getWorkingWith(Player p) {
        Integer id = getWorkingWithId(p);
        if(id == null)
            return null;
        return FakeEntitiesManager.getCreature(id);
    }
    
    public static void addPending(Player p) {
        pending.add(p);
    }
    
    static void addWorking(Player p, int id) {
        pending.remove(p);
        working.put(p, id);
    }
    
    public static void removeWorking(Player p) {
        working.remove(p);
        pending.remove(p);
        lastObjects.remove(p);
    }
    
    FakeCreaturesWorker() {
        Bukkit.getPluginManager().registerEvents(this, SpleefLeague.getInstance());
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        removeWorking(p);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String msg = e.getMessage();
        if(isWorking(p)) {
            e.setCancelled(true);
            FakeCreature creature = getWorkingWith(p);
            Location loc = p.getLocation();
            Task.schedule(() -> {
                switch(msg.toLowerCase()) {
                    case "move":
                        creature.setLocation(loc);
                        break;
                    case "head":
                    case "rotation":
                        creature.setRotation(loc.getYaw(), loc.getPitch());
                        break;
                    case "hand":
                    case "weapon":
                        if(!(creature instanceof FakeEquippableCreature)) {
                            UtilChat.s(Theme.ERROR, p, "This action can't be performed to such an entity.");
                            return;
                        }
                        ((FakeEquippableCreature) creature).getEquipment().setMainhand(p.getItemInHand());
                        UtilChat.s(Theme.SUCCESS, p, "Done.");
                        break;
                    case "helm":
                    case "helmet":
                        if(!(creature instanceof FakeEquippableCreature)) {
                            UtilChat.s(Theme.ERROR, p, "This action can't be performed to such an entity.");
                            return;
                        }
                        ((FakeEquippableCreature) creature).getEquipment().setHelmet(p.getItemInHand());
                        UtilChat.s(Theme.SUCCESS, p, "Done.");
                        break;
                    case "chest":
                    case "chestplate":
                        if(!(creature instanceof FakeEquippableCreature)) {
                            UtilChat.s(Theme.ERROR, p, "This action can't be performed to such an entity.");
                            return;
                        }
                        ((FakeEquippableCreature) creature).getEquipment().setChestplate(p.getItemInHand());
                        UtilChat.s(Theme.SUCCESS, p, "Done.");
                        break;
                    case "leg":
                    case "legs":
                    case "leggings":
                        if(!(creature instanceof FakeEquippableCreature)) {
                            UtilChat.s(Theme.ERROR, p, "This action can't be performed to such an entity.");
                            return;
                        }
                        ((FakeEquippableCreature) creature).getEquipment().setLeggings(p.getItemInHand());
                        UtilChat.s(Theme.SUCCESS, p, "Done.");
                        break;
                    case "boots":
                        if(!(creature instanceof FakeEquippableCreature)) {
                            UtilChat.s(Theme.ERROR, p, "This action can't be performed to such an entity.");
                            return;
                        }
                        ((FakeEquippableCreature) creature).getEquipment().setBoots(p.getItemInHand());
                        UtilChat.s(Theme.SUCCESS, p, "Done.");
                        break;
                    case "die":
                    case "kill":
                        creature.invalidate();
                        UtilChat.s(Theme.SUCCESS, p, "Creature has been invalidated. You've just quit the setup mode.");
                        break;
                    case "save":
                        if(!(creature instanceof FakeNpc)) {
                            UtilChat.s(Theme.ERROR, p, "This action can be performed only to childs of FakeNpc class.");
                            return;
                        }
                        ((FakeNpc) creature).saveToConfig();
                        UtilChat.s(Theme.SUCCESS, p, "Done.");
                        break;
                    case "delete":
                    case "remove":
                        if(!(creature instanceof FakeNpc)) {
                            UtilChat.s(Theme.ERROR, p, "This action can be performed only to childs of FakeNpc class.");
                            return;
                        }
                        ((FakeNpc) creature).deleteFromConfig();
                        UtilChat.s(Theme.SUCCESS, p, "Done.");
                        break;
                    default:
                        String[] args = msg.split(" ");
                        boolean memory = false;
                        if(args[0].endsWith("*")) {
                            memory = true;
                            args[0] = args[0].replace("*", "");
                        }
                        switch(args[0].toLowerCase()) {
                            case "setfield": {
                                if(args.length != 3)
                                    return;
                                String field = args[1];
                                try {
                                    UtilReflect.setFieldRecursively(memory ? lastObjects.get(p) : creature, field, args[2]);
                                    UtilChat.s(Theme.SUCCESS, p, "Value of the field has been updated.");
                                }catch(Exception ex) {
                                    UtilChat.s(Theme.ERROR, p, "This action can't be performed.");
                                }
                                break;
                            }case "getfield": {
                                if(args.length != 2)
                                    return;
                                String field = args[1];
                                try {
                                    UtilChat.s(Theme.INFO, p, "Value of the field %s: &a%s&e.", field, UtilReflect.getFieldRecursively(memory ? lastObjects.get(p) : creature, field));
                                }catch(Exception ex) {
                                    UtilChat.s(Theme.ERROR, p, "This action can't be performed.");
                                }
                                break;
                            }case "memfield":
                            case "mfield": {
                                if(args.length != 2)
                                    return;
                                String field = args[1];
                                try {
                                    Object value = UtilReflect.getFieldRecursively(memory ? lastObjects.get(p) : creature, field);
                                    lastObjects.put(p, value);
                                    UtilChat.s(Theme.SUCCESS, p, "Value of the field %s: (&a%s&e) has been saved.", field, value);
                                }catch(Exception ex) {
                                    UtilChat.s(Theme.ERROR, p, "This action can't be performed.");
                                }
                                break;
                            }case "invoke":
                            case "method": {
                                if(args.length < 2)
                                    return;
                                String method = args[1];
                                String[] strings = new String[args.length - 2];
                                for(int i = 0; i < strings.length; ++i)
                                    strings[i] = args[i + 2];
                                try {
                                    UtilReflect.invokeMethodWithStringArgsRecursively(memory ? lastObjects.get(p) : creature, method, strings);
                                    UtilChat.s(Theme.SUCCESS, p, "Method has been successfully invoked.");
                                }catch(Exception ex) {
                                    UtilChat.s(Theme.ERROR, p, "This action can't be performed.");
                                }
                                break;
                            }case "exinvoke":
                            case "exmethod": {
                                if(args.length < 2)
                                    return;
                                String method = args[1];
                                String[] strings = new String[args.length - 2];
                                for(int i = 0; i < strings.length; ++i)
                                    strings[i] = args[i + 2];
                                try {
                                    UtilReflect.invokeMethodWithStringArgsRecursively(memory ? lastObjects.get(p) : creature, method, strings);
                                    UtilChat.s(Theme.SUCCESS, p, "Method has been successfully invoked.");
                                }catch(Exception ex) {
                                    UtilChat.s(Theme.ERROR, p, "This action can't be performed, and exception is in the console.");
                                    ex.printStackTrace();
                                }
                                break;
                            }case "meminvoke":
                            case "minvoke": {
                                if(args.length < 2)
                                    return;
                                String method = args[1];
                                String[] strings = new String[args.length - 2];
                                for(int i = 0; i < strings.length; ++i)
                                    strings[i] = args[i + 2];
                                try {
                                    lastObjects.put(p, UtilReflect.invokeMethodAndGetWithStringArgsRecursively(memory ? lastObjects.get(p) : creature, method, strings));
                                    UtilChat.s(Theme.SUCCESS, p, "Method has been successfully invoked.");
                                }catch(Exception ex) {
                                    UtilChat.s(Theme.ERROR, p, "This action can't be performed.");
                                }
                                break;
                            }case "showinvoke":
                            case "sinvoke": {
                                if(args.length < 2)
                                    return;
                                String method = args[1];
                                String[] strings = new String[args.length - 2];
                                for(int i = 0; i < strings.length; ++i)
                                    strings[i] = args[i + 2];
                                try {
                                    UtilChat.s(Theme.INFO, p, "The value of %s: &a%s&e.", method, UtilReflect.invokeMethodAndGetWithStringArgsRecursively(memory ? lastObjects.get(p) : creature, method, strings).toString());
                                }catch(Exception ex) {
                                    UtilChat.s(Theme.ERROR, p, "This action can't be performed.");
                                }
                                break;
                            }case "listfields": {
                                List<String> fields = UtilReflect.listFieldsRecursively(memory ? lastObjects.get(p) : creature);
                                UtilChat.s(Theme.INFO, p, "List of fields:");
                                fields.forEach(f -> UtilChat.s(p, "&e%s", f));
                                break;
                            }case "listmethods": {
                                List<String> fields = UtilReflect.listMethodsRecursively(memory ? lastObjects.get(p) : creature);
                                UtilChat.s(Theme.INFO, p, "List of methods:");
                                fields.forEach(f -> UtilChat.s(p, "&e%s", f));
                                break;
                            }default:
                                UtilChat.s(Theme.ERROR, p, "Unknown action.");
                                break;
                        }
                        break;
                }
            });
        }
    }
    
}