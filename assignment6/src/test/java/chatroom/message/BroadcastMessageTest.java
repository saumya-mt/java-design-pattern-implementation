package chatroom.message;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BroadcastMessageTest {
    @Test
    void testGetSender() {
        BroadcastMessage message = new BroadcastMessage("testUser", "Hello World");
        assertEquals("testUser", message.getSender());
    }

    @Test
    void testGetContent() {
        BroadcastMessage message = new BroadcastMessage("testUser", "Hello World");
        assertEquals("Hello World", message.getContent());
    }

    @Test
    void testGetMessageIdentifier() {
        BroadcastMessage message = new BroadcastMessage("testUser", "Hello World");
        assertEquals(MessageFactory.BROADCAST_MESSAGE, message.getMessageIdentifier());
    }

    @Test
    void testEquals() {
        BroadcastMessage message1 = new BroadcastMessage("testUser", "Hello World");
        BroadcastMessage message2 = new BroadcastMessage("testUser", "Hello World");
        BroadcastMessage differentSender = new BroadcastMessage("otherUser", "Hello World");
        BroadcastMessage differentContent = new BroadcastMessage("testUser", "Different content");

        // Test equality with same values
        assertEquals(message1, message2);
        assertEquals(message2, message1);

        // Test inequality with different values
        assertNotEquals(message1, differentSender);
        assertNotEquals(message1, differentContent);

        // Test equality with itself
        assertEquals(message1, message1);

        // Test inequality with null
        assertNotEquals(message1, null);

        // Test inequality with different type
        assertNotEquals(message1, "not a BroadcastMessage");
    }

    @Test
    void testHashCode() {
        BroadcastMessage message1 = new BroadcastMessage("testUser", "Hello World");
        BroadcastMessage message2 = new BroadcastMessage("testUser", "Hello World");
        BroadcastMessage differentMessage = new BroadcastMessage("otherUser", "Different content");

        // Equal objects should have equal hash codes
        assertEquals(message1.hashCode(), message2.hashCode());

        // Different objects should (usually) have different hash codes
        assertNotEquals(message1.hashCode(), differentMessage.hashCode());
    }

    @Test
    void testToString() {
        BroadcastMessage message = new BroadcastMessage("testUser", "Hello World");
        String expected = "BroadcastMessage{sender='testUser', content='Hello World'}";
        assertEquals(expected, message.toString());
    }
} 