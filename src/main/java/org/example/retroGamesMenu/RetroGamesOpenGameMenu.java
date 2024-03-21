package org.example.retroGamesMenu;

import org.example.retroGamesMenu.Hangman.HangmanWindow;
import org.example.retroGamesMenu.RamseyGraph.RamseyGraphWindow;
import org.example.retroGamesMenu.RockPaperScissors.RockPaperScissorsWindow;
import org.example.retroGamesMenu.TicTacToeMenu.TicTacToeMenu;

import javax.swing.*;
import java.awt.*;

public class RetroGamesOpenGameMenu {

    public static void openGameMenu(JFrame parentFrame, String gameTitle, RetroGamesMenu retroGamesMenu) {
        parentFrame.setTitle("");
        Container contentPane = parentFrame.getContentPane();
        contentPane.removeAll();


        // Crearea meniului pentru fiecare tip de joc
        if (gameTitle.equals("Tic-Tac-Toe")) {
            TicTacToeMenu ticTacToeMenu = new TicTacToeMenu(retroGamesMenu);
            contentPane.add(ticTacToeMenu);
        } else if (gameTitle.equals("Hangman")) {
            HangmanWindow hangmanWindow = new HangmanWindow(retroGamesMenu);
            contentPane.add(hangmanWindow);
        } else if (gameTitle.equals("Ramsey Graph")) {
            RamseyGraphWindow ramseyGraphWindow = new RamseyGraphWindow(retroGamesMenu);
            contentPane.add(ramseyGraphWindow);
        } else if (gameTitle.equals("Rock Paper Scissors")) {
            RockPaperScissorsWindow rockPaperScissors= new RockPaperScissorsWindow(retroGamesMenu);
            contentPane.add(rockPaperScissors);
        }

        parentFrame.revalidate();
        parentFrame.repaint();
    }
}
