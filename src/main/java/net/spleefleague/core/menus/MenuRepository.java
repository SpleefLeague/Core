/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.menus;

import java.util.Collection;
import net.spleefleague.core.chat.ChatChannel;
import net.spleefleague.core.chat.ChatManager;
import net.spleefleague.core.player.SLPlayer;
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

    public static ItemStack getOptionsMenuItem() {
        ItemStack is = new ItemStack(Material.GHAST_TEAR);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.GREEN + "Options");
        is.setItemMeta(im);
        return is;
    }
    
    public static void showOptionsMenu(final SLPlayer slp) {
        Player p = slp.getPlayer();
        Collection<ChatChannel> available = ChatManager.getAvailableChatChannels(slp);
        SubMenu options = new SubMenu("Options", p, null, getOptionsMenuItem());
            //Chat
            SubMenu chat = new SubMenu("Chat Options", p, options, Material.PAPER);
                //Game
                final SubMenu games = new SubMenu("Game Messages", p, chat, Material.DIAMOND_SPADE);
                for(final ChatChannel channel : available) {
                    if(channel.getName().startsWith("GAME_MESSAGE")) {
                        final String word = slp.isInChatChannel(channel.getName()) ? "disable" : "enable";
                        games.addItem(new MenuItem(channel.getDisplayName(), Material.PAPER).setLore(new String[]{ChatColor.GRAY + "Click to " + word + " the " + ChatColor.GREEN + channel.getDisplayName() + "."}).setListener(new MenuClickListener() {
                            @Override
                            public void onClick(InventoryClickEvent event, MenuItem item) {
                                System.out.println(channel.getName());
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
        options.show();
    }
}
