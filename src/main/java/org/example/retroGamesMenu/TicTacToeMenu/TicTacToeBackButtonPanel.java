package org.example.retroGamesMenu.TicTacToeMenu;
import org.example.RetroGamesClient;
import org.example.retroGamesMenu.RetroGamesMenu;

import javax.swing.*;
import java.awt.*;

public class TicTacToeBackButtonPanel extends JPanel {
    public TicTacToeBackButtonPanel(RetroGamesMenu parentFrame) {
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            System.out.println("Back Tic Tac Toe");
            RetroGamesClient.getInstance().sendMessageToServer("backTicTacToe");
            // Intoarecere in meniul de jocuri
            parentFrame.recreateRetroGamesMenu();
        });

        add(backButton);
    }
}

