import org.json.JSONException;
import org.json.JSONObject;

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
    private static List<String> onlineUsers = new ArrayList<>();

    public Server() {
        loadConfig();
        clients = new ArrayList<>();
        ServerVersion = "Alpha 1";
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
        StringBuilder content = new StringBuilder();
        try {
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private void writeFile(String filename, String content) {
        try {
            FileWriter fileWriter = new FileWriter(filename);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            PrintWriter printWriter = new PrintWriter(bufferedWriter);
            printWriter.println(content);
            printWriter.close();
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            clientSocket = socket;
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String clientMessage;
                while ((clientMessage = in.readLine()) != null) {
                    // 处理客户端消息
                    if (clientMessage.equalsIgnoreCase("/list")) {
                        sendOnlineUsers();
                    } else {
                        // 其他处理逻辑
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                    out.close();
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void sendOnlineUsers() {
            StringBuilder userList = new StringBuilder();
            userList.append("Online Users:\n");
            synchronized (onlineUsers) {
                for (String user : onlineUsers) {
                    userList.append(user).append("\n");
                }
            }
            out.println(userList.toString());
        }
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("TinyMSG Server " + ServerVersion + " Started! Bind at " + port + " port, output file name is " + accessFile);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
