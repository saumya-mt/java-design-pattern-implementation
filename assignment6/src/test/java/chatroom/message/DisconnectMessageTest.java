package chatroom.message;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import chatroom.message.DisconnectMessage;
import chatroom.message.Message;
import chatroom.message.MessageFactory;

public class DisconnectMessageTest {
    @Test
    public void testGetUsername() {
        DisconnectMessage message = new DisconnectMessage("testUser");
        assertEquals("testUser", message.getUsername());
    }

    @Test
    public void testGetMessageIdentifier() {
        DisconnectMessage message = new DisconnectMessage("testUser");
        assertEquals(MessageFactory.DISCONNECT_MESSAGE, message.getMessageIdentifier());
    }

    @Test
    void testEquals() {
        DisconnectMessage message1 = new DisconnectMessage("testUser");
        DisconnectMessage message2 = new DisconnectMessage("testUser");
        DisconnectMessage differentUser = new DisconnectMessage("otherUser");

        // Test equality with same values
        assertEquals(message1, message2);
        assertEquals(message2, message1);

        // Test inequality with different values
        assertNotEquals(message1, differentUser);

        // Test equality with itself
        assertEquals(message1, message1);

        // Test inequality with null
        assertNotEquals(message1, null);

        // Test inequality with different type
        assertNotEquals(message1, "not a DisconnectMessage");
    }

    @Test
    void testHashCode() {
        DisconnectMessage message1 = new DisconnectMessage("testUser");
        DisconnectMessage message2 = new DisconnectMessage("testUser");
        DisconnectMessage differentMessage = new DisconnectMessage("otherUser");

        // Equal objects should have equal hash codes
        assertEquals(message1.hashCode(), message2.hashCode());

        // Different objects should (usually) have different hash codes
        assertNotEquals(message1.hashCode(), differentMessage.hashCode());
    }

    @Test
    void testToString() {
        DisconnectMessage message = new DisconnectMessage("testUser");
        String expected = "DisconnectMessage{username='testUser'}";
        assertEquals(expected, message.toString());
    }
} 