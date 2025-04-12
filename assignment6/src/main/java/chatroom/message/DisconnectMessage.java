package chatroom.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Message sent by a client to notify the server that they are disconnecting.
 * Contains the username of the disconnecting client.
 */
public class DisconnectMessage implements Message {
    private final String username;

    /**
     * Constructs a new disconnect message.
     *
     * @param username The username of the disconnecting client
     */
    public DisconnectMessage(String username) {
        this.username = username;
    }

    /**
     * {@inheritDoc}
     * Returns the message identifier for disconnect messages.
     */
    @Override
    public int getMessageIdentifier() {
        return MessageFactory.DISCONNECT_MESSAGE;
    }

    /**
     * {@inheritDoc}
     * Serializes the disconnect message into a byte array.
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
     * Gets the username of the disconnecting client.
     *
     * @return The username specified in this disconnect message
     */
    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "DisconnectMessage{" +
                "username='" + username + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DisconnectMessage that = (DisconnectMessage) o;
        return username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(username);
    }
} 