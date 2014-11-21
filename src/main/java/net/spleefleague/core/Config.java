/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

/**
 *
 * @author Jonas
 */
public class Config {

    public static String DB_HOST = "mongo.spleefleague.net";
    public static int DB_PORT = 27017;
    public static String DB_USERNAME = "SpleefLeague";
    public static String DB_PASSWORD = "";
    private static HashMap<String, String> ADDITIONAL_CONFIG;
    
    static {
        ADDITIONAL_CONFIG = new HashMap<>();
    }
    
    public static void loadConfig() {
        if (!new File("db.conf").exists()) {
            return;
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader("db.conf"));
            String s;
            while ((s = br.readLine()) != null) {
                if (!s.startsWith("#") && s.contains(":")) {
                    while (s.startsWith(" ")) {
                        s = s.replaceAll(" ", "");
                    }
                    String[] command = s.split(":");
                    if (command[0].equalsIgnoreCase("host")) {
                        if (command.length == 2) {
                            DB_HOST = command[1];
                        }
                    } else if (command[0].equalsIgnoreCase("port")) {
                        if (command.length == 2) {
                            DB_PORT = Integer.valueOf(command[1]);
                        }
                    } else if (command[0].equalsIgnoreCase("username")) {
                        if (command.length == 2) {
                            DB_USERNAME = command[1];
                        }
                    } else if (command[0].equalsIgnoreCase("password")) {
                        if (command.length == 2) {
                            DB_PASSWORD = command[1];
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public static boolean hasKey(String key) {
        return ADDITIONAL_CONFIG.containsKey(key);
    }
    
    public static String getString(String key) {
        return (String) ADDITIONAL_CONFIG.get(key);
    }
    
    public static int getInteger(String key) {
        return Integer.parseInt(ADDITIONAL_CONFIG.get(key));
    }
    
    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(ADDITIONAL_CONFIG.get(key));
    }
}