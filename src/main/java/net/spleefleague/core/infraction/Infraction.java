/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.spleefleague.core.infraction;

import java.util.UUID;
import net.spleefleague.core.io.DBEntity;
import net.spleefleague.core.io.DBLoad;
import net.spleefleague.core.io.DBLoadable;
import net.spleefleague.core.io.DBSave;
import net.spleefleague.core.io.DBSaveable;
import net.spleefleague.core.utils.TypeConverter;

/**
 *
 * @author Manuel
 */
public class Infraction extends DBEntity implements DBLoadable, DBSaveable {
    private UUID uuid;
    private InfractionType type;
    private long time;
    private long duration;
    private String message;
    
    public Infraction(UUID uuid, InfractionType type, long time, long duration, String message){
        this.uuid = uuid;
        this.type = type;
        this.time = time;
        this.duration = duration;
        this.message = message;
    }
    
    public Infraction(){
    
    }
    
    @DBLoad(fieldName = "uuid", typeConverter = TypeConverter.UUIDStringConverter.class)
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }
    
    @DBLoad(fieldName = "type")
    public void setType(InfractionType type){
        this.type = type;
    }
    
    @DBLoad(fieldName = "time")
    public void setTime(long time){
        this.time = time;
    }
    
    @DBLoad(fieldName = "duration")
    public void setDuration(long duration){
        this.duration = duration;
    }
    
    @DBLoad(fieldName = "message")
    public void setMessage(String message){
        this.message = message;
    }
    
    @DBSave(fieldName = "uuid", typeConverter = TypeConverter.UUIDStringConverter.class)
    public UUID getUUID(){
        return uuid;
    }
    
    @DBSave(fieldName = "type")
    public InfractionType getType(){
        return type;
    }
    
    @DBSave(fieldName = "time")
    public long getTime(){
        return time;
    }
    
    @DBSave(fieldName = "duration")
    public long getDuration(){
        return duration;
    }
    
    @DBSave(fieldName = "message")
    public String getMessage(){
        return message;
    }
}