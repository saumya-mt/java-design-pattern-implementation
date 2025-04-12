package chatroom.message;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QueryUserResponseTest {
    @Test
    void testGetUsernames() {
        String[] users = {"user1", "user2"};
        QueryUserResponse message = new QueryUserResponse(users);
        assertArrayEquals(users, message.getUsernames());
    }

    @Test
    void testGetMessageIdentifier() {
        String[] users = {"user1", "user2"};
        QueryUserResponse message = new QueryUserResponse(users);
        assertEquals(MessageFactory.QUERY_USER_RESPONSE, message.getMessageIdentifier());
    }
} 