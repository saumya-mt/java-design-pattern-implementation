package chatroom.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Message sent by a client to request sending an insult to another client.
 * Contains the sender's username and the recipient's username.
 */
public class SendInsultMessage implements Message {
    private final String sender;
    private final String recipient;

    /**
     * Constructs a new send insult message.
     *
     * @param sender The username of the client sending the insult
     * @param recipient The username of the client to receive the insult
     */
    public SendInsultMessage(String sender, String recipient) {
        this.sender = sender;
        this.recipient = recipient;
    }

    /**
     * {@inheritDoc}
     * Returns the message identifier for send insult messages.
     */
    @Override
    public int getMessageIdentifier() {
        return MessageFactory.SEND_INSULT;
    }

    /**
     * {@inheritDoc}
     * Serializes the send insult message into a byte array.
     * Format: [message_id][sender_length][sender_bytes][recipient_length][recipient_bytes]
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

        // Flush and get bytes
        dos.flush();
        byte[] result = baos.toByteArray();
        
        // Debug output
        System.out.println("DEBUG: SendInsultMessage serialized:");
        System.out.println("  Message type: " + getMessageIdentifier());
        System.out.println("  Sender length: " + senderBytes.length);
        System.out.println("  Recipient length: " + recipientBytes.length);
        System.out.println("  Total bytes: " + result.length);
        
        return result;
    }

    /**
     * Gets the username of the client who sent the insult request.
     *
     * @return The sender's username
     */
    public String getSender() {
        return sender;
    }

    /**
     * Gets the username of the client who should receive the insult.
     *
     * @return The recipient's username
     */
    public String getRecipient() {
        return recipient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SendInsultMessage that = (SendInsultMessage) o;
        return (sender == null ? that.sender == null : sender.equals(that.sender)) &&
               (recipient == null ? that.recipient == null : recipient.equals(that.recipient));
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(sender, recipient);
    }

    @Override
    public String toString() {
        return "SendInsultMessage{sender='" + sender + "', recipient='" + recipient + "'}";
    }
} 