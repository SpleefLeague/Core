package com.spleefleague.core.command.commands;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.chat.ChatManager;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.io.Config;
import com.spleefleague.core.io.connections.ConnectionClient;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.StringUtil;
import java.util.UUID;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

/**
 *
 * @author Patrick F.
 */
public class ticket extends BasicCommand {

    public ticket(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.DEFAULT);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if (args.length > 0) {
            String message = StringUtil.fromArgsArray(args);
            ConnectionClient cc = SpleefLeague.getInstance().getConnectionClient();
            if(cc.isEnabled()) {
                JSONObject sendObject = new JSONObject();
                sendObject.put("rankColor", slp.getRank().getColor().name());
                sendObject.put("sendUUID", p.getUniqueId());
                sendObject.put("sendName", p.getName());
                sendObject.put("shownName", p.getName());
                sendObject.put("message", message);
                SpleefLeague.getInstance().getConnectionClient().send("ticket", sendObject);
            }
            else {
                sendTicket(p.getName(), p.getName(), p.getUniqueId(), message, Config.getString("server_name"), slp.getRank().getColor());
            }
        } else {
            sendUsage(p);
        }
    }

    public void sendTicket(String playerName, String shownName, UUID playerUUID, String message, String server, ChatColor chatColor) {
        ChatManager.sendMessage(ChatChannel.STAFF,
                new ComponentBuilder("[").color(ChatColor.DARK_GREEN.asBungee()).append("Ticket")
                        .color(ChatColor.GREEN.asBungee()).append("|").color(ChatColor.DARK_GREEN.asBungee())
                        .append(playerName).color(ChatColor.GREEN.asBungee()).append("] ").color(ChatColor.DARK_GREEN.asBungee())
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join " + server + "!").color(ChatColor.GRAY.asBungee()).create()))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/join " + server))
                        .append(shownName).color(chatColor.asBungee()).append(": ").color(ChatColor.GRAY.asBungee())
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join " + server + "!").color(ChatColor.GRAY.asBungee()).create()))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/join " + server))
                        .append(message).color((server.equalsIgnoreCase(Config.getString("server_name")) ? ChatColor.YELLOW.asBungee() : ChatColor.RED.asBungee()))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Server: " + server)
                                .color(ChatColor.GRAY.asBungee())
                                .append("\n")
                                .append("Click to respond!").color(ChatColor.GRAY.asBungee()).create()))
                        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/treply " + playerName + " ")).create());
        if (Bukkit.getPlayer(playerUUID) != null) {
            Bukkit.getPlayer(playerUUID).sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "Ticket"
                    + ChatColor.DARK_GREEN + "|" + ChatColor.GREEN + playerName + ChatColor.DARK_GREEN + "] "
                    + chatColor + shownName + ChatColor.GRAY + ": " + ChatColor.YELLOW + message);
        }
    }
}
