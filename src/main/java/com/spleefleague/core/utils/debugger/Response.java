package com.spleefleague.core.utils.debugger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Response {

    private int code;
    private String text;

    public Response(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public JSONArray getJSONArray() {
        try {
            return (JSONArray) new JSONParser().parse(this.text);
        } catch (Exception e) {
            return null;
        }
    }

    public JSONObject getJSON() {
        try {
            return (JSONObject) new JSONParser().parse(this.text);
        } catch (Exception e) {
            return null;
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
