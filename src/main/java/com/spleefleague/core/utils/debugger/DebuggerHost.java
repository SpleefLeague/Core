package com.spleefleague.core.utils.debugger;

import org.bson.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class DebuggerHost {

    private static final String USER_AGENT = "Mozilla/5.0";
    private int index;

    public final int getIndex() {
        return this.index;
    }

    protected final void setIndex(int index) {
        this.index = index;
    }

    public abstract String getType();

    public abstract boolean load(Document doc);

    public abstract boolean isMatch(String key);

    public abstract String handle(String key) throws Exception;

    public String param(String input, String key, Object value) {
        return input.replaceAll("\\b{" + key + "}\\b", value.toString());
    }

    public final Response getUrlString(URL url) {
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();
            boolean redirect = false;

            if (responseCode != HttpURLConnection.HTTP_OK) {
                if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
                    redirect = true;
                }
            }

            if (redirect) {
                String newUrl = con.getHeaderField("Location");
                con = (HttpURLConnection) new URL(newUrl).openConnection();
                con.setRequestProperty("User-Agent", USER_AGENT);
            }
            StringBuffer response;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            return new Response(responseCode, response.toString());
        } catch (IOException e) {
            return new Response(404, null);
        }
    }

}
