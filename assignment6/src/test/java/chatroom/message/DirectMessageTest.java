package chatroom.message;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DirectMessageTest {
    @Test
    public void testGetSender() {
        DirectMessage message = new DirectMessage("sender", "recipient", "Hello");
        assertEquals("sender", message.getSender());
    }

    @Test
    public void testGetRecipient() {
        DirectMessage message = new DirectMessage("sender", "recipient", "Hello");
        assertEquals("recipient", message.getRecipient());
    }

    @Test
    public void testGetContent() {
        DirectMessage message = new DirectMessage("sender", "recipient", "Hello");
        assertEquals("Hello", message.getContent());
    }

    @Test
    public void testGetMessageIdentifier() {
        DirectMessage message = new DirectMessage("sender", "recipient", "Hello");
        assertEquals(MessageFactory.DIRECT_MESSAGE, message.getMessageIdentifier());
    }

    @Test
    void testEquals() {
        DirectMessage message1 = new DirectMessage("sender", "recipient", "Hello");
        DirectMessage message2 = new DirectMessage("sender", "recipient", "Hello");
        DirectMessage differentSender = new DirectMessage("other", "recipient", "Hello");
        DirectMessage differentRecipient = new DirectMessage("sender", "other", "Hello");
        DirectMessage differentContent = new DirectMessage("sender", "recipient", "Different");

        // Test equality with same values
        assertEquals(message1, message2);
        assertEquals(message2, message1);

        // Test inequality with different values
        assertNotEquals(message1, differentSender);
        assertNotEquals(message1, differentRecipient);
        assertNotEquals(message1, differentContent);

        // Test equality with itself
        assertEquals(message1, message1);

        // Test inequality with null
        assertNotEquals(message1, null);

        // Test inequality with different type
        assertNotEquals(message1, "not a DirectMessage");
    }

    @Test
    void testHashCode() {
        DirectMessage message1 = new DirectMessage("sender", "recipient", "Hello");
        DirectMessage message2 = new DirectMessage("sender", "recipient", "Hello");
        DirectMessage differentMessage = new DirectMessage("other", "other", "Different");

        // Equal objects should have equal hash codes
        assertEquals(message1.hashCode(), message2.hashCode());

        // Different objects should (usually) have different hash codes
        assertNotEquals(message1.hashCode(), differentMessage.hashCode());
    }

    @Test
    void testToString() {
        DirectMessage message = new DirectMessage("sender", "recipient", "Hello");
        String expected = "DirectMessage{sender='sender', recipient='recipient', content='Hello'}";
        assertEquals(expected, message.toString());
    }
} 