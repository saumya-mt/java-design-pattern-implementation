package chatroom.message;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SendInsultMessageTest {
    @Test
    public void testGetSender() {
        SendInsultMessage message = new SendInsultMessage("sender", "recipient");
        assertEquals("sender", message.getSender());
    }

    @Test
    public void testGetRecipient() {
        SendInsultMessage message = new SendInsultMessage("sender", "recipient");
        assertEquals("recipient", message.getRecipient());
    }

    @Test
    public void testGetMessageIdentifier() {
        SendInsultMessage message = new SendInsultMessage("sender", "recipient");
        assertEquals(MessageFactory.SEND_INSULT, message.getMessageIdentifier());
    }

    @Test
    void testEquals() {
        SendInsultMessage message1 = new SendInsultMessage("sender", "recipient");
        SendInsultMessage message2 = new SendInsultMessage("sender", "recipient");
        SendInsultMessage differentSender = new SendInsultMessage("other", "recipient");
        SendInsultMessage differentRecipient = new SendInsultMessage("sender", "other");

        // Test equality with same values
        assertEquals(message1, message2);
        assertEquals(message2, message1);

        // Test inequality with different values
        assertNotEquals(message1, differentSender);
        assertNotEquals(message1, differentRecipient);

        // Test equality with itself
        assertEquals(message1, message1);

        // Test inequality with null
        assertNotEquals(message1, null);

        // Test inequality with different type
        assertNotEquals(message1, "not a SendInsultMessage");
    }

    @Test
    void testHashCode() {
        SendInsultMessage message1 = new SendInsultMessage("sender", "recipient");
        SendInsultMessage message2 = new SendInsultMessage("sender", "recipient");
        SendInsultMessage differentMessage = new SendInsultMessage("other", "other");

        // Equal objects should have equal hash codes
        assertEquals(message1.hashCode(), message2.hashCode());

        // Different objects should (usually) have different hash codes
        assertNotEquals(message1.hashCode(), differentMessage.hashCode());
    }

    @Test
    void testToString() {
        SendInsultMessage message = new SendInsultMessage("sender", "recipient");
        String expected = "SendInsultMessage{sender='sender', recipient='recipient'}";
        assertEquals(expected, message.toString());
    }

    @Test
    void testSerialize() throws IOException {
        SendInsultMessage message = new SendInsultMessage("sender", "recipient");
        byte[] serialized = message.serialize();
        
        // Verify message identifier
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(serialized));
        assertEquals(MessageFactory.SEND_INSULT, dis.readInt());
        
        // Verify sender
        int senderLength = dis.readInt();
        byte[] senderBytes = new byte[senderLength];
        dis.readFully(senderBytes);
        assertEquals("sender", new String(senderBytes, StandardCharsets.UTF_8));
        
        // Verify recipient
        int recipientLength = dis.readInt();
        byte[] recipientBytes = new byte[recipientLength];
        dis.readFully(recipientBytes);
        assertEquals("recipient", new String(recipientBytes, StandardCharsets.UTF_8));
    }

    @Test
    void testSerializeWithEmptyStrings() throws IOException {
        SendInsultMessage message = new SendInsultMessage("", "");
        byte[] serialized = message.serialize();
        
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(serialized));
        assertEquals(MessageFactory.SEND_INSULT, dis.readInt());
        
        // Verify empty sender
        int senderLength = dis.readInt();
        assertEquals(0, senderLength);
        
        // Verify empty recipient
        int recipientLength = dis.readInt();
        assertEquals(0, recipientLength);
    }

    @Test
    void testSerializeWithSpecialCharacters() throws IOException {
        String sender = "sénđér";
        String recipient = "récípïént";
        SendInsultMessage message = new SendInsultMessage(sender, recipient);
        byte[] serialized = message.serialize();
        
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(serialized));
        assertEquals(MessageFactory.SEND_INSULT, dis.readInt());
        
        // Verify sender with special characters
        int senderLength = dis.readInt();
        byte[] senderBytes = new byte[senderLength];
        dis.readFully(senderBytes);
        assertEquals(sender, new String(senderBytes, StandardCharsets.UTF_8));
        
        // Verify recipient with special characters
        int recipientLength = dis.readInt();
        byte[] recipientBytes = new byte[recipientLength];
        dis.readFully(recipientBytes);
        assertEquals(recipient, new String(recipientBytes, StandardCharsets.UTF_8));
    }

    @Test
    void testEqualsWithNullFields() {
        SendInsultMessage message1 = new SendInsultMessage(null, null);
        SendInsultMessage message2 = new SendInsultMessage(null, null);
        SendInsultMessage message3 = new SendInsultMessage("sender", null);
        SendInsultMessage message4 = new SendInsultMessage(null, "recipient");
        
        assertEquals(message1, message2);
        assertNotEquals(message1, message3);
        assertNotEquals(message1, message4);
        assertNotEquals(message3, message4);
    }

    @Test
    void testHashCodeWithNullFields() {
        SendInsultMessage message1 = new SendInsultMessage(null, null);
        SendInsultMessage message2 = new SendInsultMessage(null, null);
        SendInsultMessage message3 = new SendInsultMessage("sender", null);
        
        assertEquals(message1.hashCode(), message2.hashCode());
        assertNotEquals(message1.hashCode(), message3.hashCode());
    }

    @Test
    void testToStringWithNullFields() {
        SendInsultMessage message = new SendInsultMessage(null, null);
        String expected = "SendInsultMessage{sender='null', recipient='null'}";
        assertEquals(expected, message.toString());
    }

    @Test
    void testEqualsWithDifferentTypes() {
        SendInsultMessage message = new SendInsultMessage("sender", "recipient");
        assertNotEquals(message, "not a SendInsultMessage");
        assertNotEquals(message, null);
        assertNotEquals(message, new Object());
    }

    @Test
    void testHashCodeConsistency() {
        SendInsultMessage message = new SendInsultMessage("sender", "recipient");
        int hashCode1 = message.hashCode();
        int hashCode2 = message.hashCode();
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testEqualsReflexivity() {
        SendInsultMessage message = new SendInsultMessage("sender", "recipient");
        assertEquals(message, message);
    }

    @Test
    void testEqualsSymmetry() {
        SendInsultMessage message1 = new SendInsultMessage("sender", "recipient");
        SendInsultMessage message2 = new SendInsultMessage("sender", "recipient");
        assertEquals(message1, message2);
        assertEquals(message2, message1);
    }

    @Test
    void testEqualsTransitivity() {
        SendInsultMessage message1 = new SendInsultMessage("sender", "recipient");
        SendInsultMessage message2 = new SendInsultMessage("sender", "recipient");
        SendInsultMessage message3 = new SendInsultMessage("sender", "recipient");
        assertEquals(message1, message2);
        assertEquals(message2, message3);
        assertEquals(message1, message3);
    }
} 