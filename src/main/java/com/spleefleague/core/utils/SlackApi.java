/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.events.SlackMessageReceivedEvent;
import com.spleefleague.core.io.Settings;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Jonas
 */
public class SlackApi {

    private final String service;

    public SlackApi(String service) {
        if (service == null) {
            throw new IllegalArgumentException(
                    "Missing WebHook URL Configuration @ SlackApi");
        }
        else if (!service.startsWith("https://hooks.slack.com/services/")) {
            throw new IllegalArgumentException(
                    "Invalid Service URL. WebHook URL Format: https://hooks.slack.com/services/{id_1}/{id_2}/{token}");
        }

        this.service = service;
    }

    public String send(String message) {
        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL(this.service);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            String payload = "payload=" + URLEncoder.encode(message, "UTF-8");

            try (
                    DataOutputStream wr = new DataOutputStream(
                            connection.getOutputStream())) {
                        wr.writeBytes(payload);
                        wr.flush();
                    }

                    InputStream is = connection.getInputStream();
                    StringBuilder response;
                    try (BufferedReader rd = new BufferedReader(new InputStreamReader(is))) {
                        String line;
                        response = new StringBuilder();
                        while ((line = rd.readLine()) != null) {
                            response.append(line);
                            response.append('\r');
                        }
                    }
                    return response.toString();

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static BukkitTask task;

    public static void killSlackMessageListener() {
        task.cancel();
    }

    public static void initSlackMessageListener() {
        task = Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Socket receiving = new Socket("mongo.spleefleague.com", 29091);
                        System.out.println("Connected to bridge");
                        DataInputStream dIn = new DataInputStream(receiving.getInputStream());
                        String token = "", userid = "", message = "";
                        while (true) {
                            switch (dIn.readByte()) {
                                case 0: {
                                    token = dIn.readUTF();
                                    break;
                                }
                                case 1: {
                                    userid = dIn.readUTF();
                                    break;
                                }
                                case 2: {
                                    message = dIn.readUTF();
                                    handleMessage(token, userid, message);
                                }
                            }
                        }
                    } catch (EOFException e) {
                        System.out.println("Bridge has disconnected..");
                    } catch (IOException | NumberFormatException | IllegalStateException ex) {
                        try {
                            Thread.sleep(10000);
                            System.out.println("Reconnecting..");
                        } catch (InterruptedException ex1) {
                            Logger.getLogger(SlackApi.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                    }
                }
            }
        });
    }

    private static void handleMessage(String token, String userid, String message) {
        if (token.equals(Settings.getString("slack_token"))) {
            Bukkit.getPluginManager().callEvent(new SlackMessageReceivedEvent(userid, message));                
        }
    }
}
