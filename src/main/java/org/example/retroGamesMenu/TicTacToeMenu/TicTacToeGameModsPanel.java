package org.example.retroGamesMenu.TicTacToeMenu;
import org.example.RetroGamesClient;
import org.example.retroGamesMenu.TicTacToeMenu.AI.AIWindow;
import org.example.retroGamesMenu.TicTacToeMenu.PVP.PVPWindow;
import org.example.retroGamesMenu.RetroGamesMenu;

import javax.swing.*;
import java.awt.*;

public class TicTacToeGameModsPanel extends JPanel {
    public RetroGamesMenu parentFrame;
    public TicTacToeGameModsPanel(RetroGamesMenu parentFrame) {
        this.parentFrame=parentFrame;

        setLayout(new GridLayout(3, 1, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(100, 0, 200, 125));

        //Butoanele pentru fiecare mod de joc

        JButton pvpButton = new JButton("Player VS Player");
        JButton aiEasyButton = new JButton("Player VS AI Easy");
        JButton aiHardButton = new JButton("Player VS AI Hard");

        Dimension buttonSize = new Dimension(250, 40);

        pvpButton.setPreferredSize(buttonSize);
        aiEasyButton.setPreferredSize(buttonSize);
        aiHardButton.setPreferredSize(buttonSize);

        Font buttonFont = new Font("Arial", Font.PLAIN, 18);
        pvpButton.setFont(buttonFont);
        aiEasyButton.setFont(buttonFont);
        aiHardButton.setFont(buttonFont);

        //Create the window for each game

        pvpButton.addActionListener(e -> {
            System.out.println("PVP button clicked");
            RetroGamesClient.getInstance().sendMessageToServer("PVP");
            parentFrame.getContentPane().removeAll(); // Clear the window
            parentFrame.getContentPane().add(new PVPWindow(parentFrame));
            parentFrame.revalidate();
            parentFrame.repaint();
        });

        aiEasyButton.addActionListener(e -> {
            RetroGamesClient.getInstance().sendMessageToServer("AIEasy");
            System.out.println("PVAI Easy button clicked");
            parentFrame.getContentPane().removeAll();
            parentFrame.getContentPane().add(new AIWindow(parentFrame));
            parentFrame.revalidate();
            parentFrame.repaint();
        });

        aiHardButton.addActionListener(e -> {
            RetroGamesClient.getInstance().sendMessageToServer("AIHard");
            parentFrame.getContentPane().removeAll();
            parentFrame.getContentPane().add(new AIWindow(parentFrame));
            parentFrame.revalidate();
            parentFrame.repaint();
        });

        add(pvpButton);
        add(aiEasyButton);
        add(aiHardButton);
    }


}

