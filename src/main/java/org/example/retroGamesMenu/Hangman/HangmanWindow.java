package org.example.retroGamesMenu.Hangman;

import org.example.RetroGamesClient;
import org.example.retroGamesMenu.RetroGamesMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class HangmanWindow extends JPanel {

    private final JLabel hangmanImageLabel;
    private final JPanel wordPanel;
    private final RetroGamesMenu parentFrame;

    private String maskedWord;
    private int numLives = 6;

    private Timer responseTimer;

    private boolean gameIsOver = false;


    public HangmanWindow(RetroGamesMenu parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Hangman");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JPanel centralPanel = new JPanel(new BorderLayout());

        hangmanImageLabel = new JLabel();
        hangmanImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        updateHangmanImage();
        hangmanImageLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        centralPanel.add(hangmanImageLabel, BorderLayout.NORTH);

        wordPanel = new JPanel();
        wordPanel.setLayout(new GridLayout(1, 0));
        centralPanel.add(wordPanel, BorderLayout.CENTER);

        add(centralPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());

        JButton backButton = new JButton("back");
        backButton.addActionListener(e -> {
            System.out.println("Back Hangman");
            gameIsOver = true;
            RetroGamesClient.getInstance().sendMessageToServer("backHM");
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            RetroGamesClient.getInstance().sendMessageToServer("backHM");
            parentFrame.recreateRetroGamesMenu();
        });
        southPanel.add(backButton, BorderLayout.EAST);

        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> {
            System.out.println("New Game Hangman");
            newGame();
            RetroGamesClient.getInstance().sendMessageToServer("newGameHM");

        });
        southPanel.add(newGameButton, BorderLayout.WEST);

        add(southPanel, BorderLayout.SOUTH);

        setFocusable(true);
        requestFocus();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(evt -> {
            if(gameIsOver){
                return false;
            }

            if (evt.getID() == KeyEvent.KEY_PRESSED) {
                Timer keyPressTimer = new Timer(100, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String serverMessage = RetroGamesClient.getInstance().getLastReceivedMessage();
                        handleFirstServerMessage(serverMessage);
                    }
                });
                keyPressTimer.setRepeats(false);
                keyPressTimer.start();

                char c = Character.toUpperCase(evt.getKeyChar());
                System.out.println(c);
                if (Character.isLetter(c)) {
                    guessLetter(c);
                }
            }
            return false;
        });

        responseTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String serverMessage = RetroGamesClient.getInstance().getLastReceivedMessage();
                handleFirstServerMessage(serverMessage);
            }
        });
        responseTimer.setRepeats(false);
        responseTimer.start();
    }

    private void newGame() {
        numLives = 6;
        updateHangmanImage();
        gameIsOver=false;

        Timer newGameTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String serverMessage = RetroGamesClient.getInstance().getLastReceivedMessage();
                handleFirstServerMessage(serverMessage);
            }
        });
        newGameTimer.setRepeats(false);
        newGameTimer.start();

        maskedWord = null;
        wordPanel.removeAll();
        requestFocus();
    }

    private void guessLetter(char letter) {
        String message = "guess:" + letter;
        RetroGamesClient.getInstance().sendMessageToServer(message);

        Timer guessLetterTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String serverMessage = RetroGamesClient.getInstance().getLastReceivedMessage();
                SwingUtilities.invokeLater(() -> handleServerResponse(serverMessage));
            }
        });
        guessLetterTimer.setRepeats(false);
        guessLetterTimer.start();
    }

    public void handleFirstServerMessage(String serverMessage) {
        if (serverMessage.startsWith("WORD: ")) {
            maskedWord = serverMessage.substring(6).trim().replace('*', ' ');
            updateWordPanel();
        }
    }


    public void handleServerResponse(String serverResponse) {
        if (serverResponse.startsWith("GOOD:")) {
            String[] parts = serverResponse.substring(5).split(":");
            if (parts.length == 2) {
                numLives = Integer.parseInt(parts[0].trim());
                String maskedWordWithAsterisks = parts[1].trim();
                maskedWord = maskedWordWithAsterisks.replace('*', ' ');
                updateWordPanel();
                updateHangmanImage();
            }
        } else if (serverResponse.startsWith("BAD:")) {
            String[] parts = serverResponse.substring(4).split(":");
            if (parts.length == 2) {
                numLives = Integer.parseInt(parts[0].trim());
                updateHangmanImage();
            }
        } else if (serverResponse.startsWith("Congratulations! You won!:")) {
            String[] parts = serverResponse.substring(26).split(":");
            if (parts.length == 1) {
                numLives = 0; // Set lives to 0 to indicate the game is over
                maskedWord = parts[0].trim().replace('*', ' ');
                updateWordPanel();
                updateHangmanImage();
                showGameResult(serverResponse);
                gameIsOver = true;
            }
        } else if (serverResponse.startsWith("GAME OVER:")) {
            String[] parts = serverResponse.substring(10).split(":");
            if (parts.length == 1) {
                numLives = 0; // Set lives to 0 to indicate the game is over
                maskedWord = parts[0].trim().replace('*', ' ');
                updateWordPanel();
                updateHangmanImage();
                showGameResult(serverResponse);
                gameIsOver = true;
            }
        }
    }

    private void updateWordPanel() {
        wordPanel.removeAll();

        int labelHeight = 40;
        int labelWidth = 40;

        int panelHeight = labelHeight + wordPanel.getInsets().top + wordPanel.getInsets().bottom;
        int verticalSpacing = (wordPanel.getHeight() - panelHeight) / 2;

        for (char c : maskedWord.toCharArray()) {
            JLabel letterLabel = new JLabel(String.valueOf(c));
            letterLabel.setHorizontalAlignment(SwingConstants.CENTER);
            letterLabel.setFont(new Font("Arial", Font.BOLD, 18));
            letterLabel.setPreferredSize(new Dimension(labelWidth, labelHeight));
            letterLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            letterLabel.setOpaque(false);

            JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, verticalSpacing));
            labelPanel.setOpaque(false);
            labelPanel.add(letterLabel);

            wordPanel.add(labelPanel);
        }

        revalidate();
        repaint();
    }

    private void updateHangmanImage() {
        String imagePath = "/Users/rares/Desktop/Java-2024/Retro-Games/src/main/resources/hangman" + (7 - numLives) + ".png";
        ImageIcon imageIcon = new ImageIcon(imagePath);
        hangmanImageLabel.setIcon(imageIcon);
        revalidate();
        repaint();
    }

    private void showGameResult(String message) {
        JOptionPane.showMessageDialog(this, message, "Game Result", JOptionPane.INFORMATION_MESSAGE);
    }
}
