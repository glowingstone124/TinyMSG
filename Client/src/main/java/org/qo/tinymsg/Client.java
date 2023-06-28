package org.qo.tinymsg;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.File;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Objects;
import java.net.URL;
import java.util.Scanner;


public class Client{
    private String serverAddress;
    public static String ISNSFW;
    public String fromSource;
    private int serverPort;
    private String token;
    private static final String CONFIG_FILE = "client_cfg.json";
    public String Source;
    public String localip;
    public int localport;
    public boolean AcceptNSFW;
    public double ClientVersion = 17;
    public double version;
    public Client() {
        loadConfig();
    }
    Debugger debugger = new Debugger();
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
                localip = jsonConfig.getString("serverAddress");
                localport = jsonConfig.getInt("serverPort");
                token = jsonConfig.getString("token");
                Source = jsonConfig.getString("Source");
                fromSource = jsonConfig.getString("fromSource");
                AcceptNSFW = jsonConfig.getBoolean("AcceptNSFW");
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
            jsonConfig.put("Source", "https://tmsg.glowingstone.cn/source.json");
            jsonConfig.put("fromSource", "NO");
            jsonConfig.put("AcceptNSFW", false);
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
        String ip = localip;
        int port = localport;
        try {
            if (Objects.equals(fromSource, "YES")) {
                try {
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("Fetching Servers from Source...");
                    // 创建URL对象并打开连接
                    URL url = new URL(Source);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    // 读取JSON文件内容
                    StringBuilder jsonString = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        jsonString.append(line);
                    }
                    reader.close();
                            // 解析JSON数据
                    JSONObject json = new JSONObject(jsonString.toString());
                    String source = json.getString("Source");
                            String lastUpdate = json.getString("Last-Update");
                            JSONObject serverList = json.getJSONObject("Server-list");
                            System.out.println("Source: " + source);
                            System.out.println("Last Update: " + lastUpdate);
                            System.out.println("Server List:");

                            // 创建Scanner对象以接收用户输入

                            // 显示服务器选项
                            int index = 1;
                            for (String server : serverList.keySet()) {
                                System.out.println("(" + index + ") " + server);
                                index++;
                            }

                            // 提示用户选择服务器
                            System.out.print("Please select a server: ");
                            int choice = scanner.nextInt();
                            scanner.nextLine(); // 读取换行符

                            // 验证用户输入
                            if (choice < 1 || choice > serverList.length()) {
                                System.out.println("Invalid choice!");
                            } else {
                                // 获取选中的服务器
                                String selectedServer = serverList.names().getString(choice - 1);
                                JSONObject serverDetails = serverList.getJSONObject(selectedServer);
                                ip = serverDetails.getString("ip");
                                port = serverDetails.getInt("port");
                                version = serverDetails.getDouble("version");
                                System.out.println("Selected Server: " + selectedServer);
                                System.out.println("IP: " + ip);
                                System.out.println("Port: " + port);
                                if (version > ClientVersion) {
                                    System.out.println("You are using outdated client. Please notice that will be some error or unsupported features. We recommend you to user the latest version of TinyMSG. If you are running a Third-Party client, please ask your client provider.");
                                }
                            }

                            // 关闭Scanner
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input! Default connect with local cfg.");
                        }
            }
            Socket socket = new Socket(ip, port);
            // 获取输入流，用于接收服务器发送的数据
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // 获取输出流，用于向服务器发送数据
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            // 显示客户端启动消息
            System.out.println("TinyMSG client start");

            if (Objects.equals(token, "Your custom token here")) {
                System.out.println("Please edit client_cfg.json to configure your own token. You can not connect to a server without a token. Make sure that your token is same with the server.");
            } else {
                //
            }
            System.out.println("[client] you are now connecting to " + ip + " port " + port);

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
                if (serverMessage.startsWith("@pic")) {
                    String[] CommandParts = serverMessage.split(" ");
                    String[] PicPath = new String[]{CommandParts[1]};
                    String[] NSFW = new String[]{CommandParts[2]};
                    ISNSFW = Arrays.toString(NSFW);
                    if (Objects.equals("ISNSFW", "true") && !AcceptNSFW) {
                        System.out.println("PREVENTED AN NSFW PICTURE.");
                    } else {
                        System.out.println("Server broadcasted a Picture from : " + Arrays.toString(PicPath) + " NSFW: " + Arrays.toString(NSFW));
                        ImageShow.main(PicPath);
                    }
                } else {
                    // 在客户端打印接收到的数据
                    System.out.println(serverMessage);
                }
            }

            // 关闭连接
            //socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}