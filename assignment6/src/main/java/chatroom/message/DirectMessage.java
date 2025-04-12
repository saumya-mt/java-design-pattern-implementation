package chatroom.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Message sent by a client to send a private message to another specific client.
 * Contains the sender's username, recipient's username, and the message content.
 */
public class DirectMessage implements Message {
    private final String sender;
    private final String recipient;
    private final String content;

    /**
     * Constructs a new direct message.
     *
     * @param sender The username of the client sending the message
     * @param recipient The username of the client to receive the message
     * @param content The content of the private message
     */
    public DirectMessage(String sender, String recipient, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
    }

    /**
     * {@inheritDoc}
     * Returns the message identifier for direct messages.
     */
    @Override
    public int getMessageIdentifier() {
        return MessageFactory.DIRECT_MESSAGE;
    }

    /**
     * {@inheritDoc}
     * Serializes the direct message into a byte array.
     * Format: [message_id][sender_length][sender_bytes][recipient_length][recipient_bytes][content_length][content_bytes]
     */
    @Override
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Write message identifier
        dos.writeInt(getMessageIdentifier());
        
        // Write sender username
        byte[] senderBytes = sender.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(senderBytes.length);
        dos.write(senderBytes);

        // Write recipient username
        byte[] recipientBytes = recipient.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(recipientBytes.length);
        dos.write(recipientBytes);

        // Write message content
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(contentBytes.length);
        dos.write(contentBytes);

        return baos.toByteArray();
    }

    /**
     * Gets the username of the client who sent the direct message.
     *
     * @return The sender's username
     */
    public String getSender() {
        return sender;
    }

    /**
     * Gets the username of the client who should receive the direct message.
     *
     * @return The recipient's username
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Gets the content of the direct message.
     *
     * @return The message content
     */
    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "DirectMessage{" +
                "sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DirectMessage that = (DirectMessage) o;
        return sender.equals(that.sender) &&
                recipient.equals(that.recipient) &&
                content.equals(that.content);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(sender, recipient, content);
    }
} 