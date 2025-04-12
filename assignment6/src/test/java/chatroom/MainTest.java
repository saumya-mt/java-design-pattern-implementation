package chatroom;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Test
    void testMainWithNoArguments() {
        System.setOut(new PrintStream(outContent));
        String[] args = {};
        Main.main(args);
        assertTrue(outContent.toString().contains("Usage:"));
        System.setOut(originalOut);
    }
    
    @Test
    void testMainWithInvalidCommand() {
        System.setOut(new PrintStream(outContent));
        String[] args = {"invalid"};
        Main.main(args);
        assertTrue(outContent.toString().contains("Unknown command"));
        System.setOut(originalOut);
    }
    
    @Test
    void testMainWithInvalidServerArguments() {
        System.setOut(new PrintStream(outContent));
        String[] args = {"server", "not_a_number"};
        Main.main(args);
        assertTrue(outContent.toString().contains("Port must be a number"));
        System.setOut(originalOut);
    }
    
    @Test
    void testMainWithInvalidClientArguments() {
        System.setOut(new PrintStream(outContent));
        String[] args = {"client", "localhost", "not_a_number", "user"};
        Main.main(args);
        assertTrue(outContent.toString().contains("Port must be a number"));
        System.setOut(originalOut);
    }
    
    @Test
    void testMainWithInsufficientServerArguments() {
        System.setOut(new PrintStream(outContent));
        String[] args = {"server"};
        Main.main(args);
        assertTrue(outContent.toString().contains("Server usage"));
        System.setOut(originalOut);
    }
    
    @Test
    void testMainWithInsufficientClientArguments() {
        System.setOut(new PrintStream(outContent));
        String[] args = {"client", "localhost", "8080"};
        Main.main(args);
        assertTrue(outContent.toString().contains("Client usage"));
        System.setOut(originalOut);
    }
} 