package org.qo.tinymsg;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;


public class ApiControler {
    public static boolean Enable;
    public static final String API_CONFIG = "api.json";

    public static void loadconfig() {
        if (fileExists()) {
            // If the configuration file exists, read the configuration
            try {
                String Apiconfig = server.readFile(API_CONFIG);

                if (Apiconfig != null) {
                    JSONObject jsonConfig = new JSONObject(Apiconfig);
                    Enable = jsonConfig.getBoolean("Enable");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // Configuration file doesn't exist, use default configuration and generate the configuration file
            JSONObject jsonConfig = new JSONObject();
            jsonConfig.put("Enable", true);
            Enable = true;
            writeFile(API_CONFIG, jsonConfig.toString());

        }
    }
    static Server server;

    static {
        try {
            server = new Server();
        } catch (Exceptions.IllegalConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isServer(String username) {
        try {
            String userContent = server.readFile(Server.USER_PROFILE);
            if (userContent != null) {
                JSONObject userProfiles = new JSONObject(userContent);
                if (userProfiles.has(username)) {
                    JSONObject userProfile = userProfiles.getJSONObject(username);
                    int permissionlvl = userProfile.getInt("permission");
                    if (Objects.equals(permissionlvl, 2)) {
                        return true;
                    }
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return false;
    }
    private static boolean fileExists() {
        File file = new File(API_CONFIG);
        return file.exists() && !file.isDirectory();
    }
    private static void writeFile(String filename, String content) {
        try {
            FileWriter writer = new FileWriter(filename);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void start(){
        loadconfig();
    }
}
