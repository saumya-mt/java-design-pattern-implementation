package chatroom.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InsultGeneratorTest {
    @Test
    void testGenerateInsult() {
        InsultGenerator generator = new SimpleInsultGenerator();
        String insult = generator.generateInsult();
        assertNotNull(insult);
        assertFalse(insult.isEmpty());
    }

    @Test
    void testGenerateInsultFormat() {
        InsultGenerator generator = new SimpleInsultGenerator();
        String insult = generator.generateInsult();
        assertTrue(insult.startsWith("You're") || 
                  insult.startsWith("I'd") || 
                  insult.startsWith("You have") || 
                  insult.startsWith("Your") || 
                  insult.startsWith("You're about"));
    }
} 