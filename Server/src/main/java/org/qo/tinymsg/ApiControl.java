package org.qo.tinymsg;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import static org.qo.tinymsg.Server.USER_PROFILE;

public class ApiControl {
    static Server server = new Server();
    public static boolean isServer(String username) {
        try {
            String userContent = server.readFile(USER_PROFILE);
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
}
