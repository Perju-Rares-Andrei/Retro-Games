package org.example.retroGamesMenu;
import org.example.RetroGamesClient;

import javax.swing.*;
import java.awt.*;

public class RetroGamesGridPanel extends JPanel {

    public RetroGamesGridPanel(JFrame parentFrame, RetroGamesMenu retroGamesMenu) {
        setLayout(new GridLayout(2, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //Butoanele pentru fiecare joc

        addGameButton(parentFrame, "Tic-Tac-Toe", "/Users/rares/Desktop/Java-2024/Retro-Games/src/main/resources/tic-tac-toe.png",retroGamesMenu);
        addGameButton(parentFrame, "Hangman", "/Users/rares/Desktop/Java-2024/Retro-Games/src/main/resources/hangman.png",retroGamesMenu);
        addGameButton(parentFrame, "Ramsey Graph", "/Users/rares/Desktop/Java-2024/Retro-Games/src/main/resources/ramsey-graph.png",retroGamesMenu);
        addGameButton(parentFrame, "Rock Paper Scissors", "/Users/rares/Desktop/Java-2024/Retro-Games/src/main/resources/rockPaperScissors.png",retroGamesMenu);
    }

    private void addGameButton(JFrame parentFrame, String gameTitle, String imagePath, RetroGamesMenu retroGamesMenu) {
        JButton gameButton = new JButton();
        gameButton.setLayout(new BorderLayout());

        // Game Title
        JLabel titleLabel = new JLabel(gameTitle);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Adjust the font size
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gameButton.add(titleLabel, BorderLayout.NORTH);

        // Game image
        ImageIcon imageIcon = new ImageIcon(imagePath);
        if (imageIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
            Image image = imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(image));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            gameButton.add(imageLabel, BorderLayout.CENTER);
        } else {
            System.err.println("Failed to load image: " + imagePath);
        }

        gameButton.addActionListener(e -> {
            RetroGamesClient.getInstance().sendMessageToServer(gameTitle);
            RetroGamesOpenGameMenu.openGameMenu(parentFrame, gameTitle, retroGamesMenu);
        });


        add(gameButton);
    }
}
