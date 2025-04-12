package chatroom.server;

import chatroom.message.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Handles communication with a single client in the chat server.
 * Manages the client's connection, message processing, and disconnection.
 */
public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final ChatServer server;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;
    private boolean running;

    /**
     * Constructs a new ClientHandler for the given socket and server.
     *
     * @param socket The socket connected to the client
     * @param server The ChatServer instance managing this client
     */
    public ClientHandler(Socket socket, ChatServer server) {
        this.clientSocket = socket;
        this.server = server;
    }

    /**
     * Main thread method that handles client communication.
     * Sets up input/output streams and processes messages until the client disconnects.
     */
    @Override
    public void run() {
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            running = true;

            while (running) {
                handleMessage();
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    /**
     * Processes a single message from the client.
     * Reads the message type and delegates to appropriate handler methods.
     *
     * @throws IOException If there is an error reading from or writing to the socket streams
     */
    private void handleMessage() throws IOException {
        try {
            // Read message type
            int messageType = in.readInt();
            System.out.println("DEBUG: Server received message type: " + messageType);
            
            // Validate message type
            if (messageType < 19 || messageType > 27) {
                System.err.println("Invalid message type received: " + messageType);
                // Skip the rest of the message
                in.skipBytes(in.available());
                sendMessage(MessageFactory.createFailedMessage("Invalid message type: " + messageType));
                return;
            }
            
            synchronized (this) {
                switch (messageType) {
                    case MessageFactory.CONNECT_MESSAGE:
                        handleConnect();
                        break;
                    case MessageFactory.DISCONNECT_MESSAGE:
                        handleDisconnect();
                        break;
                    case MessageFactory.BROADCAST_MESSAGE:
                        if (username == null) {
                            sendMessage(MessageFactory.createFailedMessage("Not connected"));
                            break;
                        }
                        handleBroadcast();
                        break;
                    case MessageFactory.DIRECT_MESSAGE:
                        if (username == null) {
                            sendMessage(MessageFactory.createFailedMessage("Not connected"));
                            break;
                        }
                        handleDirectMessage();
                        break;
                    case MessageFactory.SEND_INSULT:
                        if (username == null) {
                            sendMessage(MessageFactory.createFailedMessage("Not connected"));
                            break;
                        }
                        handleInsult();
                        break;
                    case MessageFactory.QUERY_CONNECTED_USERS:
                        if (username == null) {
                            sendMessage(MessageFactory.createFailedMessage("Not connected"));
                            break;
                        }
                        handleQueryUsers();
                        break;
                    default:
                        System.err.println("Unknown message type received: " + messageType);
                        in.skipBytes(in.available());
                        sendMessage(MessageFactory.createFailedMessage("Unknown message type: " + messageType));
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling message: " + e.getMessage());
            disconnect();
        }
    }

    /**
     * Handles a connect message from the client.
     * Validates the username and registers the client if successful.
     *
     * @throws IOException If there is an error reading from or writing to the socket streams
     */
    private void handleConnect() throws IOException {
        int usernameLength = in.readInt();
        byte[] usernameBytes = new byte[usernameLength];
        in.readFully(usernameBytes);
        String newUsername = new String(usernameBytes, StandardCharsets.UTF_8);

        if (server.isUsernameTaken(newUsername)) {
            sendMessage(MessageFactory.createFailedMessage("Username already taken"));
            disconnect();
            return;
        }

        this.username = newUsername;
        server.registerClient(username, this);
        sendMessage(MessageFactory.createConnectMessage(username));
        server.broadcast("Server", username + " has joined the chat");
    }

    /**
     * Handles a disconnect message from the client.
     * Removes the client from the server and closes the connection.
     *
     * @throws IOException If there is an error reading from or writing to the socket streams
     */
    private void handleDisconnect() {
        if (username != null) {
            server.removeClient(username);
        }
        disconnect();
    }

    /**
     * Handles a broadcast message from the client.
     * Forwards the message to all connected clients.
     *
     * @throws IOException If there is an error reading from or writing to the socket streams
     */
    private void handleBroadcast() throws IOException {
        int senderLength = in.readInt();
        byte[] senderBytes = new byte[senderLength];
        in.readFully(senderBytes);
        String sender = new String(senderBytes, StandardCharsets.UTF_8);

        int messageLength = in.readInt();
        byte[] messageBytes = new byte[messageLength];
        in.readFully(messageBytes);
        String message = new String(messageBytes, StandardCharsets.UTF_8);
        
        server.broadcast(sender, message);
    }

    /**
     * Handles a direct message from the client.
     * Forwards the message to the specified recipient.
     *
     * @throws IOException If there is an error reading from or writing to the socket streams
     */
    private void handleDirectMessage() throws IOException {
        // Read sender username
        int senderLength = in.readInt();
        byte[] senderBytes = new byte[senderLength];
        in.readFully(senderBytes);
        String sender = new String(senderBytes, StandardCharsets.UTF_8);

        // Read recipient username
        int recipientLength = in.readInt();
        byte[] recipientBytes = new byte[recipientLength];
        in.readFully(recipientBytes);
        String recipient = new String(recipientBytes, StandardCharsets.UTF_8);

        // Read message content
        int contentLength = in.readInt();
        byte[] contentBytes = new byte[contentLength];
        in.readFully(contentBytes);
        String content = new String(contentBytes, StandardCharsets.UTF_8);

        server.sendDirectMessage(sender, recipient, content);
    }

    /**
     * Handles an insult message from the client.
     * Validates the sender and recipient, then sends the insult.
     *
     * @throws IOException If there is an error reading from or writing to the socket streams
     */
    private void handleInsult() throws IOException {
        // Read sender username
        int senderLength = in.readInt();
        byte[] senderBytes = new byte[senderLength];
        in.readFully(senderBytes);
        String sender = new String(senderBytes, StandardCharsets.UTF_8);

        // Read recipient username
        int recipientLength = in.readInt();
        byte[] recipientBytes = new byte[recipientLength];
        in.readFully(recipientBytes);
        String recipient = new String(recipientBytes, StandardCharsets.UTF_8);

        // Check for self-insult
        if (sender.equals(recipient)) {
            sendMessage(MessageFactory.createFailedMessage("Cannot insult yourself"));
            return;
        }

        // Send insult
        server.sendInsult(sender, recipient);
    }

    /**
     * Handles a query for connected users from the client.
     * Sends a list of all currently connected usernames.
     *
     * @throws IOException If there is an error reading from or writing to the socket streams
     */
    private void handleQueryUsers() throws IOException {
        String[] users = server.getConnectedUsers();
        Message response = MessageFactory.createQueryUserResponse(users);
        sendMessage(response);
        
        // Ensure we don't have any extra bytes
        if (in.available() > 0) {
            System.err.println("Warning: Extra bytes after query users request");
            in.skipBytes(in.available());
        }
    }

    /**
     * Sends a message to the client.
     *
     * @param message The message to send
     */
    public void sendMessage(Message message) {
        try {
            byte[] data = message.serialize();
            out.write(data);
            out.flush();
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
            disconnect();
        }
    }

    /**
     * Disconnects the client from the server.
     * Removes the client from the server's list and closes the socket.
     */
    private void disconnect() {
        if (username != null) {
            server.broadcast("Server", username + " has left the chat");
        }
        running = false;
        try {
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing client socket: " + e.getMessage());
        }
    }

    /**
     * Gets the username of this client.
     *
     * @return The client's username
     */
    public String getUsername() {
        return username;
    }
} 