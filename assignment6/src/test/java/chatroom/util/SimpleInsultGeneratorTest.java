package chatroom.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimpleInsultGeneratorTest {
    @Test
    void testGenerateInsult() {
        SimpleInsultGenerator generator = new SimpleInsultGenerator();
        String insult = generator.generateInsult();
        assertNotNull(insult);
        assertFalse(insult.isEmpty());
    }

    @Test
    void testGenerateInsultFormat() {
        SimpleInsultGenerator generator = new SimpleInsultGenerator();
        String insult = generator.generateInsult();
        assertTrue(insult.startsWith("You") || insult.startsWith("You're"), 
                  "Insult should start with 'You' or 'You're', but was: " + insult);
    }
} 