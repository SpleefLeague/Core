/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.player;

import com.spleefleague.entitybuilder.DBEntity;
import com.spleefleague.entitybuilder.DBLoad;
import com.spleefleague.entitybuilder.DBLoadable;
import com.spleefleague.entitybuilder.DBSave;
import com.spleefleague.entitybuilder.DBSaveable;
import java.util.Date;
import org.bson.Document;

/**
 *
 * @author jonas
 */
public class PlayerDataBackup extends DBEntity implements DBSaveable, DBLoadable{
    
    @DBLoad(fieldName = "timestamp")
    @DBSave(fieldName = "timestamp")
    private Date timestamp;
    @DBLoad(fieldName = "data")
    @DBSave(fieldName = "data")
    private Document data;
    
    public PlayerDataBackup() {
        
    }
    
    public PlayerDataBackup(Document data, Date timestamp) {
        this.data = data;
        this.timestamp = timestamp;
    }
}
