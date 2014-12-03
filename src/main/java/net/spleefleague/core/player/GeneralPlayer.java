/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.player;

import java.util.UUID;
import net.spleefleague.core.annotations.DBLoad;
import net.spleefleague.core.annotations.DBSave;
import net.spleefleague.core.utils.TypeConverter.UUIDStringConverter;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public abstract class GeneralPlayer {
    
    private String username;
    private UUID uuid;
    private ObjectId _id;
    
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
    
    @DBLoad(fieldName = "_id")
    public void setObjectId(ObjectId _id) {
        this._id = _id;
    }
    
    public ObjectId getObjectId() {
        return _id;
    }
    
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
    
    @DBLoad(fieldName = "username")
    public void setName(String username) {
        this.username = username;
    }
    
    @DBLoad(fieldName = "uuid", typeConverter = UUIDStringConverter.class)
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }
    
    public void setDefaults() {
        
    }
}
