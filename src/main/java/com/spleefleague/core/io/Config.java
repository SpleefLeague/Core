/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.io;

import com.mongodb.MongoCredential;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.player.Rank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author Jonas
 */
public class Config {
    
    @Deprecated
    public static String DB_HOST;
    @Deprecated
    public static int DB_PORT;
    private final static HashMap<String, String> ADDITIONAL_CONFIG;
    private static boolean configLoaded = false;
    
    static {
        ADDITIONAL_CONFIG = new HashMap<>();
    }
    
    public static boolean isConfigLoaded() {
        return configLoaded;
    }

    public static void loadConfig() {
        try {
            File file = new File("sl.conf");
            if(!file.exists()) {
                file = new File("db.conf");
                if(file.exists()) {
                    SpleefLeague.getInstance().getLogger().warning("Usage of db.conf is deprecated. Please rename the file to sl.conf");
                }
            }
            if(!file.exists()) {
                SpleefLeague.getInstance().getLogger().warning("Config file could not be found.");
                return;
            }
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s;
            while ((s = br.readLine()) != null) {
                s = s.trim();
                if (!s.startsWith("#") && s.contains(":")) {
                    String[] command = s.split(":", 2);
                    if (command.length < 2) {
                        continue;
                    }
                    String key = command[0].trim();
                    String val = command[1].trim();
                    if (key.equalsIgnoreCase("host")) {
                        DB_HOST = val;

                    } else if (key.equalsIgnoreCase("port")) {
                        DB_PORT = Integer.valueOf(val);

                    } 
                    ADDITIONAL_CONFIG.put(key, val);
                }
            }
            configLoaded = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static List<?> getList(String key) {
        if(!hasKey(key)) {
            return null;
        }
        Object value = ADDITIONAL_CONFIG.get(key);
        return Arrays.stream(value.toString().split(",")).collect(Collectors.toList());
    }

    public static Rank getRank(String key) {
        return Rank.valueOf(ADDITIONAL_CONFIG.get(key));
    }

    public static boolean hasKey(String key) {
        return ADDITIONAL_CONFIG.containsKey(key);
    }

    public static String getString(String key) {
        return (String) ADDITIONAL_CONFIG.get(key);
    }
    
    public static int getInteger(String key) {
        if (ADDITIONAL_CONFIG.containsKey(key))
            return Integer.parseInt(ADDITIONAL_CONFIG.get(key));
        return 0;
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(ADDITIONAL_CONFIG.get(key));
    }

    public static List<MongoCredential> getCredentials() {
        List<MongoCredential> credentials = new ArrayList<>();
        Iterator<String> i = ADDITIONAL_CONFIG.keySet().iterator();
        while (i.hasNext()) {
            String key = i.next();
            if (key.startsWith("pw.")) {
                MongoCredential credential = MongoCredential.createCredential("plugin", key.substring(3), getString(key).toCharArray());
                credentials.add(credential);
            }
        }
        return credentials;
    }
}
