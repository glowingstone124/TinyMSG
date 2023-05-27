import org.json.JSONObject;
import org.json.JSONException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

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
    private String accesstoken;
    private String unverifytoken;
    long Timestamp = System.currentTimeMillis();

    public Server() {
        loadConfig();
        clients = new ArrayList<>();
        onlineUsers = new ArrayList<>();
        ServerVersion = "Alpha 1.3";
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
                    accesstoken = jsonConfig.getString("token");
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
            accesstoken = TokenGenerate();

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
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            ServerSocket serverSocket = new ServerSocket(port);

            // Display server startup message
            System.out.println("[start] TinyMSG Server " + ServerVersion + " Started! Bind at " + port + " port, output file name is " + accessFile);
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
        broadcastMessage(message,"ALL");
    }
    private void broadcastMessage(String message, String specify) {
        // Broadcast the message to all connected clients
            for (ClientHandler client : clients) {
                if (specify.contentEquals("ALL") || specify.contentEquals(client.username)) client.sendMessage(message);
            }
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        public String username;
        @SuppressWarnings("Unused")
        private boolean isSender(ClientHandler sender) {
            return this == sender;
        }
        public ClientHandler(Socket socket) {
            clientSocket = socket;

        }

        private void disconnectClient(boolean doBroadcast) {
            if(doBroadcast) {
                broadcastMessage("[server] user " + username + " disconnected.");
                System.out.println("[server] user " + username + " disconnected.");
            }
            clients.remove(this);
            onlineUsers.remove(username);

            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void run() {
            try {
                // Get input and output streams for the client connection
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Prompt for username

                // Receive the username from the client
                username = in.readLine();

                // Prompt for password

                // Receive the password from the client
                String password = in.readLine();
                unverifytoken = in.readLine();
                if (Objects.equals(unverifytoken, accesstoken)) {
                    if (verifyCredentials(username, password)) {
                        // Check if the user is already online
                        if (isUserOnline(username)) {
                            out.println("[ERROR] User is already logged in. Disconnected.");
                            return;
                        }

                        // Add the user to the online users list
                        onlineUsers.add(username);

                        // Send server message to the client
                        out.println(srvmsg);
                    } else {
                        out.println("[ERROR] Invalid username or password. Disconnected.");
                        disconnectClient(true);
                        return;
                    }
                } else {
                    out.println("[ERROR] Wrong token! If you believe this is an error, please contact with your server administrator.");
                    System.out.println("[server] user " + username + " disconnected. reason: wrong token");
                }
                // Verify the username and password

                while (true) {
                    // Receive client messages
                    String clientMessage = in.readLine();

                    if (clientMessage.equalsIgnoreCase("/version")) {
                        out.println("[server] TinyMSG server version " + ServerVersion);
                        clientMessage = "";
                    }
                    if (clientMessage.equalsIgnoreCase("/help")) {
                        out.println("[server] TinyMSG server version " + ServerVersion);
                        out.println("[server] /exit to Disconnect");
                        out.println("[server] /version to show Server Version");
                        out.println("[server] /list to show online users");
                        out.println("[server] /permission to show your permission level");
                        out.println("[server] /stop to stop the server");
                        clientMessage = "";
                    }
                    if (clientMessage.equalsIgnoreCase("/list")) {
                        // Send the current online user list to the client
                        sendOnlineUsersList();
                        clientMessage = "";
                    }
                    if (clientMessage.equalsIgnoreCase("/permission")) {
                        // Send the user's permission level to the client
                        String permissionLevel = showPermission(username);
                        out.println("[server] Your permission level: " + permissionLevel);
                        clientMessage = "";
                    }
                    if (clientMessage.equalsIgnoreCase("/token")) {
                        broadcastMessage("[server] server token is " + accesstoken);
                        clientMessage = "";
                    }
                    if (clientMessage.equalsIgnoreCase("/exit")) {
                       disconnectClient(true);
                       clientMessage = "";
                    }
                    if (clientMessage.equalsIgnoreCase("/stop")) {
                        if(isAdmin(username)) {

                            System.exit(0);
                        } else {
                            broadcastMessage("You don't have permission to DO THAT!",username);
                            clientMessage = "";
                        }
                    }

                    if (clientMessage == null) {
                        // Client has disconnected
                        break;
                    }

                    if (clientMessage != null && !clientMessage.isEmpty()) {
                        // Display the received message on the server console
                        System.out.println("[Client:" + username + "] " + clientMessage);

                        // Broadcast the message to all connected clients
                        broadcastMessage("[Client:" + username + "] " + clientMessage);
                    }

                }

                disconnectClient(false);
            } catch (IOException e) {
                e.printStackTrace();
                disconnectClient(true);
            }

        }

        private boolean isAdmin(String username) {
            try {
                String userContent = readFile(USER_PROFILE);
                if (userContent != null) {
                    JSONObject userProfiles = new JSONObject(userContent);
                    if (userProfiles.has(username)) {
                        JSONObject userProfile = userProfiles.getJSONObject(username);
                        int permissionlvl = userProfile.getInt("permission");
                        if (Objects.equals(permissionlvl, 1)) {
                            return true;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return false;
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

    private String showPermission(String username) {
        try {
            String userContent = readFile(USER_PROFILE);
            if (userContent != null) {
                JSONObject userProfiles = new JSONObject(userContent);
                if (userProfiles.has(username)) {
                    JSONObject userProfile = userProfiles.getJSONObject(username);
                    int userPermissionLevel = userProfile.getInt("permission");
                    if (userPermissionLevel == 1) {
                        String userPermissionReturn = "admin";
                        return String.valueOf(userPermissionReturn);
                    }
                    if (userPermissionLevel == 0) {
                        String userPermissionReturn = "user";
                        return String.valueOf(userPermissionReturn);
                    }
                    if (userPermissionLevel == 2) {
                        String userPermissionReturn = "System";
                        return String.valueOf(userPermissionReturn);
                    } else {
                        broadcastMessage("[ERROR] Illegal Argument: Permission in user:" + username);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return username;
    }

    private boolean isUserOnline(String username) {
        return onlineUsers.contains(username);
    }

    private void sendOnlineUsersList() {
        StringBuilder userList = new StringBuilder();
        for (String user : onlineUsers) {
            userList.append("\n").append(user);
        }
        broadcastMessage("[server] Online Users:" + userList);
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
    private String TokenGenerate() {
        int length = 256;
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        broadcastMessage(sb.toString());
        String token = sb.toString();
        return sb.toString();
    }

}
