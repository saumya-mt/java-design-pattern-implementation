package chatroom.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyString;

import java.io.*;

@ExtendWith(MockitoExtension.class)
public class ConsoleUITest {
    private ConsoleUI ui;
    private ChatClient mockClient;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final InputStream originalIn = System.in;

    @BeforeEach
    public void setUp() {
        mockClient = mock(ChatClient.class);
        ui = new ConsoleUI(mockClient);
        
        // Redirect stdout and stderr
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.setIn(originalIn);
        outContent.reset();
        errContent.reset();
    }

    @Test
    public void testDisplayWelcomeMessage() {
        ui.displayWelcomeMessage();
        assertTrue(outContent.toString().contains("Welcome to the chat room"));
        assertTrue(outContent.toString().contains("Commands:"));
    }

    @Test
    public void testDisplayMessage() {
        String message = "Test message";
        ui.displayMessage(message);
        assertEquals(message + System.lineSeparator(), outContent.toString());
    }

    @Test
    public void testDisplayError() {
        String error = "Test error";
        ui.displayError(error);
        assertEquals("Error: " + error + System.lineSeparator(), errContent.toString());
    }

    @Test
    public void testHandleInputBroadcast() {
        String message = "@all Hello everyone";
        ui.handleInput(message);
        verify(mockClient).sendBroadcastMessage("Hello everyone");
    }

    @Test
    public void testHandleInputDirectMessage() {
        String message = "@user1 Hello there";
        ui.handleInput(message);
        verify(mockClient).sendDirectMessage("user1", "Hello there");
    }

    @Test
    public void testHandleInputInsult() {
        String message = "!user1";
        ui.handleInput(message);
        verify(mockClient).sendInsult("user1");
    }

    @Test
    public void testHandleInputWho() {
        ui.handleInput("who");
        verify(mockClient).queryConnectedUsers();
    }

    @Test
    public void testHandleInputLogoff() {
        ui.handleInput("logoff");
        verify(mockClient).disconnect();
    }

    @Test
    public void testHandleInputHelp() {
        outContent.reset(); // Clear any previous output
        ui.handleInput("?");
        
        // Get output and normalize line endings
        String output = outContent.toString().replace("\r\n", "\n");
        
        // Create expected help text with normalized line endings
        String expectedHelp = String.join("\n",
            "? - Display this help message",
            "@user message - Send private message to user",
            "@all message - Broadcast message to all users",
            "!user - Send insult to user",
            "who - List connected users",
            "logoff - Disconnect from chat",
            ""  // Account for trailing newline
        );
        
        // Compare exact strings
        assertEquals(expectedHelp, output);
    }

    @Test
    public void testHandleEmptyInput() {
        ui.handleInput("");
        verifyNoInteractions(mockClient);
    }

    @Test
    public void testHandleNullInput() {
        ui.handleInput(null);
        verifyNoInteractions(mockClient);
    }

    @Test
    public void testHandleInputEmptyDirectMessage() {
        String message = "@user1 ";
        ui.handleInput(message);
        assertEquals("Error: Invalid direct message format. Use @user message" + System.lineSeparator(), errContent.toString());
    }

    @Test
    public void testHandleInputEmptyRecipient() {
        String message = "@ ";
        ui.handleInput(message);
        assertEquals("Error: Invalid direct message format. Use @user message" + System.lineSeparator(), errContent.toString());
    }

    @Test
    public void testHandleInputInvalidDirectMessageFormat() {
        String message = "@user1";
        ui.handleInput(message);
        assertEquals("Error: Invalid direct message format. Use @user message" + System.lineSeparator(), errContent.toString());
    }

    @Test
    public void testHandleInputEmptyInsult() {
        String message = "!";
        ui.handleInput(message);
        assertEquals("Error: Please specify a user to insult" + System.lineSeparator(), errContent.toString());
    }

    @Test
    public void testHandleInputEmptyInsultRecipient() {
        String message = "! ";
        ui.handleInput(message);
        assertEquals("Error: Please specify a user to insult" + System.lineSeparator(), errContent.toString());
    }

    @Test
    public void testHandleInputEmptyBroadcast() {
        String message = "@all ";
        ui.handleInput(message);
        verify(mockClient, never()).sendBroadcastMessage(anyString());
    }

    @Test
    public void testHandleInputWhitespaceOnly() {
        String message = "   ";
        ui.handleInput(message);
        verifyNoInteractions(mockClient);
    }

    @Test
    public void testHandleInputNewlineOnly() {
        String message = "\n";
        ui.handleInput(message);
        verifyNoInteractions(mockClient);
    }

    @Test
    public void testHandleInputTabOnly() {
        String message = "\t";
        ui.handleInput(message);
        verifyNoInteractions(mockClient);
    }

    @Test
    public void testHandleInputMixedWhitespace() {
        String message = " \t\n ";
        ui.handleInput(message);
        verifyNoInteractions(mockClient);
    }

    @Test
    public void testHandleInputLongMessage() {
        String message = "a".repeat(1000); // Long message
        ui.handleInput(message);
        verify(mockClient).sendBroadcastMessage(message);
    }

    @Test
    public void testHandleInputSpecialCharacters() {
        String message = "@user1 Hello! @#$%^&*()";
        ui.handleInput(message);
        verify(mockClient).sendDirectMessage("user1", "Hello! @#$%^&*()");
    }

    @Test
    public void testHandleInputUnicodeCharacters() {
        String message = "@user1 你好世界";
        ui.handleInput(message);
        verify(mockClient).sendDirectMessage("user1", "你好世界");
    }
} 