/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils;

import com.spleefleague.core.io.Settings;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class XenforoAPIUtil {

    private static final String emailFormat = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String passwordAlphabet = "abcdefghijkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ123456789";
    private static final SecureRandom random;

    public static Result createForumUser(Player player, String email) {
        if (email.contains("=") || email.contains("&")) {
            return Result.INJECTION;
        } else if (!email.matches(emailFormat)) {
            return Result.INVALID_EMAIL;
        } else {
            try {
                StringBuilder query = new StringBuilder();
                query.append("?action=register&hash=");
                query.append(Settings.getString("XenAPIKey"));
                query.append("&username=");
                query.append(player.getName());
                query.append("&password=");
                query.append(getRandomPassword(12));
                query.append("&email=");
                query.append(email);
                query.append("&custom_fields=mcusername=");
                query.append("mcusername=");
                query.append(player.getName());
                query.append(",mcuuid=");
                query.append(player.getUniqueId().toString());
                query.append("&user_state=email_confirm");
                URL url = new URL(Settings.getString("XenAPIURL") + query.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                connection.connect();
                if (connection.getResponseCode() == 400) {
                    return Result.EMAIL_EXISTS;
                } else if (connection.getResponseCode() == 200) {
                    return Result.SUCCESS;
                } else {
                    return Result.UNREACHABLE;
                }
            } catch (MalformedURLException e) {
                return Result.UNREACHABLE;
            } catch (IOException e) {
                return Result.UNREACHABLE;
            }
        }
    }

    private static String getRandomPassword(int length) {
        StringBuilder sb = new StringBuilder();
        while (length >= 0) {
            sb.append(passwordAlphabet.charAt(random.nextInt(passwordAlphabet.length())));
            length--;
        }
        return sb.toString();
    }

    public static enum Result {
        SUCCESS,
        INVALID_EMAIL,
        INJECTION,
        EMAIL_EXISTS,
        UNREACHABLE;
    }

    static {
        random = new SecureRandom();
    }
}
