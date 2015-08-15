package com.spleefleague.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.io.Settings;
import com.spleefleague.core.utils.Debugger.CommandExecutor;
import com.spleefleague.core.utils.Debugger.Stoppable;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 *
 * @author Jonas Balsfulland
 */
public class RuntimeCompiler {
    
    private static HashMap<String, Debugger> debuggerList;
    private static File directoryTemp;
    private static File directoryPermanent;
    
    public static Object loadHastebin(String id) {
        try {
            InputStream is;
            try {
                URL url = new URL(Settings.getString("debugger_paste_raw").replace("{id}", id));
                is = url.openStream();
            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }
            File javaFile = new File(directoryTemp.getPath() + "/" + id + ".java"); 
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
            while((line = br.readLine()) != null) {
                words.addAll(Arrays.asList(line.split(" ")));
            }
            for(int i = 0; i < words.size(); i++) {
                if(words.get(i).equals("class")) {
                    className = words.get(i + 1).replace("{", "");
                    break;
                }
            }
            br.close();
            File correctName = new File(directoryTemp.getAbsolutePath() + "/" + className + ".java");
            correctName.delete();
            javaFile.renameTo(correctName);
            File classFile = RuntimeCompiler.compile(correctName);
            Class c = RuntimeCompiler.load(classFile);
            Object o = c.newInstance();
            return o;
        } catch (IOException | IllegalAccessException | InstantiationException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    private static File compile(File file) {
        try {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            List<String> optionList = new ArrayList<>();
            File jar = getJar(RuntimeCompiler.class);
            File pluginDirectory = new File(jar.getAbsolutePath().substring(0, jar.getAbsolutePath().length() - jar.getName().length()));
            String classes = buildClassPath(getJar(Bukkit.class).getName(), pluginDirectory.getName() + "/*");
            optionList.addAll(Arrays.asList("-classpath", classes));
            boolean success;
            try (StandardJavaFileManager fileManager = compiler.getStandardFileManager( null, null, null )) {
                Iterable<? extends JavaFileObject> units;
                units = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file));
                JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, optionList, null, units);
                success = task.call();
            }
            if(success) {
                return new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 5) + ".class");
            }
            else {
                return null;
            }
        } catch (IOException ex) {
            Logger.getLogger(RuntimeCompiler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static Class load(File file) {
        try {
            URL url = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - file.getName().length())).toURI().toURL();
            ClassLoader cl = new URLClassLoader(new URL[]{url}, SpleefLeague.class.getClassLoader());
            Class c = cl.loadClass(file.getName().substring(0, file.getName().length() - 6));
            return c;
        } catch (MalformedURLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    private static String buildClassPath(String... paths) {
        StringBuilder sb = new StringBuilder();
        for (String path : paths) {
            if (path.endsWith("*")) {
                path = path.substring(0, path.length() - 1);
                File pathFile = new File(path);
                for (File file : pathFile.listFiles()) {
                    if (file.isFile() && file.getName().endsWith(".jar")) {
                        sb.append(path);
                        sb.append(file.getName());
                        sb.append(System.getProperty("path.separator"));
                    }
                }
            } else {
                sb.append(path);
                sb.append(System.getProperty("path.separator"));
            }
        }
        return sb.toString();
    }
    
    public static File getJar(Class aclass) {
        try {
            return new File(aclass.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException ex) {
            Logger.getLogger(RuntimeCompiler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private static void debugFromClass(Class c) {
        try {
            Object o = c.newInstance();
            if (!(o instanceof Debugger)) {
                throw new Exception("Runtime script isn't extending the Debugger class");   
            }
            Debugger debugger = (Debugger)o;
            startDebugger(debugger);
            if (RuntimeCompiler.debuggerList == null) {
                RuntimeCompiler.debuggerList = new HashMap<>();
            }
            if (debugger instanceof CommandExecutor || debugger instanceof Listener || debugger instanceof Stoppable) {
                int uid = 1;
                while (RuntimeCompiler.debuggerList.containsKey((o.getClass().getName() + Integer.toString(uid)).toLowerCase())) {
                    uid++;
                }
                String n = o.getClass().getName() + Integer.toString(uid);
                RuntimeCompiler.debuggerList.put(n.toLowerCase(), debugger);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(RuntimeCompiler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(RuntimeCompiler.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
    
    public static String[] debugFromHastebin(String id) {
        Object o = RuntimeCompiler.loadHastebin(id);
        try {
            if (!(o instanceof Debugger)) {
                throw new Exception("Runtime script isn't extending the Debugger class");   
            }
            Debugger debugger = (Debugger)o;
            startDebugger(debugger);
            if (RuntimeCompiler.debuggerList == null) {
                RuntimeCompiler.debuggerList = new HashMap<>();
            }
            String n = o.getClass().getName();
            if (!((debugger instanceof CommandExecutor || debugger instanceof Listener || debugger instanceof Stoppable))) {
                return new String[] { n };
            }
            else {
                int uid = 1;
                while (RuntimeCompiler.debuggerList.containsKey((o.getClass().getName() + Integer.toString(uid)).toLowerCase())) {
                    uid++;
                }
                n = o.getClass().getName() + Integer.toString(uid);
                RuntimeCompiler.debuggerList.put(n.toLowerCase(), debugger);
                if(debugger instanceof CommandExecutor) {
                    if(debugger instanceof Listener || debugger instanceof Stoppable) {
                        return new String[]{n, ""};
                    }
                    else {
                        return new String[]{n, "", "", ""};
                    }
                }
                else {
                    return new String[]{n, "", ""};
                }
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(RuntimeCompiler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(RuntimeCompiler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static boolean runDebuggerCommand(String id, CommandSender sender, String[] args) throws Exception {
        Debugger d = RuntimeCompiler.debuggerList.get(id.toLowerCase());
        if (d != null && d instanceof CommandExecutor) {
            ((CommandExecutor)d).onCommand(sender, args);
            return true;
        }
        return false;
    }
    
    public static String stopDebugger(String id) {
        Debugger d = RuntimeCompiler.debuggerList.remove(id.toLowerCase());
        if (d != null && (d instanceof Stoppable || d instanceof Listener)) {
            try {
                stopDebugger(d);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return d.getClass().getName();
        }
        return null;
    }
    
    private static File getPluginDirectory() {
        File file = getJar(RuntimeCompiler.class);
        return new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - file.getName().length()));
    }
    
    
    public static void startDebugger(Debugger inst) {
        if (inst instanceof Listener) {
            Listener l = (Listener) inst;
            Bukkit.getPluginManager().registerEvents(l, SpleefLeague.getInstance());
        }
        inst.debug();
    }
    
    public static void stopDebugger(Debugger inst) {
        if (inst instanceof Listener) {
            HandlerList.unregisterAll((Listener)inst);
        }
        if (inst instanceof Stoppable) {
            ((Stoppable)inst).stop();
        }
    }
    
    public static void loadPermanentDebuggers() {
        for(File file : directoryPermanent.listFiles(ClassFilter.getInstance())) {
            Class<? extends Debugger> debugClass = RuntimeCompiler.load(file);
            debugFromClass(debugClass);
            System.out.println(SpleefLeague.getInstance().getPrefix() + " Loaded permanent debugger: " + file.getName().replace(".class", ""));
        }
    }
    
    public static class ClassFilter implements FileFilter {

        private static final ClassFilter instance;
        
        private ClassFilter() {
            
        }
        
        @Override
        public boolean accept(File file) {
            return file.getName().toLowerCase().endsWith(".class");
        }
        
        public static ClassFilter getInstance() {
            return instance;
        }
        
        static {
            instance = new ClassFilter();
        }
    }
    
    //Can be necessary on some windows and java versions.
    static {
//        System.setProperty("java.home", System.getProperty("java.home").replace("jre", "jdk"));
        directoryTemp = new File(getPluginDirectory().getAbsolutePath() + "/debug/temp");
        directoryPermanent = new File(getPluginDirectory().getAbsolutePath() + "/debug/permanent");
        if(!directoryTemp.exists()) {
            directoryTemp.mkdirs();
        }
        if(!directoryPermanent.exists()) {
            directoryPermanent.mkdirs();
        }
    }
}