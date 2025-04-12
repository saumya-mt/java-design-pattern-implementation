package chatroom.server;

import chatroom.message.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@ExtendWith(MockitoExtension.class)
public class ClientHandlerTest {

    private ClientHandler handler;
    private Socket mockSocket;
    private ChatServer mockServer;
    private ByteArrayOutputStream outputStream;
    private DataOutputStream dataOut;
    private String username = "testUser";

    @BeforeEach
    public void setUp() throws IOException {
        mockSocket = mock(Socket.class);
        mockServer = mock(ChatServer.class);
        outputStream = new ByteArrayOutputStream();
        dataOut = new DataOutputStream(outputStream);

        when(mockSocket.getOutputStream()).thenReturn(outputStream);
        handler = new ClientHandler(mockSocket, mockServer);

        // Connect the client first
        ByteArrayOutputStream connectBaos = new ByteArrayOutputStream();
        DataOutputStream connectDos = new DataOutputStream(connectBaos);
        connectDos.writeInt(MessageFactory.CONNECT_MESSAGE);
        byte[] usernameBytes = username.getBytes(StandardCharsets.UTF_8);
        connectDos.writeInt(usernameBytes.length);
        connectDos.write(usernameBytes);

        when(mockSocket.getInputStream()).thenReturn(
            new ByteArrayInputStream(connectBaos.toByteArray()));
        when(mockServer.isUsernameTaken(username)).thenReturn(false);

        // Run the handler to process connect message
        handler.run();

        // Verify connection was successful
        verify(mockServer).registerClient(eq(username), eq(handler));
    }

    @Test
    public void testHandleBroadcast() throws IOException {
        String message = "Hello World";

        // Create input stream with broadcast message
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(MessageFactory.BROADCAST_MESSAGE);

        // Write sender
        byte[] senderBytes = username.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(senderBytes.length);
        dos.write(senderBytes);

        // Write message
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(messageBytes.length);
        dos.write(messageBytes);

        // Set up input stream
        when(mockSocket.getInputStream()).thenReturn(new ByteArrayInputStream(baos.toByteArray()));

        // Run the handler
        handler.run();

        // Verify interactions
        verify(mockServer).broadcast(username, message);
    }

    @Test
    public void testHandleDirectMessage() throws IOException {
        String recipient = "recipient";
        String content = "Hello";

        // Create input stream with direct message
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(MessageFactory.DIRECT_MESSAGE);

        // Write sender
        byte[] senderBytes = username.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(senderBytes.length);
        dos.write(senderBytes);

        // Write recipient
        byte[] recipientBytes = recipient.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(recipientBytes.length);
        dos.write(recipientBytes);

        // Write content
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(contentBytes.length);
        dos.write(contentBytes);

        // Set up input stream
        when(mockSocket.getInputStream()).thenReturn(new ByteArrayInputStream(baos.toByteArray()));

        // Run the handler
        handler.run();

        // Verify interactions
        verify(mockServer).sendDirectMessage(username, recipient, content);
    }

    @Test
    public void testHandleDisconnect() throws IOException {
        // Create input stream with disconnect message
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(MessageFactory.DISCONNECT_MESSAGE);

        // Set up input stream
        when(mockSocket.getInputStream()).thenReturn(new ByteArrayInputStream(baos.toByteArray()));

        // Run the handler
        handler.run();

        // Verify interactions
        verify(mockServer).removeClient(username);
    }

    @Test
    public void testHandleInsult() throws IOException {
        String recipient = "recipient";

        // Create input stream with insult message
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(MessageFactory.SEND_INSULT);

        // Write sender
        byte[] senderBytes = username.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(senderBytes.length);
        dos.write(senderBytes);

        // Write recipient
        byte[] recipientBytes = recipient.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(recipientBytes.length);
        dos.write(recipientBytes);

        // Set up input stream
        when(mockSocket.getInputStream()).thenReturn(new ByteArrayInputStream(baos.toByteArray()));

        // Run the handler
        handler.run();

        // Verify interactions
        verify(mockServer).sendInsult(username, recipient);
    }

    @Test
    public void testHandleQueryUsers() throws IOException {
        String[] users = {"user1", "user2"};
        when(mockServer.getConnectedUsers()).thenReturn(users);

        // Create input stream with query users message
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(MessageFactory.QUERY_CONNECTED_USERS);

        // Set up input stream
        when(mockSocket.getInputStream()).thenReturn(new ByteArrayInputStream(baos.toByteArray()));

        // Run the handler
        handler.run();

        // Verify interactions
        verify(mockServer).getConnectedUsers();
    }

    @Test
    public void testHandleDuplicateConnect() throws IOException {
        // Create input stream with connect message
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(MessageFactory.CONNECT_MESSAGE);
        byte[] usernameBytes = username.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(usernameBytes.length);
        dos.write(usernameBytes);

        // Set up input stream
        when(mockSocket.getInputStream()).thenReturn(new ByteArrayInputStream(baos.toByteArray()));
        when(mockServer.isUsernameTaken(username)).thenReturn(true);

        // Run the handler
        handler.run();

        // Verify only one registration attempt
        verify(mockServer, times(1)).registerClient(eq(username), any(ClientHandler.class));
    }
}