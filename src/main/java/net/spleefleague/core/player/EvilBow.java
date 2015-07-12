/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.player;

import net.spleefleague.core.utils.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Jonas
 */
public class EvilBow implements Debugger, Listener{

    @Override
    public void debug() {
        ItemStack is = new ItemStack(Material.BOW);
        is.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.RED + "Bow of Rekness");
        is.setItemMeta(im);
        Bukkit.getPlayer("Joba").getInventory().addItem(is);
    }
    
    @EventHandler
    public void entityDamageEvent(EntityDamageByEntityEvent event) {
        if(event.getCause() == DamageCause.PROJECTILE) {
            if(event.getDamager() instanceof Player) {
                Player player = (Player)event.getDamager();
                if(player.getItemInHand().getType() == Material.BOW && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.RED + "Bow of Rekness")) {
                    player.getWorld().strikeLightning(event.getEntity().getLocation());
                    event.getEntity().remove();
                }
            }
        }
    }
}
