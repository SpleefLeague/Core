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
import com.spleefleague.core.io.DBSaveable;
import com.spleefleague.core.io.EntityBuilder;
import com.spleefleague.core.io.TypeConverter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.Location;

/**
 *
 * @author Jonas
 */
public class Candy extends DBEntity implements DBLoadable, DBSaveable {
    
    @DBLoad(fieldName = "location", typeConverter = TypeConverter.LocationConverter.class)
    private Location location;
    
    public Location getLocation() {
        return location;
    }
    
    private static Candy[] candies;
    
    public static void init() {
        ArrayList<Candy> candies = new ArrayList<>();
        MongoCursor<Document> dbc = SpleefLeague.getInstance().getPluginDB().getCollection("Candy").find().iterator();
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
    
    public static class CandyObjectIdConverter extends TypeConverter<List<ObjectId>, HashSet<Candy>> {

        @Override
        public HashSet<Candy> convertLoad(List<ObjectId> t) {
            HashSet<Candy> set = new HashSet<>();
            for(ObjectId _id : t) {
                set.add(Candy.getCandy(_id));
            }
            return set;
        }

        @Override
        public List<ObjectId> convertSave(HashSet<Candy> v) {
            List<ObjectId> list = new ArrayList<>();
            for(Candy c : v) {
                list.add(c.getObjectId());
            }
            return list;
        }
    }
}