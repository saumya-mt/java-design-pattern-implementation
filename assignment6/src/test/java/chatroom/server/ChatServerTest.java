package chatroom.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@ExtendWith(MockitoExtension.class)
public class ChatServerTest {
    private ChatServer server;
    private static final int TEST_PORT = 12345;
    private static final int OCCUPIED_PORT = 12346;

    @BeforeEach
    public void setUp() {
        server = new ChatServer();
        // Start server in a separate thread since it blocks
        new Thread(() -> server.start(TEST_PORT)).start();
        // Give server time to start
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void tearDown() {
        server.shutdown();
    }

    @Test
    public void testRegisterClient() {
        ClientHandler handler = mock(ClientHandler.class);
        server.registerClient("testUser", handler);
        assertTrue(server.isUsernameTaken("testUser"));
    }

    @Test
    public void testRemoveClient() {
        ClientHandler handler = mock(ClientHandler.class);
        server.registerClient("testUser", handler);
        server.removeClient("testUser");
        assertFalse(server.isUsernameTaken("testUser"));
    }

    @Test
    public void testBroadcast() {
        ClientHandler handler1 = mock(ClientHandler.class);
        ClientHandler handler2 = mock(ClientHandler.class);
        
        server.registerClient("user1", handler1);
        server.registerClient("user2", handler2);
        
        server.broadcast("user1", "Hello everyone!");
        
        verify(handler1).sendMessage(any());
        verify(handler2).sendMessage(any());
    }

    @Test
    public void testSendDirectMessage() {
        ClientHandler sender = mock(ClientHandler.class);
        ClientHandler recipient = mock(ClientHandler.class);
        
        server.registerClient("sender", sender);
        server.registerClient("recipient", recipient);
        
        server.sendDirectMessage("sender", "recipient", "Hello!");
        
        verify(recipient).sendMessage(any());
    }

    @Test
    public void testSendDirectMessageToNonexistentUser() {
        ClientHandler sender = mock(ClientHandler.class);
        
        server.registerClient("sender", sender);
        server.sendDirectMessage("sender", "nonexistent", "Hello!");
        
        verify(sender).sendMessage(any());
    }

    @Test
    public void testGetConnectedUsers() {
        ClientHandler handler1 = mock(ClientHandler.class);
        ClientHandler handler2 = mock(ClientHandler.class);
        
        server.registerClient("user1", handler1);
        server.registerClient("user2", handler2);
        
        String[] users = server.getConnectedUsers();
        assertEquals(2, users.length);
        assertTrue(java.util.Arrays.asList(users).contains("user1"));
        assertTrue(java.util.Arrays.asList(users).contains("user2"));
    }

    @Test
    public void testRegisterDuplicateUsername() {
        ClientHandler handler1 = mock(ClientHandler.class);
        ClientHandler handler2 = mock(ClientHandler.class);
        
        server.registerClient("testUser", handler1);
        assertThrows(IllegalArgumentException.class, () -> 
            server.registerClient("testUser", handler2));
    }

    @Test
    public void testBroadcastToEmptyServer() {
        server.broadcast("sender", "Hello!");
        // Should not throw any exceptions
    }

    @Test
    public void testSendInsult() {
        ClientHandler senderHandler = mock(ClientHandler.class);
        ClientHandler recipientHandler = mock(ClientHandler.class);
        when(senderHandler.getUsername()).thenReturn("sender");
        when(recipientHandler.getUsername()).thenReturn("recipient");
        
        server.registerClient("sender", senderHandler);
        server.registerClient("recipient", recipientHandler);
        
        server.sendInsult("sender", "recipient");
        
        verify(recipientHandler).sendMessage(any());
    }

    @Test
    public void testSendInsultToNonexistentUser() {
        ClientHandler sender = mock(ClientHandler.class);
        
        server.registerClient("sender", sender);
        server.sendInsult("sender", "nonexistent");
        
        verify(sender).sendMessage(any());
    }

    @Test
    public void testServerShutdownWithActiveClients() {
        ClientHandler handler1 = mock(ClientHandler.class);
        ClientHandler handler2 = mock(ClientHandler.class);
        
        server.registerClient("user1", handler1);
        server.registerClient("user2", handler2);
        
        server.shutdown();
        
        // Verify that both clients were removed from the server
        assertFalse(server.isUsernameTaken("user1"));
        assertFalse(server.isUsernameTaken("user2"));
    }

    @Test
    public void testServerStartWithInvalidPort() {
        ChatServer newServer = new ChatServer();
        assertThrows(IllegalArgumentException.class, () -> 
            newServer.start(-1));
    }

} 