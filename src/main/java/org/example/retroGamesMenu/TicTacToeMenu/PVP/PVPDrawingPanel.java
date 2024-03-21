package org.example.retroGamesMenu.TicTacToeMenu.PVP;

import org.example.RetroGamesClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PVPDrawingPanel extends JPanel {
    private final int[][] grid;


    public PVPDrawingPanel(int[][] grid ,PVPWindow pvpWindow) {
        this.grid=grid;
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    String lastMessage = handleServerMessage();
                    System.out.println(lastMessage);
                    if (lastMessage != null) {
                        return;
                    }
                    //Determin coordonatele click-ului

                    int x = e.getX();
                    int y = e.getY();

                    if (x >= 95 && x <= 395 && y >= 95 && y <= 395) {
                        //Determin pozitia corespunzatoare in matrice a ceclulei pe pare s-a facut click-ul
                        int row = (y - 95) / 100;
                        int col = (x - 95) / 100;

                        if (grid[row][col] == 0) {
                            //Determin jucatorul curent si updataz matricea cu numarul corezpunzator jucatorului
                            int currentPlayer = countCells() % 2 + 1;
                            grid[row][col] = currentPlayer;

                            //Trimit pozitia serverului
                            RetroGamesClient.getInstance().sendMessageToServer(row + " " + col);

                            //Actualizez imaginea cu starea crenta a matricii
                            pvpWindow.updateGrid();
                        }
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            //Desenez careul

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
     private int countCells() {
         int count = 0;
         for (int i = 0; i < 3; i++) {
             for (int j = 0; j < 3; j++) {
                 if (grid[i][j] != 0) {
                     count++;
                 }
             }
         }
         return count;
     }

    private String handleServerMessage() {
        String serverMessage;
        serverMessage = RetroGamesClient.getInstance().getLastReceivedMessage();

        // Procesez mesajul de la server
        if (serverMessage != null && (serverMessage.startsWith("Player") || serverMessage.contains("draw!"))) {
            JOptionPane.showMessageDialog(this,serverMessage);
            return serverMessage ;
        }
        return null;
    }
 }

