package chatroom.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Message sent by a client to request connection to the chat server.
 * Contains the username that the client wishes to use.
 */
public class ConnectMessage implements Message {
    private final String username;

    /**
     * Constructs a new connect message with the specified username.
     *
     * @param username The username of the connecting client
     */
    public ConnectMessage(String username) {
        this.username = username;
    }

    /**
     * {@inheritDoc}
     * Returns the message identifier for connect messages.
     */
    @Override
    public int getMessageIdentifier() {
        return MessageFactory.CONNECT_MESSAGE;
    }

    /**
     * {@inheritDoc}
     * Serializes the connect message into a byte array.
     * Format: [message_id][username_length][username_bytes]
     */
    @Override
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Write message identifier
        dos.writeInt(getMessageIdentifier());
        
        // Write username length and bytes
        byte[] usernameBytes = username.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(usernameBytes.length);
        dos.write(usernameBytes);

        return baos.toByteArray();
    }

    /**
     * Gets the username of the connecting client.
     *
     * @return The username specified in this connect message
     */
    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "ConnectMessage{" +
                "username='" + username + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectMessage that = (ConnectMessage) o;
        return username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(username);
    }
} 