package org.example.retroGamesMenu.TicTacToeMenu;
import org.example.retroGamesMenu.RetroGamesMenu;

import javax.swing.*;
import java.awt.*;

public class TicTacToeMenu extends JPanel {

    public TicTacToeMenu(RetroGamesMenu parentFrame) {

        setLayout(new BorderLayout());

        // Game Title Panel
        TicTacToeTitlePanel titlePanel = new TicTacToeTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // Central Panel
        TicTacToeCentralPanel centralPanel = new TicTacToeCentralPanel();
        add(centralPanel, BorderLayout.CENTER);

        // Game Image Panel
        TicTacToeImagePanel imagePanel = new TicTacToeImagePanel();
        centralPanel.add(imagePanel, BorderLayout.NORTH);

        // Game Modes Panel
        TicTacToeGameModsPanel gameModsPanel = new TicTacToeGameModsPanel(parentFrame);
        centralPanel.add(gameModsPanel, BorderLayout.EAST);

        // Back Button Panel
        TicTacToeBackButtonPanel backButtonPanel = new TicTacToeBackButtonPanel(parentFrame);
        add(backButtonPanel, BorderLayout.SOUTH);
    }




}
