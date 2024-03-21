package org.example.retroGamesMenu.TicTacToeMenu.AI;

import javax.swing.*;
import java.awt.*;

public class AITitlePanel extends JPanel {
    public AITitlePanel() {
        setLayout(new FlowLayout());
        JLabel titleLabel = new JLabel("<html><div style='text-align: center;padding-top: 20px;'>Tic Tac Toe<br>Player vs AI</div></html>");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel);
    }
}

