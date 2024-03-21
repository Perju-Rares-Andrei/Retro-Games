package org.example.retroGamesMenu;

import org.example.retroGamesMenu.TicTacToeMenu.TicTacToeMenu;

import javax.swing.*;
import java.awt.*;

public class RetroGamesMenu extends JFrame {
    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 800;

    public RetroGamesMenu() {
        setTitle("Retro Games");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        retroGamesMenuLayout();
     }

    public void retroGamesMenuLayout() {

        //Creez layout-ul pentru meniu

        Container contentPane = getContentPane();
        contentPane.removeAll();
        contentPane.setLayout(new BorderLayout());

        RetroGamesTitlePanel retroGamesTitlePanel = new RetroGamesTitlePanel();
        contentPane.add(retroGamesTitlePanel, BorderLayout.NORTH);

        RetroGamesGridPanel retroGamesGridPanel = new RetroGamesGridPanel(this,this);
        contentPane.add(retroGamesGridPanel, BorderLayout.CENTER);

        RetroGamesExitPanel retroGamesExitPanel = new RetroGamesExitPanel();
        contentPane.add(retroGamesExitPanel, BorderLayout.SOUTH);


        revalidate();
        repaint();
    }
    public void recreateRetroGamesMenu() {
        // Metoda pentru a reconstrui layout-ul meniului
        retroGamesMenuLayout();
    }

    public void recreateTicTacToeMenu() {
        // Metoda pentru a reconstrui layout-ul meniului pentru tic tac toe
        getContentPane().removeAll();

        TicTacToeMenu ticTacToeMenu = new TicTacToeMenu(this);
        getContentPane().add(ticTacToeMenu);

        revalidate();
        repaint();
    }

}
