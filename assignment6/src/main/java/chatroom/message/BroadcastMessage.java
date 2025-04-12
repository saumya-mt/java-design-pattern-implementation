package chatroom.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Message sent by a client to broadcast a message to all connected clients.
 * Contains the sender's username and the message content.
 */
public class BroadcastMessage implements Message {
    private final String sender;
    private final String content;

    /**
     * Constructs a new broadcast message.
     *
     * @param sender The username of the client sending the message
     * @param content The content of the message to broadcast
     */
    public BroadcastMessage(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    /**
     * {@inheritDoc}
     * Returns the message identifier for broadcast messages.
     */
    @Override
    public int getMessageIdentifier() {
        return MessageFactory.BROADCAST_MESSAGE;
    }

    /**
     * {@inheritDoc}
     * Serializes the broadcast message into a byte array.
     * Format: [message_id][sender_length][sender_bytes][content_length][content_bytes]
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

        // Write message content
        byte[] messageBytes = content.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(messageBytes.length);
        dos.write(messageBytes);

        // Flush and get bytes
        dos.flush();
        byte[] result = baos.toByteArray();
        
        // Debug output
        System.out.println("DEBUG: BroadcastMessage serialized:");
        System.out.println("  Message type: " + getMessageIdentifier());
        System.out.println("  Sender length: " + senderBytes.length);
        System.out.println("  Message length: " + messageBytes.length);
        System.out.println("  Total bytes: " + result.length);
        
        return result;
    }

    /**
     * Gets the username of the client who sent the broadcast message.
     *
     * @return The sender's username
     */
    public String getSender() {
        return sender;
    }

    /**
     * Gets the content of the broadcast message.
     *
     * @return The message content
     */
    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "BroadcastMessage{" +
                "sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BroadcastMessage that = (BroadcastMessage) o;
        return sender.equals(that.sender) &&
                content.equals(that.content);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(sender, content);
    }
} 