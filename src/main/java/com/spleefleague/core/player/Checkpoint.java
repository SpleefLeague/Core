/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.player;

import com.spleefleague.core.io.typeconverters.LocationConverter;
import com.spleefleague.entitybuilder.DBEntity;
import com.spleefleague.entitybuilder.DBLoad;
import com.spleefleague.entitybuilder.DBLoadable;
import com.spleefleague.entitybuilder.DBSave;
import com.spleefleague.entitybuilder.DBSaveable;
import com.spleefleague.entitybuilder.TypeConverter;
import java.util.Date;
import org.bukkit.Location;

/**
 *
 * @author jonas
 */
public class Checkpoint extends DBEntity implements DBLoadable, DBSaveable{
    
    @DBLoad(fieldName = "location", typeConverter = LocationConverter.class)
    @DBSave(fieldName = "location", typeConverter = LocationConverter.class)
    private Location target;
    @DBLoad(fieldName = "timeout", typeConverter = TypeConverter.DateConverter.class)
    @DBSave(fieldName = "timeout", typeConverter = TypeConverter.DateConverter.class)
    private Date timeout;
    
    public Checkpoint() {
        
    }

    public Checkpoint(Location target, Date timeout) {
        this.target = target;
        this.timeout = timeout;
    }

    public Location getTarget() {
        return target;
    }

    public boolean isValid() {
        return timeout == null || timeout.after(new Date());
    }
}
