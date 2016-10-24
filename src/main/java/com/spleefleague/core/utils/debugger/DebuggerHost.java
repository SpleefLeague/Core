package com.spleefleague.core.utils.debugger;

import org.bson.Document;

import java.io.BufferedReader;
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

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return new Response(responseCode, response.toString());
        } catch (Exception e) {
            return new Response(404, null);
        }
    }

}
