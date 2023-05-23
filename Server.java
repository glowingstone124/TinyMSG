import org.json.JSONObject;
import org.json.JSONException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final String CONFIG_FILE = "config_server.json";
    private static final String USER_PROFILE = "users.json";
    public String ServerVersion;

    private int port;
    private String workingDirectory;
    private String accessFile;
    private String srvmsg;
    private List<ClientHandler> clients;
    private List<String> onlineUsers;

    public Server() {
        loadConfig();
        clients = new ArrayList<>();
        onlineUsers = new ArrayList<>();
        ServerVersion = "Alpha 1.2";
    }

    private void loadConfig() {
        // Load server configuration
        if (fileExists(CONFIG_FILE)) {
            // If the configuration file exists, read the configuration
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
            // Configuration file doesn't exist, use default configuration and generate the configuration file
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
            // Create ServerSocket object and bind it to the listening port
            ServerSocket serverSocket = new ServerSocket(port);

            // Display server startup message
            System.out.println("TinyMSG Server " + ServerVersion + " Started! Bind at " + port + " port, output file name is " + accessFile);

            while (true) {
                // Listen for client connection requests
                Socket clientSocket = serverSocket.accept();

                // Create a new client handler thread
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);

                // Start the thread to handle the client connection
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcastMessage(String message) {
        // Broadcast the message to all connected clients
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            clientSocket = socket;
        }

        public void run() {

            try {

                // Get input and output streams for the client connection
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Prompt for username

                // Receive the username from the client
                String username = in.readLine();

                // Prompt for password

                // Receive the password from the client
                String password = in.readLine();

                // Verify the username and password
                if (verifyCredentials(username, password)) {
                    // Send server message to the client
                    out.println(srvmsg);
                } else {
                    out.println("Invalid username or password. Disconnected.");
                    return;
                }

                while (true) {
                    // Receive client messages
                    String clientMessage = in.readLine();
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
                        out.println("/list to show online users");
                    }
                    if (clientMessage.equalsIgnoreCase("/list")) {
                        // 发送当前在线用户列表给客户端
                        sendOnlineUsersList();
                    }

                    if (clientMessage == null) {
                        // Client has disconnected
                        break;
                    }

                    // Display the received message on the server console
                    System.out.println("[Client: " + username + "] " + clientMessage);

                    // Broadcast the message to all connected clients
                    broadcastMessage("[Client: " + username + "] " + clientMessage);
                }

                // Client has disconnected, remove the client handler from the list
                clients.remove(this);

                // Close the client connection
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void sendMessage(String message) {
            // Send a message to the client
            out.println(message);
        }
    }

    private boolean verifyCredentials(String username, String password) {
        try {
            String userContent = readFile(USER_PROFILE);
            if (userContent != null) {
                JSONObject userProfiles = new JSONObject(userContent);
                if (userProfiles.has(username)) {
                    JSONObject userProfile = userProfiles.getJSONObject(username);
                    String storedPassword = userProfile.getString("password");
                    return password.equals(storedPassword);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
    private void sendOnlineUsersList() {
        StringBuilder userList = new StringBuilder();
        for (String user : onlineUsers) {
            userList.append(user).append("\n");
        }
        broadcastMessage("Online Users:\n" + userList.toString());
    }
}
