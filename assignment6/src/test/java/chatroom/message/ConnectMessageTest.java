package chatroom.message;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ConnectMessageTest {
    @Test
    public void testGetUsername() {
        ConnectMessage message = new ConnectMessage("testUser");
        assertEquals("testUser", message.getUsername());
    }

    @Test
    public void testGetMessageIdentifier() {
        ConnectMessage message = new ConnectMessage("testUser");
        assertEquals(MessageFactory.CONNECT_MESSAGE, message.getMessageIdentifier());
    }

    @Test
    void testSerialize() throws IOException {
        ConnectMessage message = new ConnectMessage("testUser");
        byte[] serialized = message.serialize();
        
        // Verify message identifier
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(serialized));
        assertEquals(MessageFactory.CONNECT_MESSAGE, dis.readInt());
        
        // Verify username
        int usernameLength = dis.readInt();
        byte[] usernameBytes = new byte[usernameLength];
        dis.readFully(usernameBytes);
        assertEquals("testUser", new String(usernameBytes, StandardCharsets.UTF_8));
    }

    @Test
    void testSerializeWithEmptyUsername() throws IOException {
        ConnectMessage message = new ConnectMessage("");
        byte[] serialized = message.serialize();
        
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(serialized));
        assertEquals(MessageFactory.CONNECT_MESSAGE, dis.readInt());
        
        int usernameLength = dis.readInt();
        assertEquals(0, usernameLength);
    }

    @Test
    void testSerializeWithSpecialCharacters() throws IOException {
        String username = "téstÜsér";
        ConnectMessage message = new ConnectMessage(username);
        byte[] serialized = message.serialize();
        
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(serialized));
        assertEquals(MessageFactory.CONNECT_MESSAGE, dis.readInt());
        
        int usernameLength = dis.readInt();
        byte[] usernameBytes = new byte[usernameLength];
        dis.readFully(usernameBytes);
        assertEquals(username, new String(usernameBytes, StandardCharsets.UTF_8));
    }

    @Test
    void testEquals() {
        ConnectMessage message1 = new ConnectMessage("testUser");
        ConnectMessage message2 = new ConnectMessage("testUser");
        ConnectMessage differentUser = new ConnectMessage("otherUser");

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
        assertNotEquals(message1, "not a ConnectMessage");
    }

    @Test
    void testHashCode() {
        ConnectMessage message1 = new ConnectMessage("testUser");
        ConnectMessage message2 = new ConnectMessage("testUser");
        ConnectMessage differentMessage = new ConnectMessage("otherUser");

        // Equal objects should have equal hash codes
        assertEquals(message1.hashCode(), message2.hashCode());

        // Different objects should (usually) have different hash codes
        assertNotEquals(message1.hashCode(), differentMessage.hashCode());
    }

    @Test
    void testToString() {
        ConnectMessage message = new ConnectMessage("testUser");
        String expected = "ConnectMessage{username='testUser'}";
        assertEquals(expected, message.toString());
    }
} 