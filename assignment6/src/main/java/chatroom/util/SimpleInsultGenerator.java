package chatroom.util;

import java.util.Random;

/**
 * A simple implementation of the InsultGenerator interface.
 * Provides a collection of predefined insults and randomly selects one when requested.
 */
public class SimpleInsultGenerator implements InsultGenerator {
    private static final String[] INSULTS = {
        "You're as useful as a screen door on a submarine.",
        "You're wrong and you'll always be wrong.",
        "You have the charm of a burning orphanage.",
        "You're about as helpful as a chocolate teapot.",
        "You're about as sharp as a marble."
    };
    
    private final Random random;

    /**
     * Constructs a new SimpleInsultGenerator with a random number generator.
     */
    public SimpleInsultGenerator() {
        this.random = new Random();
    }

    /**
     * Generates a random insult from the predefined collection of insults.
     *
     * @return A randomly selected insult string
     */
    @Override
    public String generateInsult() {
        return INSULTS[random.nextInt(INSULTS.length)];
    }
} 