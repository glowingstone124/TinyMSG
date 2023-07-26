package org.qo.tinymsg;
import org.qo.tinymsg.Plugins.PluginLoader;
public class Stop {
    public static void stop(){
        PluginLoader.stopPlugins();

        System.exit(0);
    }
}
