package com.spleefleague.core.utils;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.io.Settings;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.DynamicCommand.LoadedDynamicCommand;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DynamicCommandManager implements Listener {

    private final SpleefLeague core;
    private final SortedMap<String, LoadedDynamicCommand> loadedCustomCommands;
    private final SortedMap<String, LoadedDynamicCommand> unloadedCommands;
    private final File cmdStoragePath;
    private final File cmdConfigPath;

    @SuppressWarnings("LeakingThisInConstructor")
    public DynamicCommandManager(SpleefLeague core) {
        this.loadedCustomCommands = new TreeMap();
        this.unloadedCommands = new TreeMap();
        this.core = core;
        this.cmdStoragePath = new File(this.core.getDataFolder(), "commands");
        this.cmdConfigPath = new File(this.core.getDataFolder(), "enabled_commands.json");
        Bukkit.getPluginManager().registerEvents(this, core);
        this.load();
    }

    private void persist() {
        JSONArray data = new JSONArray();
        for (Entry<String, LoadedDynamicCommand> e : loadedCustomCommands.entrySet()) {
            try {
                JSONObject cd = new JSONObject();
                cd.put("name", e.getKey());
                cd.put("file", e.getValue().getFile().getAbsolutePath());
                cd.put("enabled", true);
                data.put(cd);
            } catch (JSONException ex) {
                Logger.getLogger(DynamicCommandManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for (Entry<String, LoadedDynamicCommand> e : unloadedCommands.entrySet()) {
            try {
                JSONObject cd = new JSONObject();
                cd.put("name", e.getKey());
                cd.put("file", e.getValue().getFile().getAbsolutePath());
                cd.put("enabled", false);
                data.put(cd);
            } catch (JSONException ex) {
                Logger.getLogger(DynamicCommandManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            BufferedWriter o = new BufferedWriter(new FileWriter(this.cmdConfigPath));
            String txt = data.toString(4);
            for (String line : txt.split(System.lineSeparator())) {
                o.write(line + System.lineSeparator());
            }
            o.close();
        } catch (IOException | JSONException ex) {
            Logger.getLogger(DynamicCommandManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void load() {
        try {
            core.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "Loading Dynamic Commands");
            StringBuilder sb;
            try (BufferedReader r = new BufferedReader(new FileReader(this.cmdConfigPath))) {
                sb = new StringBuilder();
                String b;
                while ((b = r.readLine()) != null) {
                    sb.append(b).append(System.lineSeparator());
                }
            }
            JSONArray data = new JSONArray(sb.toString());
            for (int i = 0; i < data.length(); i++) {
                try {
                    JSONObject c = data.getJSONObject(i);
                    String name = c.getString("name").toLowerCase();
                    String fileStr = c.getString("file");
                    boolean enabled = c.getBoolean("enabled");
                    File f = new File(fileStr);
                    if (!f.exists() || name == null || name.trim().isEmpty()) {
                        continue;
                    }
                    if (!enabled) {
                        DynamicCommand cmd = (DynamicCommand) this.createInstance(f);
                        LoadedDynamicCommand disabled = new LoadedDynamicCommand(f, cmd, name);
                        this.unloadedCommands.put(name, disabled);
                        continue;
                    }
                    this.registerFromClass(f, (ChatColor color, String txt) -> {
                        core.getServer().getConsoleSender().sendMessage(color + txt);
                    });
                } catch (Exception ex) {
                }
            }
        } catch (IOException | JSONException ex) {
            Logger.getLogger(DynamicCommandManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.persist();
        core.getServer().getConsoleSender().sendMessage(ChatColor.WHITE + Integer.toString(this.loadedCustomCommands.size()) + ChatColor.YELLOW + " Dynamic Commands Loaded");
    }

    public String[] getRegisteredCommands() {
        return this.loadedCustomCommands.keySet().toArray(new String[this.loadedCustomCommands.size()]);
    }
    
    public String[] getUnloadedCommands() {
        return this.unloadedCommands.keySet().toArray(new String[this.unloadedCommands.size()]);
    }

    private boolean registerFromClass(File classFile, AsyncOutput console) {
        if (classFile == null || !classFile.exists()) {
            say(console, ChatColor.RED, "Error loading classfile");
            return false;
        }
        Object cmdObject = createInstance(classFile);
        if (!(cmdObject instanceof DynamicCommand)) {
            say(console, ChatColor.RED, "Invalid command (does not implement DynamicCommand)");
            return false;
        }
        DynamicCommand cmd = (DynamicCommand) cmdObject;
        if (this.loadedCustomCommands.containsKey(cmd.getName().toLowerCase())) {
            LoadedDynamicCommand previous = this.loadedCustomCommands.remove(cmd.getName().toLowerCase());
            String name = (previous != null && previous.getName() != null) ? previous.getName() : "<invalid>";
            say(console, ChatColor.GREEN, "Unregistered previous command: " + name);
        }
        LoadedDynamicCommand loadedCmd = new LoadedDynamicCommand(classFile, cmd, cmd.getName().toLowerCase());
        this.loadedCustomCommands.put(cmd.getName().toLowerCase(), loadedCmd);
        say(console, ChatColor.GREEN, "Registered handler for cmd: " + loadedCmd.getName());
        return true;
    }
    
    public void enable(String name, AsyncOutput console) {
        if (!this.unloadedCommands.containsKey(name)) {
            say(console, ChatColor.RED, "Command not found in disabled commands list");
            say(console, ChatColor.RED, "Check the output of '/dcmd disabled' to see if it is listed");
            return;
        }
        LoadedDynamicCommand cmd = this.unloadedCommands.get(name);
        if (this.registerFromClass(cmd.getFile(), console)) {
            this.unloadedCommands.remove(name);
        }
    }

    public void register(String haste, AsyncOutput console) {
        async(() -> {
            if (haste == null) {
                say(console, ChatColor.RED, "Haste URL is null");
                return;
            }
            File classFile = loadFromHaste(haste);
            registerFromClass(classFile, console);
            this.persist();
        });
    }

    public boolean unregister(String name) {
        LoadedDynamicCommand cmd = this.loadedCustomCommands.remove(name);
        this.unloadedCommands.put(name, cmd);
        boolean s = cmd != null;
        if (s) {
            async(() -> {
                this.persist();
            });
        }
        return s;
    }

    private void async(Runnable r) {
        Bukkit.getScheduler().runTaskAsynchronously(core, r);
    }

    private void say(AsyncOutput c, ChatColor color, String message) {
        if (c == null || message == null || message.trim().isEmpty()) {
            return;
        }
        Bukkit.getScheduler().runTask(core, () -> {
            c.onText(color, message);
        });
    }

    private Object createInstance(File classFile) {
        try {
            Class c = RuntimeCompiler.load(classFile);
            Object o = c.newInstance();
            return o;
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(DynamicCommandManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private File loadFromHaste(String id) {
        try {
            InputStream is;
            try {
                URL url = new URL(Settings.getString("debugger_paste_raw").replace("{id}", id));
                is = url.openStream();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    if (Settings.hasKey("debugger_paste_raw_backup")) {
                        URL url = new URL(Settings.getString("debugger_paste_raw_backup").replace("{id}", id));
                        is = url.openStream();
                    } else {
                        return null;
                    }
                } catch (Exception e2) {
                    return null;
                }
            }
            File javaFile = new File(cmdStoragePath.getPath() + "/" + id + ".java");
            javaFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(javaFile);
            String className = "";
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = is.read(bytes)) != -1) {
                fos.write(bytes, 0, read);
            }
            fos.close();
            is.close();
            FileInputStream fis = new FileInputStream(javaFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line;
            ArrayList<String> words = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                words.addAll(Arrays.asList(line.split(" ")));
            }
            for (int i = 0; i < words.size(); i++) {
                if (words.get(i).equals("class")) {
                    className = words.get(i + 1).replace("{", "");
                    break;
                }
            }
            br.close();
            File correctName = new File(cmdStoragePath.getAbsolutePath() + "/" + className + ".java");
            correctName.delete();
            javaFile.renameTo(correctName);
            File classFile = RuntimeCompiler.compile(correctName);
            return classFile;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        boolean valid = false;
        try {
            if (event.getPlayer() == null) {
                return;
            }
            String cmdLine = event.getMessage();
            String cmd = (cmdLine.contains(" ")) ? cmdLine.split(" ")[0] : cmdLine;
            String[] args = (cmdLine.contains(" ")) ? Arrays.copyOfRange(cmdLine.split(" "), 1, cmdLine.split(" ").length) : new String[0];
            if (cmd.startsWith("/")) {
                cmd = cmd.substring(1);
            }
            cmd = cmd.toLowerCase();
            if (!this.loadedCustomCommands.containsKey(cmd)) {
                return;
            }
            valid = true;
            event.setCancelled(true);
            Player p = event.getPlayer();
            SLPlayer sp = core.getPlayerManager().get(p);
            DynamicCommand exe = this.loadedCustomCommands.get(cmd).getCommand();
            if (sp == null) {
                exe.error(sp, BasicCommand.PLAYERDATA_ERROR_MESSAGE);
                return;
            }
            if (exe.canExecute(sp)) {
                exe.error(sp, BasicCommand.NO_COMMAND_PERMISSION_MESSAGE);
                return;
            }
            exe.run(p, sp, args);
        } catch (Exception ex) {
            if (valid && event != null && event.getPlayer() != null && event.getPlayer().isOp()) {
                event.getPlayer().sendMessage(ChatColor.RED + "Error running DynamicCommand");
                ex.printStackTrace();
            }
        }
    }

    public static interface AsyncOutput {

        public void onText(ChatColor color, String txt);

    }

}
