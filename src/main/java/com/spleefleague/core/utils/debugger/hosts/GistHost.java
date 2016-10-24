package com.spleefleague.core.utils.debugger.hosts;

import com.spleefleague.core.utils.debugger.DebuggerHost;
import com.spleefleague.core.utils.debugger.DebuggerHostFailedException;
import com.spleefleague.core.utils.debugger.Response;
import org.bson.Document;
import org.json.simple.JSONObject;

import java.net.URL;

public class GistHost  extends DebuggerHost {

    private String url;
    private int minlength;
    private int maxlength;

    @Override
    public String getType() {
        return "gist";
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
        Response r1 = super.getUrlString(new URL(param(this.url, "id", key)));
        if (r1.getCode() != 200) {
            throw new DebuggerHostFailedException(this, key, r1);
        }
        JSONObject files = (JSONObject) r1.getJSON().get("files");
        if (files.keySet().isEmpty()) {
            throw new DebuggerHostFailedException(this, key, "Empty file list");
        }
        JSONObject file = (JSONObject) files.get(files.keySet().iterator().next());
        String rawUrl = (String) file.get("raw_url");
        Response r2 = super.getUrlString(new URL(rawUrl));
        if (r2.getCode() != 200) {
            throw new DebuggerHostFailedException(this, key, r2);
        }
        return r2.getText();
    }

}
