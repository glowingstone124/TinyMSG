import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private String serverAddress;
    private int serverPort;

    public Client(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void start() {
		System.setProperty("file.encoding", "UTF-8");
        try {
            // 连接服务器
            Socket socket = new Socket(serverAddress, serverPort);

            // 获取输入流，用于接收服务器发送的数据
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 获取输出流，用于向服务器发送数据
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // 显示客户端启动消息
            System.out.println("text client start");

            // 从控制台读取用户输入的账户名称
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("请输入账户名称: ");
            String accountName = reader.readLine();

            // 向服务器发送账户名称
            out.println(accountName);

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
                    break;
                }

                // 在客户端打印接收到的数据
                System.out.println("[聊天] " + serverMessage);
            }

            // 关闭连接
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 1234;

        Client client = new Client(serverAddress, serverPort);
        client.start();
    }
}
