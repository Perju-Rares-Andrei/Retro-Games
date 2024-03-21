package org.example.retroGamesMenu.TicTacToeMenu.PVP;

import org.example.retroGamesMenu.RetroGamesMenu;

import javax.swing.*;
import java.awt.*;

public class PVPWindow extends JPanel {

    private final int[][] grid;
    private final PVPDrawingPanel drawingPanel;

    public PVPWindow(RetroGamesMenu parentFrame) {
        this.grid = new int[3][3];

        setLayout(new BorderLayout());

        JPanel titlePanel = new PVPTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        resetGrid();

        drawingPanel = new PVPDrawingPanel(grid,this);
        add(drawingPanel, BorderLayout.CENTER);

        JPanel southPanel = new PVPSouthPanel(parentFrame ,this);
        add(southPanel, BorderLayout.SOUTH);
    }

    public void resetGrid() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                grid[i][j] = 0;
            }
        }
    }

    public void updateGrid() {
        drawingPanel.repaint();
    }
}