TINYMSG BY glowingstone124
version: alpha1
just clone my repo and everything is set
TinyMSG使用1234端口进行通信，请确认您放行了1234端口，您也可以在config.json中手动更改通讯端口
注意：你需要在client.java中手动更改端口和域名信息来匹配服务器，我会在后续更新中加入cfg
此聊天软件暂时只支持英语，后续会加入中文支持
启动服务器：
在windows上，确认你安装了openjdk17或者以上的java版本并且包含jre
执行start_server.bat打开服务端
执行start_client.bat打开客户端
在linux上，确认你安装了openjdk17或者以上的java版本并且包含jre
执行sh start_server.sh打开服务端
注意：在文件中注释了sudo ufw allow 1234/tcp
如果您不确定您的服务器是否开放1234端口，请取消此注释并运行start_server.sh，一般情况下ufw会放行1234端口
执行sh start_client.sh打开客户端
注意：在linux部分目录运行时可能需要root权限，可以在sh前加上sudo或使用root账户运行
在客户端，使用/exit来结束服务器进程


TinyMSG uses port 1234 for communication. Please make sure that you have allowed port 1234 in your firewall settings. You can also manually change the communication port in the config.json file.

Note: You need to manually update the port and hostname information in the Client.java file to match your server. I will add a configuration file (cfg) in future updates.

This chat application currently only supports English. Chinese support will be added in the future.

To start the server:
On Windows, make sure you have installed OpenJDK 17 or a higher version of Java that includes JRE. Execute start_server.bat to open the server. Execute start_client.bat to open the client.

On Linux, make sure you have installed OpenJDK 17 or a higher version of Java that includes JRE. Execute sh start_server.sh to open the server.
Note: In the file, the command sudo ufw allow 1234/tcp is commented out. If you are unsure whether port 1234 is open on your server, uncomment this line and run start_server.sh. In most cases, ufw will allow port 1234.

Execute sh start_client.sh to open the client.
Note: Running in certain directories on Linux may require root privileges. You can prefix sudo before sh or run it with the root account.

On the client side, use /exit to terminate the server process.
