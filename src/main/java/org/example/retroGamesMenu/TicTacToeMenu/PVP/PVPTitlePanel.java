package org.example.retroGamesMenu.TicTacToeMenu.PVP;

import javax.swing.*;
import java.awt.*;

public class PVPTitlePanel extends JPanel {
    public PVPTitlePanel() {
        JLabel titleLabel = new JLabel("<html><div style='text-align: center;padding-top: 20px;'>Tic Tac Toe<br>Player vs Player</div></html>");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel);
    }
}
