package org.example.retroGamesMenu;

import javax.swing.*;
import java.awt.*;

public class RetroGamesTitlePanel extends JPanel {
    public RetroGamesTitlePanel() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Retro Games");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        int topSpace = 20;
        int leftSpace = 0;
        int bottomSpace = 30;
        int rightSpace = 0;
        setBorder(BorderFactory.createEmptyBorder(topSpace, leftSpace, bottomSpace, rightSpace));

        add(titleLabel, BorderLayout.CENTER);
    }
}