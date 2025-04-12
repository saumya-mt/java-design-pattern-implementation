package chatroom.message;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FailedMessageTest {
    @Test
    void testGetErrorMessage() {
        FailedMessage message = new FailedMessage("Test error");
        assertEquals("Test error", message.getErrorMessage());
    }

    @Test
    void testGetMessageIdentifier() {
        FailedMessage message = new FailedMessage("Test error");
        assertEquals(MessageFactory.FAILED_MESSAGE, message.getMessageIdentifier());
    }

    @Test
    void testEquals() {
        FailedMessage message1 = new FailedMessage("Test error");
        FailedMessage message2 = new FailedMessage("Test error");
        FailedMessage differentMessage = new FailedMessage("Different error");

        // Test equality with same values
        assertEquals(message1, message2);
        assertEquals(message2, message1);

        // Test inequality with different values
        assertNotEquals(message1, differentMessage);

        // Test equality with itself
        assertEquals(message1, message1);

        // Test inequality with null
        assertNotEquals(message1, null);

        // Test inequality with different type
        assertNotEquals(message1, "not a FailedMessage");
    }

    @Test
    void testHashCode() {
        FailedMessage message1 = new FailedMessage("Test error");
        FailedMessage message2 = new FailedMessage("Test error");
        FailedMessage differentMessage = new FailedMessage("Different error");

        // Equal objects should have equal hash codes
        assertEquals(message1.hashCode(), message2.hashCode());

        // Different objects should (usually) have different hash codes
        assertNotEquals(message1.hashCode(), differentMessage.hashCode());
    }

    @Test
    void testToString() {
        FailedMessage message = new FailedMessage("Test error");
        String expected = "FailedMessage{errorMessage='Test error'}";
        assertEquals(expected, message.toString());
    }
} 