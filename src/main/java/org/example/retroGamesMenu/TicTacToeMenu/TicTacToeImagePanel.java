package org.example.retroGamesMenu.TicTacToeMenu;
import javax.swing.*;
import java.awt.*;

public class TicTacToeImagePanel extends JPanel {
    public TicTacToeImagePanel() {
        setLayout(new BorderLayout());
        ImageIcon gameIcon = new ImageIcon("/Users/rares/Desktop/PA/RetroGames/src/main/resources/tic-tac-toe.png");
        Image scaledImage = gameIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        ImageIcon scaledGameIcon = new ImageIcon(scaledImage);
        JLabel gamePhotoLabel = new JLabel(scaledGameIcon);
        gamePhotoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(gamePhotoLabel, BorderLayout.NORTH);
    }
}

