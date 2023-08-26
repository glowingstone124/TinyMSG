package org.qo.tinymsg.Plugins;

import org.qo.tinymsg.Debugger;
import org.qo.tinymsg.Exceptions;
import org.qo.tinymsg.Logger;
import org.qo.tinymsg.Server;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader {
    /*
    When server starts, it will execute PluginLoader.startup() method.
    PluginLoad.startup() method contains 4 steps:
    1.Show loader message
    2.Get user dir and looking for a "plugins" folder. If there is no "plugins", it will create one.
    3.execute loadPlugins() method. This method will looking for all "run" method in .jar file and execute it.
    When server exits, it will execute PluginLoader.exitPlugins method. This method will execute all "stop" method in .jar file and execute it.
     */
    public static String folderPath;
    public static String workingDirectory;
    static Server server;

    static {
        try {
            server = new Server();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void startup() throws Exception {
        Debugger debugger = new Debugger();
        debugger.log("Plugin Loader Started. Ready to execute.", 0);
        workingDirectory = System.getProperty("user.dir");
        createPluginsFolder();
        loadPlugins();
    }

    public static void createPluginsFolder() {
        String os = System.getProperty("os.name");
        if (os.startsWith("Windows")) {
            folderPath = workingDirectory + "\\plugins";
        } else {
            folderPath = workingDirectory + "/plugins";
        }
        File folder = new File(folderPath);

        // 检测文件夹是否存在
        if (!folder.exists()) {
            // 文件夹不存在，创建它
            boolean created = folder.mkdirs();

            if (created) {
                Logger.log("Created Plugin Folder.");
            } else {
                Logger.log("Unable to create Plugin Folder.");
                return;
            }
        }
    }

    private static void loadPlugins() {
        File pluginsFolder = new File(folderPath);
        if (pluginsFolder.isDirectory()) {
            File[] pluginFiles = pluginsFolder.listFiles();
            if (pluginFiles != null) {
                for (File pluginFile : pluginFiles) {
                    if (pluginFile.isFile() && pluginFile.getName().endsWith(".jar")) {
                        String className = getPluginClassName(pluginFile);
                        if (className != null) {
                            loadPlugin(pluginFile, className);
                        }
                    }
                }
            }
        }
    }
    public static void stopPlugins() {
        File pluginsFolder = new File(folderPath);
        if (pluginsFolder.isDirectory()) {
            File[] pluginFiles = pluginsFolder.listFiles();
            if (pluginFiles != null) {
                for (File pluginFile : pluginFiles) {
                    if (pluginFile.isFile() && pluginFile.getName().endsWith(".jar")) {
                        String className = getPluginClassName(pluginFile);
                        if (className != null) {
                            exitPlugin(pluginFile, className);
                        }
                    }
                }
            }
        }
    }


    private static void loadPlugin(File pluginFile, String className) {
        try {
            URL pluginUrl = pluginFile.toURI().toURL();
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{pluginUrl});
            Class<?> pluginClass = classLoader.loadClass(className);
            Method executeMethod = pluginClass.getMethod("run");

            // 创建一个新线程并执行 run 方法
            Thread thread = new Thread(() -> {
                try {
                    executeMethod.invoke(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // 启动线程
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void exitPlugin(File pluginFile, String className) {
        try {
            URL pluginUrl = pluginFile.toURI().toURL();
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{pluginUrl});
            Class<?> pluginClass = classLoader.loadClass(className);
            Method executeMethod = pluginClass.getMethod("stop");
            executeMethod.invoke(null);
        } catch (NoSuchMethodException e) {
            server.log("Plugin " + className + " doesn't have stop() method! ", 2);
        } catch (MalformedURLException ex) {
            //empty catch block
        } catch (ClassNotFoundException exc) {
            server.log("No such class found!", 2);
        } catch (IllegalAccessException exce) {
            //empty catch block
        } catch (InvocationTargetException excep) {

        }
    }
    private static String getPluginClassName(File pluginFile) {
        try (JarFile jarFile = new JarFile(pluginFile)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    String className = entry.getName().replace('/', '.').substring(0, entry.getName().length() - ".class".length());
                    return className;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
