package org.qo.tinymsg;


import java.util.Objects;
import java.util.Scanner;

public class ConsoleCommandHandler implements Runnable {
    private Server server;
    public static Debugger debugger;

    public ConsoleCommandHandler(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        Debugger debugger = new Debugger();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();

            if (command.startsWith("/token")) {
                System.out.println("Server token is " + server.getAccessToken());
            } else if (command.startsWith("/adduser")) {
                String[] commandParts = command.split(" ");
                if (commandParts.length == 3) {
                    String newUsername = commandParts[1];
                    String newPassword = commandParts[2];
                    boolean success = server.addUser(newUsername, newPassword);
                    if (success) {
                        server.log("User " + newUsername + " added successfully.", 0);
                    } else {
                        System.out.println("Failed to add user " + newUsername + ".");
                    }
                } else {
                    server.log("Invalid command format. Please use '/adduser <username> <password>'.", 1);
                }
            } else if (command.startsWith("/help")) {
                System.out.println("""
                        /help for a list of avaliable commands.
                        /token to show server token
                        /adduser <username> <password> to add a USER.
                        /stop to stop server.
                        /addadmin <username> <password> to add a Administrator.
                        /deluser <username> to delete a USER.
                        /inlog <log> to create a server-side log. (debug)
                        /outlog <log> to output a log to log file. (debug)
                        /pic <picture path> <NSFW status> to broadcast a pic.
                        """);
            } else if (command.startsWith("/stop")) {
                server.log("retype '/stop Confirm' to stop the server", 0);
                if (command.startsWith("/stop")) {
                    String CommandParts[] = command.split(" ");
                    if (CommandParts.length == 2) {
                        String willstop = CommandParts[1];
                        if (Objects.equals(willstop, "Confirm")) {
                            System.out.println("Exiting.");
                            System.exit(0);
                        }
                    }
                } else {
                    System.out.println("unCorrect argument.");
                }
            } else if (command.startsWith("/kick")) {
                String CommandParts[] = command.split(" ");
                if (CommandParts.length == 2) {
                    String kickuser = CommandParts[1];
                } else {
                    server.log("unCorrect argument.", 1);
                }
            } else if (command.startsWith("/pic")){
                String[] commandParts = command.split(" ");
                if (commandParts.length == 3) {
                    String Filepth = commandParts[1];
                    String NSFW = commandParts[2];
                    if (!server.NOPIC){
                            server.broadcastMessage("@pic " + Filepth + " " + NSFW);
                            server.log("Successfully broadcasted a picture.", 0);
                    } else {
                        server.log("server disabled picture send.", 2);
                    }
                } else {
                    server.log("Invalid command format. Please use '/pic <picpath> <nsfw status (true/false)>'.", 1);
                }
            } else if (command.startsWith("/addadmin")) {
                String[] commandParts = command.split(" ");
                if (commandParts.length == 3) {
                    String newUsername = commandParts[1];
                    String newPassword = commandParts[2];
                    boolean success = server.addAdmin(newUsername, newPassword);
                    if (success) {
                        server.log("User " + newUsername + " added successfully.", 0);
                    } else {
                        System.out.println("Failed to add user " + newUsername + ".");
                    }
                } else {
                    server.log("Invalid command format. Please use '/adduser <username> <password>'.", 1);
                }
            } else if (command.startsWith("/deluser")){
                String[] commandParts = command.split(" ");
                if (commandParts.length == 2) {
                    String username = commandParts[1];
                    boolean success = server.delUser(username);
                    if (success) {
                        server.log("Deleted user " + username + "." ,0);
                    } else {
                        server.log("unable to delete user " + username, 1);
                    }
                } else {
                    server.log("Invalid command format. Please use '/deluser <username>'.", 1);
                }
            } else if(command.startsWith("/inlog")){
                String[] commandParts = command.split(" ");
                if (commandParts.length >= 2){
                    StringBuilder msgBuilder = new StringBuilder();
                    if (commandParts[1].startsWith("\"")){
                        for (int i = 1; i<commandParts.length; i++) {
                            msgBuilder.append(commandParts[i]).append(" ");
                            if (commandParts[i].endsWith("\n")) {
                                break;
                            }
                        }
                    } else {
                        msgBuilder.append(commandParts[1]);
                    }
                    String msg = msgBuilder.toString().trim();
                    if (msg.startsWith("\"") && msg.endsWith("\"")){
                        // Remove the surrounding quotes if present
                        msg = msg.substring(1, msg.length() - 1);
                    }
                    debugger.log(msg, 3);
                } else {
                    server.log("Invalid command format.", 1);
                }
            } else if(command.startsWith("/outlog")){
                String[] commandParts = command.split(" ");
                if (commandParts.length >= 2){
                    StringBuilder msgBuilder = new StringBuilder();
                    if (commandParts[1].startsWith("\"")){
                        // Combine the parts within quotes
                        for (int i = 1; i < commandParts.length; i++){
                            msgBuilder.append(commandParts[i]).append(" ");
                            if (commandParts[i].endsWith("\"")){
                                break;
                            }
                        }
                    } else {
                        msgBuilder.append(commandParts[1]);
                    }

                    String msg = msgBuilder.toString().trim();
                    if (msg.startsWith("\"") && msg.endsWith("\"")){
                        // Remove the surrounding quotes if present
                        msg = msg.substring(1, msg.length() - 1);
                    }

                    debugger.outputlog("[UOP] " + msg);
                } else {
                    server.log("Invalid command format.", 1);
                }
            } else {
                server.log("Unknown command. Type '/help' for a list of available commands.", 1);
        }
        }
    }
}
