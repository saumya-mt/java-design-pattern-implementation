package chatroom.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Message sent by the server to indicate that an operation failed.
 * Contains an error message explaining the reason for the failure.
 */
public class FailedMessage implements Message {
    private final String errorMessage;

    /**
     * Constructs a new failed message.
     *
     * @param errorMessage A description of the error that occurred
     */
    public FailedMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * {@inheritDoc}
     * Returns the message identifier for failed messages.
     */
    @Override
    public int getMessageIdentifier() {
        return MessageFactory.FAILED_MESSAGE;
    }

    /**
     * {@inheritDoc}
     * Serializes the failed message into a byte array.
     * Format: [message_id][error_message_length][error_message_bytes]
     */
    @Override
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Write message identifier
        dos.writeInt(getMessageIdentifier());
        
        // Write error message
        byte[] messageBytes = errorMessage.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(messageBytes.length);
        dos.write(messageBytes);

        return baos.toByteArray();
    }

    /**
     * Gets the error message explaining why the operation failed.
     *
     * @return The error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "FailedMessage{" +
                "errorMessage='" + errorMessage + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FailedMessage that = (FailedMessage) o;
        return errorMessage.equals(that.errorMessage);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(errorMessage);
    }
} 