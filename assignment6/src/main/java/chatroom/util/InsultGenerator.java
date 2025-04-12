package chatroom.util;

/**
 * Interface for generating insults in the chat application.
 * Implementations should provide different strategies for generating insults.
 */
public interface InsultGenerator {
    /**
     * Generates an insult string.
     *
     * @return A string containing the generated insult
     */
    String generateInsult();
} 