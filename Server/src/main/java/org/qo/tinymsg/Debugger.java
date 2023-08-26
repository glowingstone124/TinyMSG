
package org.qo.tinymsg;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONObject;
import java.io.BufferedReader;
public class Debugger {
    static Server server;

    static {
        try {
            server = new Server();
        } catch (Exceptions.IllegalConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    Logger logger = new Logger();

    public static void log(String message, int lvl) {
        server.log(message, lvl);
    }

    public void outputlog(String message) {
        logger.log(message);
    }

    public static boolean testConnection(String url) throws Exception {

        int timeOut = 3000;
        if (InetAddress.getByName(url).isReachable(timeOut)) {
            return false;
        }
        return true;
    }
    public boolean needUpdate() throws IOException {
        String url = "https://tmsg.glowingstone.cn/update.json";
        try {
            JSONObject json = readJsonFromUrl(url);
            double version = json.getDouble("version");
            if(version > Server.innerVersion) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static JSONObject readJsonFromUrl(String url) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return new JSONObject(sb.toString());
        }
    }
    public static void clean() {
        try {
            String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.println("\033c");
            }
        } catch (Exception exception) {
            //  Handle exception.
        }
    }

}