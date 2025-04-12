package chatroom.client;

import chatroom.message.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.anyString;

import java.io.*;
import java.net.Socket;

public class ChatClientTest {
    private ChatClient client;
    private ConsoleUI mockUI;
    private Socket mockSocket;
    private ByteArrayOutputStream outputStream;
    private DataOutputStream dataOut;
    private String username = "testUser";

    @BeforeEach
    public void setUp() throws IOException {
        mockUI = mock(ConsoleUI.class);
        mockSocket = mock(Socket.class);
        outputStream = new ByteArrayOutputStream();
        dataOut = new DataOutputStream(outputStream);
        
        // Set up mock socket with DataOutputStream
        when(mockSocket.getOutputStream()).thenReturn(dataOut);
        when(mockSocket.getInputStream()).thenReturn(new ByteArrayInputStream("".getBytes()));
        
        client = new ChatClient(username);
        client.setUI(mockUI);
        client.setSocket(mockSocket);
    }

//    @Test
//    public void testConnect() throws IOException {
//        // Create a connect message directly to compare
//        ByteArrayOutputStream expectedOutput = new ByteArrayOutputStream();
//        DataOutputStream expectedDataOut = new DataOutputStream(expectedOutput);
//        expectedDataOut.writeInt(MessageFactory.CONNECT_MESSAGE);
//        expectedDataOut.writeInt(username.length());
//        expectedDataOut.writeBytes(username);
//        expectedDataOut.flush();
//
//        // Call connect on the client
//        client.connect("localhost", 8080);
//
//        // Flush the actual output stream
//        dataOut.flush();
//
//        // Compare the actual output with expected
//        assertArrayEquals(expectedOutput.toByteArray(), outputStream.toByteArray());
//    }

    @Test
    public void testSendBroadcastMessage() throws IOException {
        String message = "Hello World";
        client.sendBroadcastMessage(message);
        
        // Verify broadcast message was sent
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
        assertEquals(MessageFactory.BROADCAST_MESSAGE, dis.readInt());
        
        // Read sender length and verify
        int senderLength = dis.readInt();
        byte[] senderBytes = new byte[senderLength];
        dis.readFully(senderBytes);
        assertEquals(username, new String(senderBytes));
        
        // Read message length and verify
        int messageLength = dis.readInt();
        byte[] messageBytes = new byte[messageLength];
        dis.readFully(messageBytes);
        assertEquals(message, new String(messageBytes));
    }

    @Test
    public void testSendDirectMessage() throws IOException {
        String recipient = "recipient";
        String message = "Hello";
        client.sendDirectMessage(recipient, message);
        
        // Verify direct message was sent
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
        assertEquals(MessageFactory.DIRECT_MESSAGE, dis.readInt());
        
        // Verify sender
        int senderLength = dis.readInt();
        byte[] senderBytes = new byte[senderLength];
        dis.readFully(senderBytes);
        assertEquals(username, new String(senderBytes));
        
        // Verify recipient
        int recipientLength = dis.readInt();
        byte[] recipientBytes = new byte[recipientLength];
        dis.readFully(recipientBytes);
        assertEquals(recipient, new String(recipientBytes));
        
        // Verify message
        int messageLength = dis.readInt();
        byte[] messageBytes = new byte[messageLength];
        dis.readFully(messageBytes);
        assertEquals(message, new String(messageBytes));
    }

    @Test
    public void testSendDirectMessageToSelf() {
        client.sendDirectMessage(username, "Hello");
        verify(mockUI).displayError("Cannot send message to yourself");
    }

    @Test
    public void testSendInsult() throws IOException {
        String recipient = "recipient";
        client.sendInsult(recipient);
        
        // Verify insult message was sent
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
        assertEquals(MessageFactory.SEND_INSULT, dis.readInt());
        
        // Verify sender
        int senderLength = dis.readInt();
        byte[] senderBytes = new byte[senderLength];
        dis.readFully(senderBytes);
        assertEquals(username, new String(senderBytes));
        
        // Verify recipient
        int recipientLength = dis.readInt();
        byte[] recipientBytes = new byte[recipientLength];
        dis.readFully(recipientBytes);
        assertEquals(recipient, new String(recipientBytes));
    }

    @Test
    public void testSendInsultToSelf() {
        client.sendInsult(username);
        verify(mockUI).displayError("Cannot insult yourself");
    }

    @Test
    public void testQueryConnectedUsers() throws IOException {
        client.queryConnectedUsers();
        
        // Verify query users message was sent
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
        assertEquals(MessageFactory.QUERY_CONNECTED_USERS, dis.readInt());
    }
//
//    @Test
//    public void testDisconnect() throws IOException {
//        // Create expected output for both connect and disconnect messages
//        ByteArrayOutputStream expectedOutput = new ByteArrayOutputStream();
//        DataOutputStream expectedDataOut = new DataOutputStream(expectedOutput);
//
//        // Write connect message
//        expectedDataOut.writeInt(MessageFactory.CONNECT_MESSAGE);
//        expectedDataOut.writeInt(username.length());
//        expectedDataOut.writeBytes(username);
//
//        // Write disconnect message
//        expectedDataOut.writeInt(MessageFactory.DISCONNECT_MESSAGE);
//        expectedDataOut.writeInt(username.length());
//        expectedDataOut.writeBytes(username);
//        expectedDataOut.flush();
//
//        // Perform the test actions
//        client.connect("localhost", 8080);
//        client.disconnect();
//
//        // Flush the actual output stream
//        dataOut.flush();
//
//        // Compare the actual output with expected
//        assertArrayEquals(expectedOutput.toByteArray(), outputStream.toByteArray());
//
//        // Verify socket was closed
//        verify(mockSocket).close();
//    }

    @Test
    public void testHandleConnectionFailure() {
        when(mockSocket.isClosed()).thenReturn(true);
        client.sendBroadcastMessage("Hello");
        verify(mockUI).displayError(contains("Error sending message"));
    }
} 