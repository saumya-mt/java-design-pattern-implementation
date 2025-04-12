package chatroom.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Message sent by a client to request a list of currently connected users.
 * Contains the username of the client making the request.
 */
public class QueryConnectedUsers implements Message {
    private final String username;

    /**
     * Constructs a new query connected users message.
     *
     * @param username The username of the client making the query
     */
    public QueryConnectedUsers(String username) {
        this.username = username;
    }

    /**
     * {@inheritDoc}
     * Returns the message identifier for query connected users messages.
     */
    @Override
    public int getMessageIdentifier() {
        return MessageFactory.QUERY_CONNECTED_USERS;
    }

    /**
     * {@inheritDoc}
     * Serializes the query message into a byte array.
     * Format: [message_id][username_length][username_bytes]
     */
    @Override
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Write message identifier
        dos.writeInt(getMessageIdentifier());
        
        // Write username
        byte[] usernameBytes = username.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(usernameBytes.length);
        dos.write(usernameBytes);

        return baos.toByteArray();
    }

    /**
     * Gets the username of the client who made the query.
     *
     * @return The username of the querying client
     */
    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "QueryConnectedUsers{" +
                "username='" + username + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryConnectedUsers that = (QueryConnectedUsers) o;
        return username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(username);
    }
} 