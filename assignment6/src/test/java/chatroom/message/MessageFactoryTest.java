package chatroom.message;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MessageFactoryTest {
    @Test
    void testCreateConnectMessage() {
        Message message = MessageFactory.createConnectMessage("testUser");
        assertEquals(MessageFactory.CONNECT_MESSAGE, message.getMessageIdentifier());
    }

    @Test
    void testCreateConnectResponse() {
        Message message = MessageFactory.createConnectResponse(true, "Connected successfully");
        assertEquals(MessageFactory.CONNECT_RESPONSE, message.getMessageIdentifier());
    }

    @Test
    void testCreateDisconnectMessage() {
        Message message = MessageFactory.createDisconnectMessage("testUser");
        assertEquals(MessageFactory.DISCONNECT_MESSAGE, message.getMessageIdentifier());
    }

    @Test
    void testCreateQueryConnectedUsers() {
        Message message = MessageFactory.createQueryConnectedUsers("testUser");
        assertEquals(MessageFactory.QUERY_CONNECTED_USERS, message.getMessageIdentifier());
    }

    @Test
    void testCreateQueryUserResponse() {
        String[] users = {"user1", "user2"};
        Message message = MessageFactory.createQueryUserResponse(users);
        assertEquals(MessageFactory.QUERY_USER_RESPONSE, message.getMessageIdentifier());
    }

    @Test
    void testCreateBroadcastMessage() {
        Message message = MessageFactory.createBroadcastMessage("testUser", "Hello everyone!");
        assertEquals(MessageFactory.BROADCAST_MESSAGE, message.getMessageIdentifier());
    }

    @Test
    void testCreateDirectMessage() {
        Message message = MessageFactory.createDirectMessage("sender", "recipient", "Hello!");
        assertEquals(MessageFactory.DIRECT_MESSAGE, message.getMessageIdentifier());
    }

    @Test
    void testCreateFailedMessage() {
        Message message = MessageFactory.createFailedMessage("Error occurred");
        assertEquals(MessageFactory.FAILED_MESSAGE, message.getMessageIdentifier());
    }

    @Test
    void testCreateSendInsultMessage() {
        Message message = MessageFactory.createSendInsultMessage("sender", "recipient");
        assertEquals(MessageFactory.SEND_INSULT, message.getMessageIdentifier());
    }
} 