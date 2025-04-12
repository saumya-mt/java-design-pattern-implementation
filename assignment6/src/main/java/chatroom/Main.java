package chatroom;

import chatroom.client.ChatClient;
import chatroom.server.ChatServer;

/**
 * Main entry point for the chat room application.
 * Handles command-line arguments to start either a server or client instance.
 */
public class Main {
    /**
     * Main method that processes command-line arguments and starts the appropriate component.
     * 
     * @param args Command-line arguments:
     *            - For server mode: ["server", port]
     *                 where port is the port number to listen on
     *            - For client mode: ["client", host, port, username]
     *                 where host is the server hostname
     *                 where port is the server port number
     *                 where username is the client's username
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage:");
            System.out.println("  Server: java -jar chatroom.jar server <port>");
            System.out.println("  Client: java -jar chatroom.jar client <host> <port> <username>");
            return;
        }

        try {
            if (args[0].equals("server")) {
                if (args.length != 2) {
                    System.out.println("Server usage: java -jar chatroom.jar server <port>");
                    return;
                }
                int port = Integer.parseInt(args[1]);
                ChatServer server = new ChatServer();
                server.start(port);
            } else if (args[0].equals("client")) {
                if (args.length != 4) {
                    System.out.println("Client usage: java -jar chatroom.jar client <host> <port> <username>");
                    return;
                }
                String host = args[1];
                int port = Integer.parseInt(args[2]);
                String username = args[3];
                
                ChatClient client = new ChatClient(username);
                client.connect(host, port);
            } else {
                System.out.println("Unknown command: " + args[0]);
                System.out.println("Use 'server' or 'client'");
            }
        } catch (NumberFormatException e) {
            System.out.println("Port must be a number");
        }
    }
} 