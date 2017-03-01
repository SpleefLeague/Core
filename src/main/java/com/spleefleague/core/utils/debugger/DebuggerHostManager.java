package com.spleefleague.core.utils.debugger;

import com.spleefleague.core.utils.debugger.hosts.GistHost;
import com.spleefleague.core.utils.debugger.hosts.HastebinHost;
import com.spleefleague.core.utils.debugger.hosts.PastebinHost;
import org.bson.Document;

import java.lang.reflect.Constructor;
import java.util.*;

public class DebuggerHostManager {

    private static final Class<? extends DebuggerHost>[] DEFAULT_HOSTS = new Class[] {
            GistHost.class, HastebinHost.class, PastebinHost.class
    };

    private final Map<String, Class<? extends DebuggerHost>> typeMap;

    private final Collection<DebuggerHost> hosts;
    private final Comparator<DebuggerHost> comparator;

    public DebuggerHostManager() {
        this.comparator = (d1, d2) -> Integer.compare(d1.getIndex(), d2.getIndex());
        this.typeMap = new HashMap<>();
        this.hosts = new TreeSet<>(this.comparator);
    }

    public String handle(String key) {
        for (DebuggerHost host : this.hosts) {
            try {
                if (host.isMatch(key)) {
                    return host.handle(key);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String handle(String type, String key) {
        for (DebuggerHost host : this.hosts) {
            try {
                if (host.getType().equalsIgnoreCase(type)) {
                    if (host.isMatch(key)) {
                        return host.handle(key);
                    }
                }
            } catch(Exception e) {

            }
        }
        return null;
    }

    public void add(DebuggerHost host) {
        this.hosts.add(host);
    }

    public void reloadAll(List<Document> hosts) {
        this.hosts.clear();
        this.typeMap.clear();

        for (Class<? extends DebuggerHost> hostClass : DEFAULT_HOSTS) {
            try {
                Constructor<? extends DebuggerHost> c = hostClass.getDeclaredConstructor();
                DebuggerHost host = c.newInstance();
                this.typeMap.put(host.getType(), hostClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Document host : hosts) {
            try {
                Class<? extends DebuggerHost> c = this.typeMap.get(host.getString("type"));
                DebuggerHost dhost = ((Constructor<DebuggerHost>)c.getDeclaredConstructor()).newInstance();
                dhost.setIndex(host.getInteger("index"));
                if (dhost.load(host)) {
                    this.hosts.add(dhost);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
