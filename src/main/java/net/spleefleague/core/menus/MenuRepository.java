/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.menus;

import com.mongodb.BasicDBObject;
import java.util.Collection;
import net.spleefleague.core.chat.ChatChannel;
import net.spleefleague.core.chat.ChatManager;
import net.spleefleague.core.player.GeneralPlayer;
import net.spleefleague.core.player.SLPlayer;
import net.spleefleague.core.plugin.CorePlugin;
import net.spleefleague.core.queue.GameQueue;
import net.spleefleague.core.queue.Queue;
import net.spleefleague.core.utils.menu.MenuClickListener;
import net.spleefleague.core.utils.menu.MenuItem;
import net.spleefleague.core.utils.menu.SubMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Jonas
 */
public class MenuRepository {

    public static ItemStack getSLMenuItem() {
        ItemStack is = new ItemStack(Material.GHAST_TEAR);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.GREEN + "Options");
        is.setItemMeta(im);
        return is;
    }
    
    public static void showMenu(final SLPlayer slp) {
        final Player p = slp.getPlayer();
        SubMenu slmenu = new SubMenu("SL Menu", p, null, getSLMenuItem()); 
        {
            //Spleef - Start
            SubMenu spleef = new SubMenu("Spleef", p, slmenu, Material.DIAMOND_SPADE); 
            {
                CorePlugin spleefPlugin = null;
                for(CorePlugin cp : CorePlugin.getAll()) {
                    if(cp.getName().equals("SuperSpleef")) {
                        spleefPlugin = cp;
                    }
                }
                //Queues
                final SubMenu queues = new SubMenu("Queues", p, spleef, Material.SIGN);
                final GameQueue<? extends GeneralPlayer, ? extends Queue> gameQueue = (GameQueue<? extends GeneralPlayer, ? extends Queue>)GameQueue.getQueueFromPlugin(spleefPlugin);
                for(Queue q : gameQueue.getQueues()) {
                    boolean general = q == null;
                    if(general) {
                        q = gameQueue.getGeneralQueue();
                    }
                    final Queue queue = q;
                    MenuItem item = new MenuItem(queue.getName(), general ? Material.DIAMOND_SPADE : Material.MAP);
                    if(queue.isQueued(slp)) {
                        item.setLore(new String[] {
                            ChatColor.GRAY + "Position: " + ChatColor.GOLD + queue.getQueuePosition(slp) + ChatColor.GRAY + "/" + queue.getQueueLength(),
                            queue.getCurrentState() == null ? "" : queue.getCurrentState()
                        });
                    }
                    else {
                        item.setLore(new String[] {
                            ChatColor.GRAY + "Queued: " + ChatColor.GOLD + queue.getQueueLength(),
                            queue.getCurrentState() == null ? "" : queue.getCurrentState()
                        });
                    }
                    item.setListener(new MenuClickListener() {
                        @Override
                        public void onClick(InventoryClickEvent event, MenuItem item) {
                            if(queue.isQueued(slp)) {
                                    gameQueue.dequeue(p);
                            }
                            else {
                                gameQueue.dequeue(p);
                                gameQueue.queue(p, queue);
                            }
                            queues.update();
                        }
                    });
                    queues.addItem(item);
                }
            }
            slmenu.addSubMenu(spleef);
            //Spleef - End
            //SuperJump - Start
            SubMenu jump = new SubMenu("SuperJump", p, slmenu, Material.LEATHER_BOOTS);
            {
                CorePlugin jumpPlugin = null;
                for(CorePlugin cp : CorePlugin.getAll()) {
                    if(cp.getName().equals("SuperJump")) {
                        jumpPlugin = cp;
                    }
                }
                //Queues
                final SubMenu queues = new SubMenu("Queues", p, jump, Material.SIGN);
                final GameQueue<? extends GeneralPlayer, ? extends Queue> gameQueue = (GameQueue<? extends GeneralPlayer, ? extends Queue>)GameQueue.getQueueFromPlugin(jumpPlugin);
                for(Queue q : gameQueue.getQueues()) {
                    boolean general = q == null;
                    if(general) {
                        q = gameQueue.getGeneralQueue();
                    }
                    final Queue queue = q;
                    MenuItem item = new MenuItem(queue.getName(), general ? Material.LEATHER_BOOTS : Material.MAP);
                    if(queue.isQueued(slp)) {
                        item.setLore(new String[] {
                            ChatColor.GRAY + "Position: " + ChatColor.GOLD + queue.getQueuePosition(slp) + ChatColor.GRAY + "/" + queue.getQueueLength(),
                            queue.getCurrentState() == null ? "" : queue.getCurrentState()
                        });
                    }
                    else {
                        item.setLore(new String[] {
                            ChatColor.GRAY + "Queued: " + ChatColor.GOLD + queue.getQueueLength(),
                            queue.getCurrentState() == null ? "" : queue.getCurrentState()
                        });
                    }
                    item.setListener(new MenuClickListener() {
                        @Override
                        public void onClick(InventoryClickEvent event, MenuItem item) {
                            if(queue.isQueued(slp)) {
                                    gameQueue.dequeue(p);
                            }
                            else {
                                gameQueue.dequeue(p);
                                gameQueue.queue(p, queue);
                            }
                            queues.update();
                        }
                    });
                    queues.addItem(item);
                }
            }
            slmenu.addSubMenu(jump);
            //SuperJump - End
            //Options - Start
            SubMenu options = new SubMenu("Options", p, slmenu, Material.BOOK_AND_QUILL);
            {
                //Chat
                SubMenu chat = new SubMenu("Chat Options", p, options, Material.PAPER);
                Collection<ChatChannel> available = ChatManager.getAvailableChatChannels(slp);
                    //Game
                    final SubMenu games = new SubMenu("Game Messages", p, chat, Material.DIAMOND_SPADE);
                    for(final ChatChannel channel : available) {
                        if(channel.getName().startsWith("GAME_MESSAGE")) {
                            final String word = slp.isInChatChannel(channel.getName()) ? "disable" : "enable";
                            games.addItem(new MenuItem(channel.getDisplayName(), Material.PAPER).setLore(new String[]{ChatColor.GRAY + "Click to " + word + " the " + ChatColor.GREEN + channel.getDisplayName() + "."}).setListener(new MenuClickListener() {
                                @Override
                                public void onClick(InventoryClickEvent event, MenuItem item) {
                                    if(word.equals("disable")) {
                                        slp.removeChatChannel(channel.getName());
                                        item.setLore(item.getLore()[0].replace("disable", "enable"));
                                    }
                                    else {
                                        slp.addChatChannel(channel.getName());
                                        item.setLore(item.getLore()[0].replace("enable", "disable"));
                                    }
                                    games.update();
                                }
                            }));
                        }
                    }
                    chat.addSubMenu(games);
                options.addSubMenu(chat);
            }
            slmenu.addSubMenu(options);    
            //Options - End
        }
        slmenu.show();
    }
}
