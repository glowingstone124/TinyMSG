package org.qo.tinymsg;
import org.json.JSONObject;
import org.json.JSONException;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.File;


public class Client{
    Label response;
    private String serverAddress;
    private int serverPort;
    private String token;
    private static final String CONFIG_FILE = "client_cfg.json";

    public Client() {
        loadConfig();
    }

    private void loadConfig() {
        if (!fileExists(CONFIG_FILE)) {
            // 配置文件不存在，生成默认配置文件
            createDefaultConfig();
        }

        // 读取配置文件
        try {
            String configContent = readFile(CONFIG_FILE);

            if (configContent != null) {
                JSONObject jsonConfig = new JSONObject(configContent);
                serverAddress = jsonConfig.getString("serverAddress");
                serverPort = jsonConfig.getInt("serverPort");
                token = jsonConfig.getString("token");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean fileExists(String filename) {
        File file = new File(filename);
        return file.exists() && !file.isDirectory();
    }

    private void createDefaultConfig() {
        try {
            JSONObject jsonConfig = new JSONObject();
            jsonConfig.put("serverAddress", "localhost");
            jsonConfig.put("serverPort", 1234);
            jsonConfig.put("token", "Your custom token here");

            writeFile(CONFIG_FILE, jsonConfig.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String readFile(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            reader.close();

            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void writeFile(String filename, String content) {
        try {
            FileWriter writer = new FileWriter(filename);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            // 连接服务器
            Socket socket = new Socket(serverAddress, serverPort);

            // 获取输入流，用于接收服务器发送的数据
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 获取输出流，用于向服务器发送数据
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // 显示客户端启动消息
            System.out.println("TinyMSG client start");
            if (token == "Your custom token here") {
                System.out.println("Please edit client_cfg.json to configure your own token. You can not connect to a server without a token. Make sure that your token is same with the server.");
            }
            System.out.println("[client] you are now connecting to " + serverAddress + " port " + serverPort);

            // 从控制台读取用户输入的账户名称
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("[client] please input your Account name: ");
            String accountName = reader.readLine();
            out.println(accountName);
            System.out.print("[client] please input your password: ");
            String accountPassword = reader.readLine();
            // 向服务器发送账户名称
            //out.println(accountName);
            out.println(accountPassword);
            out.println(token);
            // 创建线程监听用户输入
            Thread inputThread = new Thread(() -> {
                try {
                    while (true) {
                        // 从控制台读取输入
                        String input = reader.readLine();

                        // 向服务器发送数据
                        out.println(input);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            inputThread.start();

            while (true) {
                // 从服务器读取数据
                String serverMessage = in.readLine();

                if (serverMessage == null) {
                    // 服务器已关闭连接
                    System.exit(0);
                }

                // 在客户端打印接收到的数据
                System.out.println(serverMessage);
            }

            // 关闭连接
            //socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}
