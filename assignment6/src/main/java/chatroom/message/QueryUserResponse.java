package chatroom.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Message sent by the server in response to a query for connected users.
 * Contains an array of usernames of all currently connected clients.
 */
public class QueryUserResponse implements Message {
    private final String[] usernames;

    /**
     * Constructs a new query user response message.
     *
     * @param usernames Array of usernames of currently connected clients
     */
    public QueryUserResponse(String[] usernames) {
        this.usernames = usernames;
    }

    /**
     * {@inheritDoc}
     * Returns the message identifier for query user response messages.
     */
    @Override
    public int getMessageIdentifier() {
        return MessageFactory.QUERY_USER_RESPONSE;
    }

    /**
     * {@inheritDoc}
     * Serializes the response message into a byte array.
     * Format: [message_id][num_users][username1_length][username1_bytes]...[usernameN_length][usernameN_bytes]
     */
    @Override
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Write message identifier
        dos.writeInt(getMessageIdentifier());
        
        // Write number of users
        dos.writeInt(usernames.length);
        
        // Write each username
        for (String username : usernames) {
            byte[] usernameBytes = username.getBytes(StandardCharsets.UTF_8);
            dos.writeInt(usernameBytes.length);
            dos.write(usernameBytes);
        }

        return baos.toByteArray();
    }

    /**
     * Gets a copy of the array of usernames of connected clients.
     * Returns a copy to maintain encapsulation of the internal array.
     *
     * @return A copy of the array of usernames
     */
    public String[] getUsernames() {
        return usernames.clone(); // Return copy to maintain encapsulation
    }
} 