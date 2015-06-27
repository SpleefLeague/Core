/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.io;

import org.bson.types.ObjectId;

/**
 *
 * @author Jonas
 */
public abstract class DBEntity {
    @DBLoad(fieldName = "_id")
    private ObjectId _id;
    
    public ObjectId getObjectId() {
        return _id;
    }
    
    public void onDone() {
        
    }
}
