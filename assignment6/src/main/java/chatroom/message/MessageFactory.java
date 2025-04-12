package chatroom.message;

import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Factory class for creating various types of messages in the chat application.
 * Provides static methods to create instances of different message types
 * and defines constants for message type identifiers.
 */
public class MessageFactory {
    // Message type constants
    /** Message type for client connection request */
    public static final int CONNECT_MESSAGE = 19;
    /** Message type for server's response to connection request */
    public static final int CONNECT_RESPONSE = 20;
    /** Message type for client disconnection notification */
    public static final int DISCONNECT_MESSAGE = 21;
    /** Message type for querying connected users */
    public static final int QUERY_CONNECTED_USERS = 22;
    /** Message type for server's response to user query */
    public static final int QUERY_USER_RESPONSE = 23;
    /** Message type for broadcasting messages to all users */
    public static final int BROADCAST_MESSAGE = 24;
    /** Message type for direct messages between users */
    public static final int DIRECT_MESSAGE = 25;
    /** Message type for error notifications */
    public static final int FAILED_MESSAGE = 26;
    /** Message type for sending insults */
    public static final int SEND_INSULT = 27;

    /**
     * Creates a connect message for a new client.
     *
     * @param username The username of the connecting client
     * @return A new ConnectMessage instance
     */
    public static Message createConnectMessage(String username) {
        return new ConnectMessage(username);
    }

    /**
     * Creates a response message for a connection attempt.
     *
     * @param success Whether the connection was successful
     * @param message The response message or error description
     * @return A new ConnectResponse instance
     */
    public static Message createConnectResponse(boolean success, String message) {
        return new ConnectResponse(success, message);
    }

    /**
     * Creates a disconnect message for a client leaving the chat.
     *
     * @param username The username of the disconnecting client
     * @return A new DisconnectMessage instance
     */
    public static Message createDisconnectMessage(String username) {
        return new DisconnectMessage(username);
    }

    /**
     * Creates a message to query the list of connected users.
     *
     * @param username The username of the client making the query
     * @return A new QueryConnectedUsers instance
     */
    public static Message createQueryConnectedUsers(String username) {
        return new QueryConnectedUsers(username);
    }

    /**
     * Creates a response message containing the list of connected users.
     *
     * @param usernames Array of usernames of connected clients
     * @return A new QueryUserResponse instance
     */
    public static Message createQueryUserResponse(String[] usernames) {
        return new QueryUserResponse(usernames);
    }

    /**
     * Creates a broadcast message to be sent to all connected clients.
     *
     * @param sender The username of the message sender
     * @param content The content of the broadcast message
     * @return A new BroadcastMessage instance
     */
    public static Message createBroadcastMessage(String sender, String content) {
        return new BroadcastMessage(sender, content);
    }

    /**
     * Creates a direct message to be sent to a specific client.
     *
     * @param sender The username of the message sender
     * @param recipient The username of the message recipient
     * @param content The content of the direct message
     * @return A new DirectMessage instance
     */
    public static Message createDirectMessage(String sender, String recipient, String content) {
        return new DirectMessage(sender, recipient, content);
    }

    /**
     * Creates a failed message to indicate an error condition.
     *
     * @param errorMessage Description of the error that occurred
     * @return A new FailedMessage instance
     */
    public static Message createFailedMessage(String errorMessage) {
        return new FailedMessage(errorMessage);
    }

    /**
     * Creates a message for sending an insult to a specific user.
     *
     * @param sender The username of the client sending the insult
     * @param recipient The username of the client to receive the insult
     * @return A new SendInsultMessage instance
     */
    public static Message createSendInsultMessage(String sender, String recipient) {
        return new SendInsultMessage(sender, recipient);
    }
} 