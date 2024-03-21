package org.example.retroGamesMenu.RamseyGraph;

import org.example.RetroGamesClient;
import org.example.retroGamesMenu.RetroGamesMenu;

import javax.swing.*;
import java.awt.*;


public class RamseyGraphWindow extends JPanel {
    private  final DrawingPanel drawingPanel;

    public RamseyGraphWindow(RetroGamesMenu parentFrame) {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Ramsey Graph");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        drawingPanel = new DrawingPanel(parentFrame);
        add(drawingPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());

        JButton backButton = new JButton("back");
        backButton.addActionListener(e -> {
            System.out.println("Back RamseyGraph");
            RetroGamesClient.getInstance().sendMessageToServer("backRG");
            parentFrame.recreateRetroGamesMenu();
        });
        southPanel.add(backButton, BorderLayout.EAST);

        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> {
            System.out.println("New Game Ramsey Graph");
            RetroGamesClient.getInstance().sendMessageToServer("newGameRG");
            drawingPanel.resetLines();
        });
        southPanel.add(newGameButton, BorderLayout.WEST);

        add(southPanel, BorderLayout.SOUTH);
    }
}
