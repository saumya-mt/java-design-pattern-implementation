package chatroom.server;

import chatroom.message.*;
import chatroom.util.InsultGenerator;
import chatroom.util.SimpleInsultGenerator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The main server class for the chat application.
 * Handles client connections, message broadcasting, and user management.
 */
public class ChatServer {
    private static final int MAX_CLIENTS = 15;
    private ServerSocket serverSocket;
    private Map<String, ClientHandler> clients;
    private boolean running;
    private final InsultGenerator insultGenerator;

    /**
     * Constructs a new ChatServer instance.
     * Initializes the client map and insult generator.
     */
    public ChatServer() {
        this.clients = new ConcurrentHashMap<>();
        this.insultGenerator = new SimpleInsultGenerator();
    }

    /**
     * Starts the server on the specified port.
     * Listens for incoming client connections and spawns new threads to handle them.
     * Rejects connections if the maximum client limit is reached.
     *
     * @param port The port number to listen on
     */
    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            System.out.println("Server started on port " + port);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                if (clients.size() >= MAX_CLIENTS) {
                    // Send failed message and close connection
                    clientSocket.close();
                    continue;
                }
                ClientHandler handler = new ClientHandler(clientSocket, this);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    /**
     * Broadcasts a message to all connected clients.
     *
     * @param sender The username of the client sending the message
     * @param message The message content to broadcast
     */
    public void broadcast(String sender, String message) {
        Message broadcastMsg = MessageFactory.createBroadcastMessage(sender, message);
        clients.values().forEach(client -> client.sendMessage(broadcastMsg));
    }

    /**
     * Sends a direct message from one client to another.
     * If the recipient is not found, sends a failed message to the sender.
     *
     * @param sender The username of the client sending the message
     * @param recipient The username of the client to receive the message
     * @param message The message content to send
     */
    public void sendDirectMessage(String sender, String recipient, String message) {
        ClientHandler recipientHandler = clients.get(recipient);
        if (recipientHandler == null) {
            ClientHandler senderHandler = clients.get(sender);
            if (senderHandler != null) {
                senderHandler.sendMessage(MessageFactory.createFailedMessage("Recipient not found"));
            }
            return;
        }
        
        Message directMsg = MessageFactory.createDirectMessage(sender, recipient, message);
        recipientHandler.sendMessage(directMsg);
    }

    /**
     * Sends an insult from one client to another.
     * Generates a random insult and sends it as a direct message.
     * If the recipient is not found, sends a failed message to the sender.
     *
     * @param sender The username of the client sending the insult
     * @param recipient The username of the client to receive the insult
     */
    public void sendInsult(String sender, String recipient) {
        // Check if recipient exists
        if (!clients.containsKey(recipient)) {
            ClientHandler senderHandler = clients.get(sender);
            if (senderHandler != null) {
                senderHandler.sendMessage(MessageFactory.createFailedMessage("Recipient not found"));
            }
            return;
        }

        // Generate insult
        String insult = insultGenerator.generateInsult();
        
        // Send direct message to recipient
        Message directMsg = MessageFactory.createDirectMessage(sender, recipient, insult);
        ClientHandler recipientHandler = clients.get(recipient);
        if (recipientHandler != null) {
            recipientHandler.sendMessage(directMsg);
        }
        
        // Send broadcast to all users except recipient
        Message broadcastMsg = MessageFactory.createBroadcastMessage(sender, 
            String.format("%s -> %s: %s", sender, recipient, insult));
        clients.values().forEach(client -> {
            if (!client.getUsername().equals(recipient)) {
                client.sendMessage(broadcastMsg);
            }
        });
    }

    /**
     * Registers a new client with the server.
     *
     * @param username The username of the client to register
     * @param handler The ClientHandler instance for this client
     * @throws IllegalArgumentException if the username is already taken
     */
    public void registerClient(String username, ClientHandler handler) {
        if (clients.containsKey(username)) {
            throw new IllegalArgumentException("Username already taken");
        }
        clients.put(username, handler);
    }

    /**
     * Removes a client from the server's client list.
     *
     * @param username The username of the client to remove
     */
    public void removeClient(String username) {
        ClientHandler handler = clients.remove(username);
        if (handler != null) {
            broadcast("Server", username + " has left the chat");
        }
    }

    /**
     * Checks if a username is already taken by a connected client.
     *
     * @param username The username to check
     * @return true if the username is already in use, false otherwise
     */
    public boolean isUsernameTaken(String username) {
        return clients.containsKey(username);
    }

    /**
     * Gets an array of all currently connected usernames.
     *
     * @return An array of usernames of all connected clients
     */
    public String[] getConnectedUsers() {
        return clients.keySet().toArray(new String[0]);
    }

    /**
     * Shuts down the server and disconnects all clients.
     */
    public void shutdown() {
        running = false;
        // Remove all clients
        clients.clear();
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
    }
} 