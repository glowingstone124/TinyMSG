package org.qo.tinymsg;

import java.util.Objects;
import java.util.Scanner;

public class ConsoleCommandHandler implements Runnable {
    private Server server;

    public ConsoleCommandHandler(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
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
                        System.out.println("User " + newUsername + " added successfully.");
                    } else {
                        System.out.println("Failed to add user " + newUsername + ".");
                    }
                } else {
                    System.out.println("Invalid command format. Please use '/adduser <username> <password>'.");
                }
            } else if (command.startsWith("/help")) {
                System.out.println("""
                        /help for a list of avaliable commands.
                        /token to show server token
                        /adduser <username> <password> to add a USER.
                        /stop to stop server.
                        """);
            } else if (command.startsWith("/stop")) {
                System.out.println("retype '/stop Confirm' to stop the server");
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
            } else {
                System.out.println("Unknown command. Type '/help' for a list of available commands.");
            }
        }
    }
}
