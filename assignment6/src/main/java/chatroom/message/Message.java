package chatroom.message;

import java.io.IOException;

/**
 * Interface representing a message in the chat application.
 * All message types must implement this interface to provide
 * a unique identifier and serialization capability.
 */
public interface Message {
    /**
     * Gets the unique identifier for this message type.
     *
     * @return An integer representing the message type
     */
    int getMessageIdentifier();

    /**
     * Serializes the message into a byte array for network transmission.
     *
     * @return A byte array containing the serialized message data
     * @throws IOException If an error occurs during serialization
     */
    byte[] serialize() throws IOException;
} 