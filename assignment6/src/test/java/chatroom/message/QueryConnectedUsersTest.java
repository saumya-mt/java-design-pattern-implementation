package chatroom.message;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QueryConnectedUsersTest {
    @Test
    void testGetUsername() {
        QueryConnectedUsers message = new QueryConnectedUsers("testUser");
        assertEquals("testUser", message.getUsername());
    }

    @Test
    void testGetType() {
        QueryConnectedUsers message = new QueryConnectedUsers("testUser");
        assertEquals(MessageFactory.QUERY_CONNECTED_USERS, message.getMessageIdentifier());
    }

    @Test
    void testEquals() {
        QueryConnectedUsers message1 = new QueryConnectedUsers("testUser");
        QueryConnectedUsers message2 = new QueryConnectedUsers("testUser");
        QueryConnectedUsers differentUser = new QueryConnectedUsers("otherUser");

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
        assertNotEquals(message1, "not a QueryConnectedUsers");
    }

    @Test
    void testHashCode() {
        QueryConnectedUsers message1 = new QueryConnectedUsers("testUser");
        QueryConnectedUsers message2 = new QueryConnectedUsers("testUser");
        QueryConnectedUsers differentMessage = new QueryConnectedUsers("otherUser");

        // Equal objects should have equal hash codes
        assertEquals(message1.hashCode(), message2.hashCode());

        // Different objects should (usually) have different hash codes
        assertNotEquals(message1.hashCode(), differentMessage.hashCode());
    }

    @Test
    void testToString() {
        QueryConnectedUsers message = new QueryConnectedUsers("testUser");
        String expected = "QueryConnectedUsers{username='testUser'}";
        assertEquals(expected, message.toString());
    }
} 