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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

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

    private static ServerSocket ss;

    public static void killSlackMessageListener() {
        try {
            ss.close();
        } catch (IOException ex) {
            Logger.getLogger(SlackApi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void initSlackMessageListener() {
        Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), new Runnable() {
            @Override
            public void run() {

                try {
                    ss = new ServerSocket(29090);
                    while (true) {
                        try (Socket socket = ss.accept(); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                            // read request
                            String line;
                            line = in.readLine();
                            boolean isPost = line.startsWith("POST");
                            int contentLength = 0;
                            while (!(line = in.readLine()).equals("")) {
                                if (isPost) {
                                    final String contentHeader = "Content-Length: ";
                                    if (line.startsWith(contentHeader)) {
                                        contentLength = Integer.parseInt(line.substring(contentHeader.length()));
                                    }
                                }
                            }
                            StringBuilder body = new StringBuilder();
                            if (isPost) {
                                int c;
                                for (int i = 0; i < contentLength; i++) {
                                    c = in.read();
                                    body.append((char) c);
                                }
                            }
                            System.out.println(body.toString());
                            String[] pairs = body.toString().split("&");
                            boolean token = false;
                            String channel = "", user = "", userid = "", message = "", trigger = "";
                            for (String pair : pairs) {
                                String[] split = pair.split("=");
                                switch (split[0]) {
                                    case "channel_name": {
                                        channel = split[1];
                                        break;
                                    }
                                    case "user_name": {
                                        user = split[1];
                                        break;
                                    }
                                    case "user_id": {
                                        userid = split[1];
                                        break;
                                    }
                                    case "text": {
                                        message = split[1];
                                        break;
                                    }
                                    case "trigger": {
                                        message = split[1];
                                        break;
                                    }
                                    case "token": {
                                        if (split[1].equals(Settings.getString("slack_token"))) {
                                            token = true;
                                        }
                                    }
                                }
                            }
                            if (token) {
                                message = message.replaceFirst(trigger, "");
                                Bukkit.getPluginManager().callEvent(new SlackMessageReceivedEvent(channel, user, userid, message));
                            }
                        }
                    }
                } catch (IOException | NumberFormatException | IllegalStateException ex) {
                    Logger.getLogger(SlackApi.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}
