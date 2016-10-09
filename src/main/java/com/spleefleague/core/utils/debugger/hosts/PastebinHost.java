package com.spleefleague.core.utils.debugger.hosts;

import com.spleefleague.core.utils.debugger.DebuggerHost;
import com.spleefleague.core.utils.debugger.DebuggerHostFailedException;
import com.spleefleague.core.utils.debugger.Response;
import org.bson.Document;

import java.net.URL;

public class PastebinHost extends DebuggerHost {

    private String url;
    private int minlength;
    private int maxlength;

    @Override
    public String getType() {
        return "pastebin";
    }

    @Override
    public boolean load(Document doc) {
        try {
            this.url = doc.getString("url");
            Document settings = (Document) doc.get("settings");
            this.minlength = settings.getInteger("minlength");
            this.maxlength = settings.getInteger("maxlength");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isMatch(String key) {
        return key.length() >= this.minlength && key.length() <= this.maxlength;
    }

    @Override
    public String handle(String key) throws Exception {
        Response r = super.getUrlString(new URL(param(this.url, "id", key)));
        if (r.getCode() != 200) {
            throw new DebuggerHostFailedException(this, key, r);
        }
        return r.getText();
    }
}