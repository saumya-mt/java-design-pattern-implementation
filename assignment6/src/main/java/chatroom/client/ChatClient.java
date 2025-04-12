/**
 * A client implementation for the chat room application.
 * Handles communication with the server, including connecting, disconnecting,
 * sending messages, and receiving responses.
 */
package chatroom.client;

import chatroom.message.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Represents a chat client that can connect to a chat server and communicate with other clients.
 * Provides functionality for sending/receiving messages and managing the connection.
 */
public class ChatClient {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;
    private ConsoleUI ui;
    private boolean running;

    /**
     * Constructs a new ChatClient with the specified username.
     *
     * @param username The username to identify this client in the chat room
     */
    public ChatClient(String username) {
        this.username = username;
        this.ui = new ConsoleUI(this);
    }

    /**
     * Sets the UI component for this client. Used primarily for testing.
     *
     * @param ui The ConsoleUI instance to use for this client
     */
    public void setUI(ConsoleUI ui) {
        this.ui = ui;
    }

    /**
     * Sets the socket for this client. Used primarily for testing.
     *
     * @param socket The socket to use for this client
     * @throws IOException If there is an error setting up the streams
     */
    public void setSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    /**
     * Connects to the chat server at the specified host and port.
     * Initializes the connection, starts the message listener thread,
     * sends the connect message, and starts the UI.
     *
     * @param host The server hostname
     * @param port The server port number
     */
    public void connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            running = true;

            // Start message listener thread
            new Thread(this::receiveMessages).start();
            
            // Send connect message
            sendMessage(MessageFactory.createConnectMessage(username));
            
            // Start UI
            ui.start();
            
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }

    /**
     * Background thread that continuously listens for messages from the server.
     * Handles different types of messages and updates the UI accordingly.
     */
    private void receiveMessages() {
        try {
            while (running) {
                // Read message type
                int messageType = in.readInt();
                
                // Validate message type
                if (messageType < 19 || messageType > 27) {
                    System.err.println("Invalid message type received: " + messageType);
                    // Skip the rest of the message
                    while (in.available() > 0) {
                        in.skipBytes(in.available());
                    }
                    continue;
                }
                
                handleServerMessage(messageType);
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("Connection to server lost: " + e.getMessage());
                disconnect();
            }
        }
    }

    /**
     * Handles a message received from the server.
     * Validates the message type and processes it accordingly.
     *
     * @throws IOException If there is an error reading from the input stream
     */
    private void handleServerMessage(int messageType) throws IOException {
        try {
            
            synchronized (this) {
                switch (messageType) {
                    case MessageFactory.CONNECT_MESSAGE:
                        handleConnectMessage();
                        break;
                    case MessageFactory.CONNECT_RESPONSE:
                        handleConnectResponse();
                        break;
                    case MessageFactory.DISCONNECT_MESSAGE:
                        handleDisconnectMessage();
                        break;
                    case MessageFactory.BROADCAST_MESSAGE:
                        handleBroadcastMessage();
                        break;
                    case MessageFactory.DIRECT_MESSAGE:
                        handleDirectMessage();
                        break;
                    case MessageFactory.FAILED_MESSAGE:
                        handleFailedMessage();
                        break;
                    case MessageFactory.QUERY_USER_RESPONSE:
                        handleQueryResponse();
                        break;
                    case MessageFactory.SEND_INSULT:
                        handleInsultMessage();
                        break;
                    default:
                        System.err.println("Unknown message type from server: " + messageType);
                        // Skip any remaining bytes for this message
                        if (in.available() > 0) {
                            in.skipBytes(in.available());
                        }
                }
            }
            
            // After handling any message, check for and skip any extra bytes
            if (in.available() > 0) {
                System.err.println("Warning: Extra bytes after message type " + messageType);
                in.skipBytes(in.available());
            }
        } catch (Exception e) {
            System.err.println("Error handling message type " + messageType + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles a connect message received from the server.
     * Displays a message indicating that a new user has joined the chat.
     *
     * @throws IOException If there is an error reading from the input stream
     */
    private void handleConnectMessage() throws IOException {
        int usernameLength = in.readInt();
        byte[] usernameBytes = new byte[usernameLength];
        in.readFully(usernameBytes);
        String username = new String(usernameBytes, StandardCharsets.UTF_8);
        ui.displayMessage(username + " has joined the chat");
    }

    /**
     * Handles a connect response message from the server.
     * Displays success or error message and disconnects if connection failed.
     *
     * @throws IOException If there is an error reading from the input stream
     */
    private void handleConnectResponse() throws IOException {
        boolean success = in.readBoolean();
        int messageLength = in.readInt();
        byte[] messageBytes = new byte[messageLength];
        in.readFully(messageBytes);
        String message = new String(messageBytes, StandardCharsets.UTF_8);
        if (success) {
            ui.displayMessage(message);
        } else {
            ui.displayError(message);
            disconnect();
        }
    }

    /**
     * Handles a disconnect message received from the server.
     * Displays a message indicating that a user has left the chat.
     *
     * @throws IOException If there is an error reading from the input stream
     */
    private void handleDisconnectMessage() throws IOException {
        int usernameLength = in.readInt();
        byte[] usernameBytes = new byte[usernameLength];
        in.readFully(usernameBytes);
        String username = new String(usernameBytes, StandardCharsets.UTF_8);
        ui.displayMessage(username + " has left the chat");
    }

    /**
     * Handles a broadcast message received from the server.
     * Displays the message with the sender's username.
     *
     * @throws IOException If there is an error reading from the input stream
     */
    private void handleBroadcastMessage() throws IOException {
        int senderLength = in.readInt();
        byte[] senderBytes = new byte[senderLength];
        in.readFully(senderBytes);
        String sender = new String(senderBytes, StandardCharsets.UTF_8);

        int messageLength = in.readInt();
        byte[] messageBytes = new byte[messageLength];
        in.readFully(messageBytes);
        String message = new String(messageBytes, StandardCharsets.UTF_8);

        ui.displayMessage(sender + ": " + message);
    }

    /**
     * Handles a direct message received from the server.
     * Displays the message if this client is the recipient.
     *
     * @throws IOException If there is an error reading from the input stream
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

        // Only display if we are the recipient
        if (recipient.equals(username)) {
            ui.displayMessage(sender + " -> " + recipient + ": " + content);
        }
    }

    /**
     * Handles a failed message response from the server.
     * Displays the error message to the user.
     *
     * @throws IOException If there is an error reading from the input stream
     */
    private void handleFailedMessage() throws IOException {
        int messageLength = in.readInt();
        byte[] messageBytes = new byte[messageLength];
        in.readFully(messageBytes);
        String message = new String(messageBytes, StandardCharsets.UTF_8);
        ui.displayError(message);
    }

    /**
     * Handles a query response message from the server.
     * Displays a list of all connected users.
     *
     * @throws IOException If there is an error reading from the input stream
     */
    private void handleQueryResponse() throws IOException {
        // Read number of users
        int numUsers = in.readInt();
        StringBuilder userList = new StringBuilder("Connected users: ");
        
        // Read each username
        for (int i = 0; i < numUsers; i++) {
            int usernameLength = in.readInt();
            byte[] usernameBytes = new byte[usernameLength];
            in.readFully(usernameBytes);
            String username = new String(usernameBytes, StandardCharsets.UTF_8);
            
            userList.append(username);
            if (i < numUsers - 1) {
                userList.append(", ");
            }
        }
        
        ui.displayMessage(userList.toString());
        
        // Ensure we don't read any extra bytes
        if (in.available() > 0) {
            System.err.println("Warning: Extra bytes after query response");
            in.skipBytes(in.available());
        }
    }

    /**
     * Handles an insult message received from the server.
     * Displays the insult if this client is the recipient.
     *
     * @throws IOException If there is an error reading from the input stream
     */
    private void handleInsultMessage() throws IOException {
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

        // Read insult content
        int contentLength = in.readInt();
        byte[] contentBytes = new byte[contentLength];
        in.readFully(contentBytes);
        String insult = new String(contentBytes, StandardCharsets.UTF_8);

        if (recipient.equals(username)) {
            ui.displayMessage(sender + " insulted you: " + insult);
        }
    }

    /**
     * Sends a broadcast message to all connected users.
     *
     * @param message The message to broadcast
     */
    public void sendBroadcastMessage(String message) {
        sendMessage(MessageFactory.createBroadcastMessage(username, message));
    }

    /**
     * Sends a direct message to a specific user.
     *
     * @param recipient The username of the recipient
     * @param message The message to send
     */
    public void sendDirectMessage(String recipient, String message) {
        if (recipient == null || recipient.isEmpty()) {
            ui.displayError("Recipient cannot be empty");
            return;
        }
        if (recipient.equals(username)) {
            ui.displayError("Cannot send message to yourself");
            return;
        }
        if (message == null || message.isEmpty()) {
            ui.displayError("Message cannot be empty");
            return;
        }
        sendMessage(MessageFactory.createDirectMessage(username, recipient, message));
    }

    /**
     * Sends an insult to a specific user.
     *
     * @param recipient The username of the recipient
     */
    public void sendInsult(String recipient) {
        if (recipient == null || recipient.isEmpty()) {
            ui.displayError("Recipient cannot be empty");
            return;
        }
        if (recipient.equals(username)) {
            ui.displayError("Cannot insult yourself");
            return;
        }
        sendMessage(MessageFactory.createSendInsultMessage(username, recipient));
    }

    /**
     * Queries the server for a list of connected users.
     */
    public void queryConnectedUsers() {
        Message queryMessage = MessageFactory.createQueryConnectedUsers(username);
        sendMessage(queryMessage);
    }

    /**
     * Disconnects from the chat server.
     * Sends a disconnect message and closes the socket.
     */
    public void disconnect() {
        if (running) {
            running = false;
            sendMessage(MessageFactory.createDisconnectMessage(username));
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    /**
     * Sends a message to the server.
     * Handles serialization and error handling.
     *
     * @param message The message to send
     */
    private void sendMessage(Message message) {
        try {
            if (socket == null || socket.isClosed()) {
                ui.displayError("Error sending message: Connection is closed");
                return;
            }
            byte[] data = message.serialize();
            out.write(data);
            out.flush();
            
            // Add small delay to prevent message flooding
            Thread.sleep(50);
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
            ui.displayError("Error sending message: " + e.getMessage());
            disconnect();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Gets the username of this client.
     *
     * @return The username
     */
    public String getUsername() {
        return username;
    }
} 