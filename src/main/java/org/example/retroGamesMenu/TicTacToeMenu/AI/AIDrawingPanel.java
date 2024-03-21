
package org.example.retroGamesMenu.TicTacToeMenu.AI;
import org.example.RetroGamesClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class AIDrawingPanel extends JPanel {
    private final int[][] grid;
    public AIDrawingPanel(int[][] grid, AIWindow aiWindow) {
        this.grid = grid;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String lastMessage = handleServerMessage();
                if (lastMessage != null) {
                    return;
                }

                int x = e.getX();
                int y = e.getY();

                if (x >= 95 && x <= 395 && y >= 95 && y <= 395) {
                    int row = (y - 95) / 100;
                    int col = (x - 95) / 100;

                    if (grid[row][col] == 0) {
                        // Daca pozia in matrice nu este ocupate fac update lapozitie
                        grid[row][col] = 1;
                        // Trimit pozitia la server
                        RetroGamesClient.getInstance().sendMessageToServer(row + " " + col);

                        // Astept mesajel de la server cu pozitia AI-ului
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                        String aiLastReceivedMessage;
                        aiLastReceivedMessage = RetroGamesClient.getInstance().getLastReceivedMessage();
                        // Fac update la matrice cu pozitia AI-ului
                        if(aiLastReceivedMessage!=null) {
                            String[] tokens = aiLastReceivedMessage.split(" ");
                            if (tokens.length == 3) {
                                int row0 = Integer.parseInt(tokens[1]);
                                int col0 = Integer.parseInt(tokens[2]);
                                grid[row0][col0] = 2;
                            }
                            aiWindow.updateGrid();
                        }
                    }
                }
            }


        });
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Desenez careul
        g2d.setStroke(new BasicStroke(4));
        g2d.setColor(Color.BLACK);
        for (int i = 1; i < 3; i++) {
            int x = i * 100;
            int y = i * 100;
            g2d.drawLine(x + 95, 95, x + 95, 395);
            g2d.drawLine(95, y + 95, 395, y + 95);
        }

        //Desenarea simbolurilor corespunzatoare fiecarui jucator
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[i][j] == 1) {
                    drawXSymbol(g2d, i, j);
                } else if (grid[i][j] == 2) {
                    drawOSymbol(g2d, i, j);
                }
            }
        }

    }

    private void drawXSymbol(Graphics2D g2d, int row, int col) {
        int x = col * 100 + 145;
        int y = row * 100 + 145;
        g2d.setStroke(new BasicStroke(6));
        g2d.setColor(Color.BLUE);
        g2d.drawLine(x - 30, y - 30, x + 30, y + 30);
        g2d.drawLine(x + 30, y - 30, x - 30, y + 30);
    }

    private void drawOSymbol(Graphics2D g2d, int row, int col) {
        int x = col * 100 + 145;
        int y = row * 100 + 145;
        g2d.setStroke(new BasicStroke(6));
        g2d.setColor(Color.RED);
        g2d.drawOval(x - 30, y - 30, 60, 60);
    }

    private String handleServerMessage() {
        String serverMessage;
        serverMessage = RetroGamesClient.getInstance().getLastReceivedMessage();

        // Proceses mesajul primit de la server in cazul de castig sau egalitate
        if (serverMessage != null && (serverMessage.startsWith("Player") || serverMessage.contains("draw!"))) {
            JOptionPane.showMessageDialog(this,serverMessage);
            return serverMessage ;
        }
        return null;
    }

}
