import org.json.JSONObject;
import org.json.JSONException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final String CONFIG_FILE = "config_server.json";
    public String ServerVersion;

    private int port;
    private String workingDirectory;
    private String accessFile;
    private String srvmsg;
    private List<ClientHandler> clients;

    public Server() {
        loadConfig();
        clients = new ArrayList<>();
        ServerVersion = "Alpha 1.1";
    }

    private void loadConfig() {
        if (fileExists(CONFIG_FILE)) {
            // 如果配置文件存在，读取配置
            try {
                String configContent = readFile(CONFIG_FILE);

                if (configContent != null) {
                    JSONObject jsonConfig = new JSONObject(configContent);
                    port = jsonConfig.getInt("port");
                    workingDirectory = jsonConfig.getString("workingDirectory");
                    accessFile = jsonConfig.getString("accessFile");
                    srvmsg = jsonConfig.getString("srvmsg");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // 配置文件不存在，使用默认配置并生成配置文件
            port = 1234;
            workingDirectory = System.getProperty("user.dir");
            accessFile = "text.txt";
            srvmsg = "CONNECT SUCCESS";

            createDefaultConfig();
        }
    }

    private boolean fileExists(String filename) {
        File file = new File(filename);
        return file.exists() && !file.isDirectory();
    }

    private void createDefaultConfig() {
        try {
            JSONObject jsonConfig = new JSONObject();
            jsonConfig.put("port", port);
            jsonConfig.put("workingDirectory", workingDirectory);
            jsonConfig.put("accessFile", accessFile);
            jsonConfig.put("srvmsg", srvmsg);

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
            // 创建ServerSocket对象，并绑定监听端口
            ServerSocket serverSocket = new ServerSocket(port);

            // 显示服务器启动消息
            System.out.println("TinyMSG Server " + ServerVersion + " Started! Bind at " + port + " port, output file name is " + accessFile);

            while (true) {
                // 监听客户端的连接请求
                Socket clientSocket = serverSocket.accept();

                // 创建一个新的客户端处理器线程
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);

                // 启动线程处理客户端连接
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcastMessage(String message) {
        // 广播消息给所有连接的客户端
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    private void writeToFile(String filename, String message) {
        try {
            FileWriter fileWriter = new FileWriter(filename, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            PrintWriter printWriter = new PrintWriter(bufferedWriter);

            printWriter.println(message);

            printWriter.close();
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private String username;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try {
                // 获取输入流，用于接收客户端发送的数据
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // 获取输出流，用于向客户端发送数据
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                // 接收客户端发送的用户名
                username = in.readLine();
                System.out.println("USER " + username + " Connected");

                // Add the username to the list of online users
                synchronized (onlineUsers) {
                    onlineUsers.add(username);
                }

                // 发送服务器消息给客户端
                out.println(srvmsg);

                while (true) {
                    // 从客户端读取数据
                    String clientMessage = in.readLine();

                    if (clientMessage == null) {
                        // 客户端已关闭连接
                        break;
                    }

                    // 在服务器端打印接收到的数据
                    System.out.println("Recived message from " + username + " : " + clientMessage);

                    if (clientMessage.equalsIgnoreCase("/exit")) {
                        // 客户端发送退出命令，断开连接
                        break;
                    }
                    if (clientMessage.equalsIgnoreCase("/version")) {
                        out.println("TinyMSG server version " + ServerVersion);
                    }
                    if (clientMessage.equalsIgnoreCase("/help")) {
                        out.println("TinyMSG server version " + ServerVersion);
                        out.println("/exit to Disconnect");
                        out.println("/version to show Server Version");
                        out.println("/list to show online users")
                    }
                    if (clientMessage.equalsIgnoreCase("/list")) {
                        sendOnlineUsers();
                    }

                    // 将消息写入文件
                    writeToFile(accessFile, clientMessage);

                    // 广播消息给所有连接的客户端
                    broadcastMessage(username + ": " + clientMessage);
                }

                // 关闭连接
                clientSocket.close();

                // Remove the username from the list of online users
                synchronized (onlineUsers) {
                    onlineUsers.remove(username);
                }

                // 从客户端处理器列表中移除当前客户端
                clients.remove(this);

                System.out.println("User " + username + " Disconnected");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String message) {
            // 向客户端发送消息
            out.println(message);
        }
    }
}
