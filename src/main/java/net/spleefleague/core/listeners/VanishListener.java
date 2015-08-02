package net.spleefleague.core.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.events.GeneralPlayerLoadedEvent;
import net.spleefleague.core.player.Rank;
import net.spleefleague.core.player.SLPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.kitteh.vanish.VanishPerms;
import org.kitteh.vanish.VanishUser;

import java.util.*;

/**
 * Created by Zed on 02/08/2015.
 */
public class VanishListener implements Listener {

    private static Listener instance;

    public static void init() {
        if(instance == null) {
            instance = new VanishListener();
            Bukkit.getPluginManager().registerEvents(instance, SpleefLeague.getInstance());
        }
    }

    private VanishListener(){}

    private Map<UUID, PermissionAttachment> vanishersAttachmentMap = new HashMap<>();
    private Set<UUID> vanishers = new HashSet<>();

    private Map<UUID, PermissionAttachment> silentJoinersAttachmentMap = new HashMap<>();
    private Set<UUID> silentJoiners = new HashSet<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(final PlayerJoinEvent event){
        if(vanishers.contains(event.getPlayer().getUniqueId())) giveVanishPerms(event.getPlayer());
        if(silentJoiners.contains(event.getPlayer().getUniqueId())) {
            giveSilentJoinPerms(event.getPlayer());
            Bukkit.getScheduler().runTaskLater(SpleefLeague.getInstance(), new Runnable() {
                @Override
                public void run() {
                    event.getPlayer().spigot().sendMessage(
                            new ComponentBuilder("\n\n+").color(ChatColor.DARK_GRAY).append("------ ").color(ChatColor.GRAY).append("Vanished!").color(ChatColor.AQUA).append(" ------").color(ChatColor.GRAY).append("+").color(ChatColor.DARK_GRAY)
                                    .append("\n").reset().append("You have joined vanished. No Join message was shown.").color(ChatColor.BLUE)
                                    .append("\n").reset().append("Your chat messages won't be sent, you won't appear in tab and players can't see you").color(ChatColor.YELLOW)
                                    .append("\n").reset().append("Use ").color(ChatColor.GRAY)
                                    .append("/vanish").color(ChatColor.GREEN)
                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vanish"))
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to run /vanish").color(ChatColor.AQUA).create()))
                                    .append(" to unvanish and show a join message\n\n").color(ChatColor.GRAY)
                                    .create());
                }
            }, 5*20L);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        if(vanishersAttachmentMap.containsKey(event.getPlayer().getUniqueId())) vanishersAttachmentMap.remove(event.getPlayer().getUniqueId());
        if(silentJoinersAttachmentMap.containsKey(event.getPlayer().getUniqueId())) silentJoinersAttachmentMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onGPLoad(GeneralPlayerLoadedEvent event){
        if(vanishers.contains(event.getPlayer().getUniqueId()) || silentJoiners.contains(event.getPlayer().getUniqueId())) return;
        if(event.getGeneralPlayer() instanceof SLPlayer){
            SLPlayer slPlayer = (SLPlayer) event.getGeneralPlayer();
            if(slPlayer.getRank().hasPermission(Rank.MODERATOR)){
                vanishers.add(slPlayer.getUUID());
                silentJoiners.add(slPlayer.getUUID());
                giveVanishPerms(event.getPlayer());
                giveSilentJoinPerms(event.getPlayer());
                VanishPerms.userQuit(event.getPlayer()); //Make sure there are no cached perms lingering around at VanishNoPacket.
                slPlayer.getPlayer().spigot().sendMessage(new ComponentBuilder("You didn't join silently as this is the first time you joined after the last restart or reload.").color(ChatColor.RED)
                        .append("\n").reset()
                        .append("The next time you join the server, it will be silent").color(ChatColor.YELLOW).create());
            }
        }
    }

    private void giveVanishPerms(Player player){
        PermissionAttachment vanisherAttachment = player.addAttachment(SpleefLeague.getInstance());
        vanisherAttachment.setPermission("vanish.standard", true);
        vanisherAttachment.setPermission("vanish.nochat", true);
        vanishersAttachmentMap.put(player.getUniqueId(), vanisherAttachment);
    }

    private void giveSilentJoinPerms(Player player){
        PermissionAttachment silentJoinAttachment = player.addAttachment(SpleefLeague.getInstance());
        silentJoinAttachment.setPermission("vanish.silentjoin", true);
        silentJoinersAttachmentMap.put(player.getUniqueId(), silentJoinAttachment);
    }
}
