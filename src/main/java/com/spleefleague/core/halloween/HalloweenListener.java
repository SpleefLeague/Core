/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.halloween;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.io.Settings;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.Area;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Jonas
 */
public class HalloweenListener implements Listener{
    
    private static Listener instance;
    private final Area portalArea;
    private final Location portalTarget;
    private final PotionEffect positive, negative;
    
    public static void init() {
        if(instance == null) {
            instance = new HalloweenListener();
            Bukkit.getPluginManager().registerEvents(instance, SpleefLeague.getInstance());
        }
    }
    
    private HalloweenListener() {
        portalArea = Settings.get("halloween_portal_area", Area.class);
        portalTarget = Settings.getLocation("halloween_portal_target");
        positive = new PotionEffect(PotionEffectType.SPEED, 20 * 15, 1);
        negative = new PotionEffect(PotionEffectType.SLOW, 20 * 15, 1);
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getPlayer().getName().equals("Joba") && (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.SKULL)) {
            for(Candy candy : Candy.getCandies()) {
                if(candy.getLocation().equals(event.getClickedBlock().getLocation())) {
                    SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(event.getPlayer());
                    if(!slp.getCandy().contains(candy)) {
                        slp.getCandy().add(candy);
                        slp.sendMessage(Theme.SUCCESS + "" + slp.getCandy().size() + "/" + Candy.getCandies().length);
                        if(Math.random() < 0.5) {
                            event.getPlayer().addPotionEffect(positive);
                        }
                        else {
                            event.getPlayer().addPotionEffect(negative);
                        }
                        event.getPlayer().getWorld().playEffect(candy.getLocation(), Effect.SPELL, 0);
                    }
                    break;
                }
            }
        }
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(event.getPlayer());
        if(slp != null) {
            if(slp.getCandy().size() >= Candy.getCandies().length && portalArea.isInArea(event.getTo())) {
                event.getPlayer().teleport(portalTarget);
            }
        }
    }
}
