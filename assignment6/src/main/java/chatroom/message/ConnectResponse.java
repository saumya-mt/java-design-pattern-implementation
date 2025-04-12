package chatroom.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Message sent by the server in response to a connection request.
 * Contains information about whether the connection was successful
 * and a message explaining the result.
 */
public class ConnectResponse implements Message {
    private final boolean success;
    private final String message;

    /**
     * Constructs a new connect response message.
     *
     * @param success Whether the connection request was successful
     * @param message A message explaining the result of the connection attempt
     */
    public ConnectResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * {@inheritDoc}
     * Returns the message identifier for connect response messages.
     */
    @Override
    public int getMessageIdentifier() {
        return MessageFactory.CONNECT_RESPONSE;
    }

    /**
     * {@inheritDoc}
     * Serializes the connect response message into a byte array.
     * Format: [message_id][success_flag][message_length][message_bytes]
     */
    @Override
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Write message identifier
        dos.writeInt(getMessageIdentifier());
        
        // Write success flag
        dos.writeBoolean(success);

        // Write response message
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(messageBytes.length);
        dos.write(messageBytes);

        return baos.toByteArray();
    }

    /**
     * Gets whether the connection request was successful.
     *
     * @return true if the connection was successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Gets the message explaining the result of the connection attempt.
     *
     * @return The response message
     */
    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectResponse that = (ConnectResponse) o;
        return success == that.success && message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(success, message);
    }

    @Override
    public String toString() {
        return "ConnectResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
} 