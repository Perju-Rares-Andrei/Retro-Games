package org.example.retroGamesMenu.TicTacToeMenu;

import javax.swing.*;
import java.awt.*;

public class TicTacToeTitlePanel extends JPanel {
    public TicTacToeTitlePanel() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Tic Tac Toe");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        int topSpace = 30;
        int leftSpace = 0;
        int bottomSpace = 0;
        int rightSpace = 0;
        setBorder(BorderFactory.createEmptyBorder(topSpace, leftSpace, bottomSpace, rightSpace));
        add(titleLabel, BorderLayout.NORTH);
    }
}

