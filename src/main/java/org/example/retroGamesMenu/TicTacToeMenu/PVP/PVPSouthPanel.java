package org.example.retroGamesMenu.TicTacToeMenu.PVP;

import org.example.RetroGamesClient;
import org.example.retroGamesMenu.RetroGamesMenu;

import javax.swing.*;
import java.awt.*;


public class PVPSouthPanel extends JPanel {

    public PVPSouthPanel(RetroGamesMenu parentFrame, PVPWindow pvpWindow) {
        setLayout(new BorderLayout());

        JButton newBackButton = new JButton("Back");
        newBackButton.addActionListener(e -> {
            System.out.println("Back Tic Tac Toe PVP");
            RetroGamesClient.getInstance().sendMessageToServer("backPVP");
            parentFrame.recreateTicTacToeMenu();
        });
        add(newBackButton, BorderLayout.EAST);

        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> {
            System.out.println("New Game Tic Tac Toe Player VS Player");
            RetroGamesClient.getInstance().sendMessageToServer("newGamePVP");
            pvpWindow.resetGrid();
            pvpWindow.updateGrid();
        });
        add(newGameButton, BorderLayout.WEST);
    }
}
