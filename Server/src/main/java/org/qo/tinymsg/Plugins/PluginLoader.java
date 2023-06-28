package org.qo.tinymsg.Plugins;
import org.json.JSONObject;
import org.qo.tinymsg.Debugger;
import org.qo.tinymsg.Logger;
import org.qo.tinymsg.Server;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class PluginLoader {
    public static String folderpth;
    static boolean Ready;
    public static String PluginName = "Plugin Loader";
    public static String workingDirectory;
    static Server server = new Server();
    public static void startup() throws Exception {
        Debugger debugger = new Debugger();
        debugger.log("Plugin Loader Started. Ready for execute.",0);
        workingDirectory = System.getProperty("user.dir");
        createPluginsFolder();
        executePlugin(new File("\\plugins\\Plugin.jar"));
    }
    public static void createPluginsFolder() {
        String os = System.getProperty("os.name");
        if (os.startsWith("Windows")) {
            folderpth = workingDirectory + "\\plugins";
        } else {
            folderpth = workingDirectory + "/plugins";
        }
        File folder = new File(folderpth);

        // 检测文件夹是否存在
        if (!folder.exists()) {
            // 文件夹不存在，创建它
            boolean created = folder.mkdirs();

            if (created) {
                Logger.log("Created Plugin Folder.");
            } else {
                Logger.log("unable to create Plugin Folder.");
                return;
            }
        }
    }
        public static void execute() {
            String pluginsFolderPath = folderpth;

            File pluginsFolder = new File(pluginsFolderPath);
            if (pluginsFolder.isDirectory()) {
                File[] pluginFiles = pluginsFolder.listFiles();
                if (pluginFiles != null) {
                    for (File pluginFile : pluginFiles) {
                        if (pluginFile.isFile() && pluginFile.getName().endsWith(".jar")) {
                            executePlugin(pluginFile);
                        }
                    }
                } else {
                    System.out.println("The plugins folder is empty.");
                }
            }
        }
        private static void executePlugin(File pluginFile) {
            try {
                URL pluginUrl = pluginFile.toURI().toURL();
                URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{pluginUrl});
                Class<?> pluginClass = classLoader.loadClass("org.qo.Plugins");
                Method executeMethod = pluginClass.getMethod("run");
                executeMethod.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
