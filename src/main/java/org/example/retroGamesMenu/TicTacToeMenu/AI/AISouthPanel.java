package org.example.retroGamesMenu.TicTacToeMenu.AI;

import org.example.RetroGamesClient;
import org.example.retroGamesMenu.RetroGamesMenu;

import javax.swing.*;
import java.awt.*;

public class AISouthPanel extends JPanel {

    public AISouthPanel(RetroGamesMenu parentFrame, AIWindow aiWindow) {

        setLayout(new BorderLayout());

        JButton newBackButton = new JButton("Back");
        newBackButton.addActionListener(e -> {
            System.out.println("Back Tic Tac Toe PVAi");
            RetroGamesClient.getInstance().sendMessageToServer("backAI");
            parentFrame.recreateTicTacToeMenu();
        });
        add(newBackButton, BorderLayout.EAST);

        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> {
            System.out.println("New Game Tic Tac Toe Player VS AI");
            RetroGamesClient.getInstance().sendMessageToServer("newGameAI");
            aiWindow.resetGrid();
            aiWindow.updateGrid();
        });
        add(newGameButton, BorderLayout.WEST);
    }

}

