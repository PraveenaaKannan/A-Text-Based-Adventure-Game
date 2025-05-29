import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


public class TreasureHuntGame extends JFrame {
    private static final int NUM_ROOMS = 5;
    private static final int NUM_TREASURE_ROOMS = 3;
    private static final int MAX_ATTEMPTS = 3;

    private static final Random random = new Random();
    private static final Map<String, String> GK_QUESTIONS = new LinkedHashMap<>();
    private static final String[] ROOM_NAMES = {"Dragon's Lair", "Mysterious Cave", "Crystal Chamber", "Shadow Hall", "Enchanted Room"};
    private static final String[] TREASURE_ROOM_NAMES = {"Gold Vault", "Genie's Puzzle Room", "Trap Room"};

    private static int keyRoom;
    private static int dangerRoom;
    private static int treasureRoomWithPrize;
    private static int treasureRoomWithMath;
    private static int treasureRoomWithDanger;

    private JPanel roomPanel;
    private JLabel messageLabel;
    private int attemptsLeft;

    static {
        // Add some basic GK questions and answers
        GK_QUESTIONS.put("What is the capital of France?", "Paris");
        GK_QUESTIONS.put("What is the largest planet in our solar system?", "Jupiter");
        GK_QUESTIONS.put("Who wrote 'Romeo and Juliet'?", "William Shakespeare");
        GK_QUESTIONS.put("What is the hardest natural substance on Earth?", "Diamond");
        GK_QUESTIONS.put("What is the smallest country in the world?", "Vatican City");
    }

    public TreasureHuntGame() {
        setTitle("Treasure Hunt Game");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize components
        messageLabel = new JLabel("Welcome to the Treasure Hunt Game!", JLabel.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(messageLabel, BorderLayout.NORTH);

        roomPanel = new JPanel();
        roomPanel.setLayout(new GridLayout(1, NUM_ROOMS));
        add(roomPanel, BorderLayout.CENTER);

        displayIntroduction();
    }

    private void displayIntroduction() {
        JOptionPane.showMessageDialog(
                this,
                "Welcome to the Treasure Hunt Game!\n\n" +
                        "You are an adventurer on a mission to find a legendary treasure.\n" +
                        "To reach the treasure, you must first find the hidden key in one of the 5 rooms.\n" +
                        "But beware! Some rooms are dangerous and may end your journey instantly.\n\n" +
                        "After finding the key, you will face a second challenge in the treasure rooms.\n" +
                        "One room holds the treasure, another a puzzle, and a third is deadly.\n\n" +
                        "Choose wisely and good luck!",
                "Introduction",
                JOptionPane.INFORMATION_MESSAGE
        );

        displayWelcomeMessage();
    }

    private void displayWelcomeMessage() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you ready to begin your adventure?",
                "Welcome",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            startGame();
        } else {
            System.exit(0);
        }
    }

    private void startGame() {
        setupInitialRooms();
        attemptsLeft = MAX_ATTEMPTS;
        displayRooms(ROOM_NAMES, "Find the key! Choose a room. Attempts left: " + attemptsLeft);
    }

    private void setupInitialRooms() {
        keyRoom = random.nextInt(NUM_ROOMS);
        dangerRoom = getRandomRoom(keyRoom, NUM_ROOMS);
    }

    private void setupTreasureRooms() {
        treasureRoomWithPrize = random.nextInt(NUM_TREASURE_ROOMS);
        treasureRoomWithMath = getRandomRoom(treasureRoomWithPrize, NUM_TREASURE_ROOMS);
        treasureRoomWithDanger = getRandomRoom(treasureRoomWithPrize, NUM_TREASURE_ROOMS);
    }

    private int getRandomRoom(int excludeRoom, int range) {
        int room;
        do {
            room = random.nextInt(range);
        } while (room == excludeRoom);
        return room;
    }

    private void displayRooms(String[] roomNames, String message) {
        roomPanel.removeAll();
        messageLabel.setText(message);

        for (int i = 0; i < roomNames.length; i++) {
            JButton roomButton = new JButton(roomNames[i]);
            final int roomIndex = i;

            roomButton.addActionListener(e -> handleRoomSelection(roomIndex));
            roomPanel.add(roomButton);
        }

        roomPanel.revalidate();
        roomPanel.repaint();
    }

    private void handleRoomSelection(int chosenRoom) {
        String selectedRoomName;

        if (roomPanel.getComponentCount() == NUM_ROOMS) {
            // Initial rooms logic with attempts count
            selectedRoomName = ROOM_NAMES[chosenRoom];
            if (chosenRoom == dangerRoom) {
                JOptionPane.showMessageDialog(
                        this,
                        "You selected: " + selectedRoomName + "\nDanger! You have died. Game over.",
                        "Danger Room",
                        JOptionPane.ERROR_MESSAGE
                );
                endGame("Game over.");
            } else if (chosenRoom == keyRoom) {
                JOptionPane.showMessageDialog(
                        this,
                        "You selected: " + selectedRoomName + "\nCongratulations! You found the key!",
                        "Key Found",
                        JOptionPane.INFORMATION_MESSAGE
                );
                setupTreasureRooms();
                displayRooms(TREASURE_ROOM_NAMES, "Find the treasure! Choose a room.");
            } else {
                attemptsLeft--;
                JOptionPane.showMessageDialog(
                        this,
                        "You selected: " + selectedRoomName + "\nThis room is empty. Try again.",
                        "Empty Room",
                        JOptionPane.WARNING_MESSAGE
                );

                if (attemptsLeft > 0) {
                    displayRooms(ROOM_NAMES, "Find the key! Attempts left: " + attemptsLeft);
                } else {
                    endGame("Out of attempts! Game over.");
                }
            }
        } else {
            // Treasure rooms logic
            selectedRoomName = TREASURE_ROOM_NAMES[chosenRoom];
            if (chosenRoom == treasureRoomWithDanger) {
                JOptionPane.showMessageDialog(
                        this,
                        "You selected: " + selectedRoomName + "\nDanger! You have died. Game over.",
                        "Danger Room",
                        JOptionPane.ERROR_MESSAGE
                );
                endGame("Game over.");
            } else if (chosenRoom == treasureRoomWithPrize) {
                JOptionPane.showMessageDialog(
                        this,
                        "You selected: " + selectedRoomName + "\nCongratulations! You found the treasure!",
                        "Treasure Found",
                        JOptionPane.INFORMATION_MESSAGE
                );
                endGame("Congratulations! You won!");
            } else if (chosenRoom == treasureRoomWithMath) {
                JOptionPane.showMessageDialog(
                        this,
                        "You selected: " + selectedRoomName + "\nWelcome to the Genie's Puzzle Room!",
                        "Genie's Room",
                        JOptionPane.INFORMATION_MESSAGE
                );
                handleGenieRoom();
            }
        }
    }

    private void handleGenieRoom() {
        String[] challenges = {"General Knowledge", "Math Puzzle", "Word Game"};
        String choice = (String) JOptionPane.showInputDialog(
                this,
                "Choose your challenge:",
                "Genie Challenge",
                JOptionPane.QUESTION_MESSAGE,
                null,
                challenges,
                challenges[0]
        );

        if (choice == null) {
            endGame("Game over. You didn't choose a challenge.");
        } else if (choice.equals("General Knowledge")) {
            askGKQuestion();
        } else if (choice.equals("Math Puzzle")) {
            askMathPuzzle();
        } else if (choice.equals("Word Game")) {
            playWordGame();
        }
    }

    private void askGKQuestion() {
        String[] questions = GK_QUESTIONS.keySet().toArray(new String[0]);
        String question = questions[random.nextInt(questions.length)];
        String correctAnswer = GK_QUESTIONS.get(question);

        String answer = JOptionPane.showInputDialog(this, question, "GK Question", JOptionPane.QUESTION_MESSAGE);

        if (answer != null && answer.equalsIgnoreCase(correctAnswer)) {
            endGame("Correct! You have won the treasure!");
        } else {
            endGame("Incorrect answer. Game over.");
        }
    }

    private void askMathPuzzle() {
        int a = random.nextInt(10) + 1;
        int b = random.nextInt(10) + 1;
        int correctAnswer = a + b;

        String answerStr = JOptionPane.showInputDialog(this, "Solve: " + a + " + " + b, "Math Puzzle", JOptionPane.QUESTION_MESSAGE);

        try {
            int answer = Integer.parseInt(answerStr);
            if (answer == correctAnswer) {
                endGame("Correct! You have won the treasure!");
            } else {
                endGame("Incorrect answer. Game over.");
            }
        } catch (NumberFormatException e) {
            endGame("Invalid input. Game over.");
        }
    }

    private void playWordGame() {
        String[] words = {"java", "programming", "challenge", "genie", "treasure"};
        String wordToGuess = words[random.nextInt(words.length)];
        String scrambledWord = scrambleWord(wordToGuess);

        String answer = JOptionPane.showInputDialog(this, "Unscramble this word: " + scrambledWord, "Word Game", JOptionPane.QUESTION_MESSAGE);

        if (answer != null && answer.equalsIgnoreCase(wordToGuess)) {
            endGame("Correct! You have won the treasure!");
        } else {
            endGame("Incorrect answer. Game over.");
        }
    }

    private String scrambleWord(String word) {
        List<Character> letters = new ArrayList<>();
        for (char c : word.toCharArray()) {
            letters.add(c);
        }
        Collections.shuffle(letters);
        StringBuilder scrambledWord = new StringBuilder();
        for (char c : letters) {
            scrambledWord.append(c);
        }
        return scrambledWord.toString();
    }

    private void endGame(String message) {
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Do you want to play again?",
                "Play Again?",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            startGame();
        } else {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TreasureHuntGame().setVisible(true));
    }
}

