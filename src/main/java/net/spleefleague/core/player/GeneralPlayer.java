/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.player;

import java.util.UUID;
import net.spleefleague.core.io.DBEntity;
import net.spleefleague.core.io.DBLoad;
import net.spleefleague.core.io.DBLoadable;
import net.spleefleague.core.io.DBSave;
import net.spleefleague.core.io.DBSaveable;
import net.spleefleague.core.io.TypeConverter.UUIDStringConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public abstract class GeneralPlayer extends DBEntity implements DBLoadable, DBSaveable {
    
    @DBLoad(fieldName = "username", priority = Integer.MAX_VALUE)
    private String username;
    @DBLoad(fieldName = "uuid", typeConverter = UUIDStringConverter.class, priority = Integer.MAX_VALUE)
    private UUID uuid;
    
    public GeneralPlayer() {
        
    }
    
    @DBSave(fieldName = "uuid", typeConverter = UUIDStringConverter.class)
    public UUID getUUID() {
        return uuid;
    }
    
    @DBSave(fieldName = "username")
    public String getName() {
        return username;
    }
    
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
    
    protected void setName(String username) {
        this.username = username;
    }
    
    protected void setUUID(UUID uuid) {
        this.uuid = uuid;
    }
    
    public void sendMessage(String message) {
        getPlayer().sendMessage(message);
    }
    
    public void setDefaults() {
        
    }
}
