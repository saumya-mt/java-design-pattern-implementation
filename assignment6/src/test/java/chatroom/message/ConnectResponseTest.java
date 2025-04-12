package chatroom.message;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ConnectResponseTest {
    @Test
    public void testIsSuccess() {
        ConnectResponse message = new ConnectResponse(true, "Success");
        assertTrue(message.isSuccess());
    }

    @Test
    public void testGetMessage() {
        ConnectResponse message = new ConnectResponse(true, "Success");
        assertEquals("Success", message.getMessage());
    }

    @Test
    public void testGetMessageIdentifier() {
        ConnectResponse message = new ConnectResponse(true, "Success");
        assertEquals(MessageFactory.CONNECT_RESPONSE, message.getMessageIdentifier());
    }

    @Test
    void testSerialize() throws IOException {
        ConnectResponse message = new ConnectResponse(true, "Success");
        byte[] serialized = message.serialize();
        
        // Verify message identifier
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(serialized));
        assertEquals(MessageFactory.CONNECT_RESPONSE, dis.readInt());
        
        // Verify success flag
        assertTrue(dis.readBoolean());
        
        // Verify message
        int messageLength = dis.readInt();
        byte[] messageBytes = new byte[messageLength];
        dis.readFully(messageBytes);
        assertEquals("Success", new String(messageBytes, StandardCharsets.UTF_8));
    }

    @Test
    void testSerializeWithEmptyMessage() throws IOException {
        ConnectResponse message = new ConnectResponse(false, "");
        byte[] serialized = message.serialize();
        
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(serialized));
        assertEquals(MessageFactory.CONNECT_RESPONSE, dis.readInt());
        
        assertFalse(dis.readBoolean());
        int messageLength = dis.readInt();
        assertEquals(0, messageLength);
    }

    @Test
    void testSerializeWithSpecialCharacters() throws IOException {
        String responseMessage = "Cönnection fäiled";
        ConnectResponse message = new ConnectResponse(false, responseMessage);
        byte[] serialized = message.serialize();
        
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(serialized));
        assertEquals(MessageFactory.CONNECT_RESPONSE, dis.readInt());
        
        assertFalse(dis.readBoolean());
        int messageLength = dis.readInt();
        byte[] messageBytes = new byte[messageLength];
        dis.readFully(messageBytes);
        assertEquals(responseMessage, new String(messageBytes, StandardCharsets.UTF_8));
    }

    @Test
    void testEquals() {
        ConnectResponse message1 = new ConnectResponse(true, "Success");
        ConnectResponse message2 = new ConnectResponse(true, "Success");
        ConnectResponse differentSuccess = new ConnectResponse(false, "Success");
        ConnectResponse differentMessage = new ConnectResponse(true, "Different");

        // Test equality with same values
        assertEquals(message1, message2);
        assertEquals(message2, message1);

        // Test inequality with different values
        assertNotEquals(message1, differentSuccess);
        assertNotEquals(message1, differentMessage);

        // Test equality with itself
        assertEquals(message1, message1);

        // Test inequality with null
        assertNotEquals(message1, null);

        // Test inequality with different type
        assertNotEquals(message1, "not a ConnectResponse");
    }

    @Test
    void testHashCode() {
        ConnectResponse message1 = new ConnectResponse(true, "Success");
        ConnectResponse message2 = new ConnectResponse(true, "Success");
        ConnectResponse differentMessage = new ConnectResponse(false, "Failed");

        // Equal objects should have equal hash codes
        assertEquals(message1.hashCode(), message2.hashCode());

        // Different objects should (usually) have different hash codes
        assertNotEquals(message1.hashCode(), differentMessage.hashCode());
    }

    @Test
    void testToString() {
        ConnectResponse message = new ConnectResponse(true, "Success");
        String expected = "ConnectResponse{success=true, message='Success'}";
        assertEquals(expected, message.toString());
    }
} 