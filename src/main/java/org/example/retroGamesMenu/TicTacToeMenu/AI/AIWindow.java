package org.example.retroGamesMenu.TicTacToeMenu.AI;
import org.example.retroGamesMenu.RetroGamesMenu;

import javax.swing.*;
import java.awt.*;

public class AIWindow extends JPanel {

    private final int[][] grid;

    public AIWindow(RetroGamesMenu parentFrame) {
        this.grid = new int[3][3];

        setLayout(new BorderLayout());

        JPanel aiTitlePanel = new AITitlePanel();
        add(aiTitlePanel, BorderLayout.NORTH);

        resetGrid();

        AIDrawingPanel drawingPanel = new AIDrawingPanel(grid,this);
        add(drawingPanel, BorderLayout.CENTER);

        JPanel aiSouthPanel = new AISouthPanel(parentFrame ,this);
        add(aiSouthPanel, BorderLayout.SOUTH);
    }

    public void resetGrid() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                grid[i][j] = 0;
            }
        }
    }
    public void updateGrid() {
        repaint();
    }
}
