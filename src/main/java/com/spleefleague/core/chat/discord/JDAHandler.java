/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.chat.discord;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.chat.ChatManager;
import com.spleefleague.core.events.ChatChannelMessageEvent;
import com.spleefleague.core.io.Config;
import com.spleefleague.core.player.SLPlayer;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Jonas
 */
public class JDAHandler extends ListenerAdapter implements Listener {
    
    private JDA jda;
    private boolean running = false;
    private final String token;
    private TextChannel channel;
    private LoadingCache<String, SLPlayer> discordUsers;
    
    public JDAHandler(String token) {
        this.token = token;
        Config.getString("server_name");
        try {
            this.jda = start();
            discordUsers = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(
                new CacheLoader<String, SLPlayer>() {
                    @Override
                    public SLPlayer load(String k) {
                        return SpleefLeague.getInstance().getPlayerManager().loadFake(new Document("discordUser", k));
                    } 
                }
            );
            Optional<TextChannel> optional = jda.getTextChannelsByName(Config.getString("server_name"), false).stream().findAny();
            if(optional.isPresent()) {
                channel = optional.get();
                jda.addEventListener(this);
                Bukkit.getPluginManager().registerEvents(this, SpleefLeague.getInstance());
            }
        } catch (LoginException | IllegalArgumentException | InterruptedException | RateLimitedException ex) {
            Logger.getLogger(JDAHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @EventHandler
    public void onMessageSent(ChatChannelMessageEvent event) {
        if(running && channel != null) {
            SLPlayer sender = event.getSender();
            if(sender != null) {
                channel.sendMessage(sender.getName() + " >> " + event.getMessage());
            }
        }
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        event.getMessage().getStrippedContent();
        SLPlayer slp = discordUsers.getUnchecked(event.getAuthor().getName());
        if(slp != null) {
            String prefix = "";
            if (!slp.getRank().getDisplayName().equals("Default")) {
                prefix = ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + slp.getRank().getDisplayName() + ChatColor.DARK_GRAY + "] ";
            }
            ChatChannel channel = slp.getSendingChannel();
            ChatManager.sendMessage(ChatColor.DARK_GRAY + "<" + prefix + slp.getRank().getColor() + slp.getName() + ChatColor.DARK_GRAY + ">" + ChatColor.RESET, event.getMessage().getStrippedContent(), channel);
        }
    }
    
    @Override
    public void onReady(ReadyEvent event) {
        running = true;
        SpleefLeague.getInstance().log("Discord connection ready");
    }
    
    @Override
    public void onDisconnect(DisconnectEvent event) {
        running = false;
        SpleefLeague.getInstance().log("Discord connection disconnected");
    }
    
    public JDA getJDA() {
        return jda;
    }
    
    public void flushCache() {
        discordUsers.invalidateAll();
    }
    
    private JDA start() throws RateLimitedException, LoginException, IllegalArgumentException, InterruptedException {
        if(!isRunning()) {
            JDA jda = new JDABuilder(AccountType.BOT).setToken(token).buildAsync();
            jda.setAutoReconnect(true);
            return jda;
        }
        return jda;
    }
    
    public boolean isRunning() {
        return running;
    }
}
