/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.halloween;

import com.mongodb.client.MongoCursor;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.io.DBEntity;
import com.spleefleague.core.io.DBLoad;
import com.spleefleague.core.io.DBLoadable;
import com.spleefleague.core.io.EntityBuilder;
import com.spleefleague.core.io.TypeConverter;
import java.util.ArrayList;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.Location;

/**
 *
 * @author Jonas
 */
public class Candy extends DBEntity implements DBLoadable{
    
    @DBLoad(fieldName = "location", typeConverter = TypeConverter.LocationConverter.class)
    private Location location;
    
    public Location getLocation() {
        return location;
    }
    
    private static Candy[] candies;
    
    public static void init() {
        ArrayList<Candy> candies = new ArrayList<>();
        MongoCursor<Document> dbc = SpleefLeague.getInstance().getMongo().getDatabase("Halloween").getCollection("Candy").find().iterator();
        while(dbc.hasNext()) {
            candies.add(EntityBuilder.load(dbc.next(), Candy.class));
        }
        Candy.candies = candies.toArray(new Candy[0]);
    }
    
    public static Candy getCandy(ObjectId _id) {
        for(Candy candy : getCandies()) {
            if(candy.getObjectId().equals(_id)) {
                return candy;
            }
        }
        return null;
    }
    
    public static Candy[] getCandies() {
        return candies;
    }
    
    public static class CandyObjectIdConverter extends TypeConverter<ObjectId, Candy> {

        @Override
        public Candy convertLoad(ObjectId t) {
            return Candy.getCandy(t);
        }

        @Override
        public ObjectId convertSave(Candy v) {
            return v.getObjectId();
        }
        
    }
}
