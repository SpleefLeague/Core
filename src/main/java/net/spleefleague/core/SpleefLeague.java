/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jonas
 */
public class SpleefLeague extends CorePlugin {
    
    private MongoClient mongo;

    public SpleefLeague() {
        super("SpleefLeague");
    }
    
    @Override
    public void onEnable() {
        instance = this;
        Config.loadConfig();
        try {
            mongo = new MongoClient(Config.DB_HOST, Config.DB_PORT);
            this.mongo.getMongoOptions().autoConnectRetry = true;
            this.mongo.getMongoOptions().connectionsPerHost = 10;
        } catch (Exception ex) {
            Logger.getLogger(SpleefLeague.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void onDisable() {
        mongo.close();
    }
    
    @Override
    public DB getPluginDB() {
        return mongo.getDB("SpleefLeague");
    }
    
    private static SpleefLeague instance;
    
    public static SpleefLeague getInstance() {
        return instance;
    }
    
    public static void main(String[] args) {
        try {
            MongoClient mongo = new MongoClient("127.0.0.1", 27017);
            mongo.getMongoOptions().autoConnectRetry = true;
            mongo.getMongoOptions().connectionsPerHost = 10;
            for(String dbname : mongo.getDatabaseNames()) {
                System.out.println(dbname);
            }
        } catch (Exception ex) {
            Logger.getLogger(SpleefLeague.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
