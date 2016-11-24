package com.spleefleague.core.utils.fakeentity;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class SkinFactory {
    
    private final static JsonParser parser = new JsonParser();
    
    public static void setupSkinForProfile(WrappedGameProfile profile, TexturesPropertyInfo skin) {
        profile.getProperties().put("textures", new WrappedSignedProperty("textures", skin.getValue(), skin.getSignature()));
    }

    public static TexturesPropertyInfo getSkinEncryptedData(UUID skinUuid) {
        try {
            StringBuilder sb = new StringBuilder();
            HttpURLConnection huc = (HttpURLConnection) new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + skinUuid.toString().replaceAll("-", "") + "?unsigned=false").openConnection();
            huc.setRequestMethod("GET");
            huc.setDoOutput(true);
            huc.setDoInput(true);
            huc.connect();
            try(Scanner scanner = new Scanner(huc.getInputStream())) {
                while(scanner.hasNextLine())
                    sb.append(scanner.nextLine());
            }
            huc.disconnect();
            JsonObject jo = (JsonObject) parser.parse(sb.toString());
            JsonArray ja = (JsonArray) jo.get("properties");
            JsonObject jo2 = (JsonObject) ja.get(0);
            return new TexturesPropertyInfo(jo2.get("value").getAsString(), jo2.get("signature").getAsString());
        }catch (IOException | JsonSyntaxException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static class TexturesPropertyInfo {
        
        private final String value;
        private final String signature;
        
        public TexturesPropertyInfo(String value, String signature) {
            this.value = value;
            this.signature = signature;
        }
        
        public String getValue() {
            return value;
        }
        
        public String getSignature() {
            return signature;
        }
        
    }

}
