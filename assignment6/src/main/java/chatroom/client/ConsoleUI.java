package chatroom.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class ConsoleUI {
    private final ChatClient client;
    private final BufferedReader reader;
    private boolean running;

    public ConsoleUI(ChatClient client) {
        this.client = client;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void start() {
        running = true;
        displayWelcomeMessage();
        
        while (running) {
            try {
                String input = reader.readLine();
                handleInput(input);
            } catch (IOException e) {
                System.err.println("Error reading input: " + e.getMessage());
                break;
            }
        }
    }

    public void displayWelcomeMessage() {
        System.out.println("Welcome to the chat room!");
        System.out.println("Commands:");
        displayHelp();
    }

    private void displayHelp() {
        System.out.println("? - Display this help message");
        System.out.println("@user message - Send private message to user");
        System.out.println("@all message - Broadcast message to all users");
        System.out.println("!user - Send insult to user");
        System.out.println("who - List connected users");
        System.out.println("logoff - Disconnect from chat");
    }

    public void handleInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        input = input.trim();

        if (input.equals("?")) {
            displayHelp();
        } else if (input.equals("who")) {
            client.queryConnectedUsers();
        } else if (input.equals("logoff")) {
            client.disconnect();
            running = false;
        } else if (input.startsWith("@all ")) {
            String message = input.substring(5).trim();
            if (!message.isEmpty()) {
                client.sendBroadcastMessage(message);
            }
        } else if (input.startsWith("@")) {
            handleDirectMessage(input);
        } else if (input.startsWith("!")) {
            handleInsult(input);
        } else {
            // Optional: treat as broadcast message
            client.sendBroadcastMessage(input);
        }
    }

    private void handleDirectMessage(String input) {
        int spaceIndex = input.indexOf(' ');
        if (spaceIndex > 1) {
            String recipient = input.substring(1, spaceIndex).trim();
            String message = input.substring(spaceIndex + 1).trim();
            if (recipient.isEmpty()) {
                displayError("Recipient cannot be empty");
                return;
            }
            if (message.isEmpty()) {
                displayError("Message cannot be empty");
                return;
            }
            client.sendDirectMessage(recipient, message);
        } else {
            displayError("Invalid direct message format. Use @user message");
        }
    }

    private void handleInsult(String input) {
        if (input.length() > 1) {
            String recipient = input.substring(1).trim();
            if (!recipient.isEmpty()) {
                client.sendInsult(recipient);
            } else {
                displayError("Please specify a user to insult");
            }
        } else {
            displayError("Please specify a user to insult");
        }
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void displayError(String error) {
        System.err.println("Error: " + error);
    }
} 