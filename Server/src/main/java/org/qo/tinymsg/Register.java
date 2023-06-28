package org.qo.tinymsg;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;

public class Register {
    private static final String USERS_FILE = "users.json";

    public static boolean reg(String username, String password) {
        try {
            // 读取现有的用户数据
            JSONObject users = readUsersFile();

            // 检查用户是否已存在
            if (!users.has(username)) {
                // 创建新用户对象
                JSONObject newUser = new JSONObject();
                newUser.put("password", password);
                newUser.put("permission", 0);

                // 添加用户到用户列表
                users.put(username, newUser);

                // 将用户列表写入文件
                writeUsersFile(users);

                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static JSONObject readUsersFile() throws IOException {
        String fileContent = new String(Files.readAllBytes(Paths.get(USERS_FILE)), StandardCharsets.UTF_8);
        return new JSONObject(fileContent);
    }

    private static void writeUsersFile(JSONObject users) throws IOException {
        Files.write(Paths.get(USERS_FILE), users.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static void main(String[] args) {
    }
}
