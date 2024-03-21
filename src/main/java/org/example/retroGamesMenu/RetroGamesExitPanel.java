package org.example.retroGamesMenu;

import javax.swing.*;
public class RetroGamesExitPanel extends JButton {
    public RetroGamesExitPanel() {
        super("Exit");
        addActionListener(e -> {
            System.out.println("Exit button clicked");
            SwingUtilities.getWindowAncestor(this).dispose();
            System.exit(0);
        });
    }
}
