package com.spleefleague.core.chat;


import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.player.SLPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class Communicator implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message)
    {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String type = in.readUTF();
        String content;
        if (type.equals("StaffAlerts"))
        {
            short len = in.readShort();
            byte[] msgbytes = new byte[len];
            in.readFully(msgbytes);

            DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
            String rank = null;
            content = null;
            Long time = null;
            try
            {
                rank = msgin.readUTF();
                content = msgin.readUTF();
                time = Long.valueOf(msgin.readLong());
            }
            catch (IOException ex)
            {
                Logger.getLogger(Communicator.class.getName()).log(Level.SEVERE, null, ex);
            }
            for (Player pl : Bukkit.getOnlinePlayers()) {
                if (SpleefLeague.getInstance().getPlayerManager().get(pl).hasPermission("mod")) {
                    pl.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "Staff" + ChatColor.GRAY + "]" + ChatColor.GRAY + player.getName() + ": " + ChatColor.GREEN + message);
                }
            }
        }
    }


    public static void sendMessage(ChatChannel channel, String content, Player sender)
            throws IOException
    {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF("ONLINE");
        out.writeUTF("Alert");

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);

        msgout.writeUTF(channel.toString());
        msgout.writeUTF(content);
        Long i = Long.valueOf(System.currentTimeMillis());
        msgout.writeLong(i.longValue());

        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());

        sender.sendPluginMessage(SpleefLeague.getInstance(), "BungeeCord", out.toByteArray());

        if (channel == ChatChannel.STAFF) {
            Bukkit.getScheduler().runTask(SpleefLeague.getInstance(), () -> {
                Bukkit.getConsoleSender().sendMessage(content);
                SpleefLeague.getInstance().getPlayerManager().getAll().stream().filter((slp) -> (slp.isInChatChannel(channel))).forEach((slp) -> {
                    slp.sendMessage(content);
                });
            });
        }
    }
}