package org.example.retroGamesMenu.RamseyGraph;

import org.example.retroGamesMenu.RetroGamesMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class DrawingPanel extends JPanel {
    final RetroGamesMenu parentFrame;
    final static int W = 500, H = 700;
    private int numVertices;
    private double edgeProbability;
    private int[] x, y;
    BufferedImage image;
    Graphics2D graphics;
    private int currentPlayer = 1;
    final Set<Line2D> selectedLines = new HashSet<>(); // set pentru liniile selectate de jucatori
    final Set<Line2D> blackLines = new HashSet<>(); // set pentru liniile negre selectate de program
    final Set<Line2D> redLines = new HashSet<>(); // set pentru liniile selectate de jucatorul rosu
    final Set<Line2D> blueLines = new HashSet<>(); // set pentru liniile selectate de jucatorul albastru

    public DrawingPanel(RetroGamesMenu parentFrame) {
        this.parentFrame = parentFrame;
        createOffscreenImage();
        initPanel();
        createBoard();

        MouseListener listener = new CustomMouseListener();
        addMouseListener(listener);
    }

    private void initPanel() {
        setPreferredSize(new Dimension(W, H));
    }

    class CustomMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            int mouseX = e.getX();
            int mouseY = e.getY();
            Point mousePoint = new Point(mouseX, mouseY);

            double threshold = 10.0;
            for (int i = 0; i < numVertices; i++) {
                for (int j = i + 1; j < numVertices; j++) {
                    Line2D line = new Line2D.Double(x[i], y[i], x[j], y[j]);
                    double distance = line.ptSegDist(mousePoint);
                    // verific daca exista o line , daca nu este selectata si daca aceasta este neagra
                    if (distance < threshold && !isLineSelected(i, j) && isBlackLine(line)) {

                        if (currentPlayer == 1) { // daca este randul primului jucator o colorez in rosu
                            graphics.setColor(Color.RED);
                            redLines.add(line);
                        } else {// daca este randul jucatorului doi o colorez in albastru
                            graphics.setColor(Color.BLUE);
                            blueLines.add(line);
                        }
                        graphics.setStroke(new BasicStroke(5));
                        graphics.drawLine(x[i], y[i], x[j], y[j]);
                        selectLine(line); // adaug linia in setul pentru linii selectate

                        Game game = new Game(redLines, blueLines, blackLines); // verific la pasul curent daca se formeaza un triunghi
                        if (game.getWinner() != -1) {
                            int winner = game.getWinner();
                            if (winner == 0) {
                                JOptionPane.showMessageDialog(parentFrame, "Tie game!");
                            } else if (winner == 1) {
                                JOptionPane.showMessageDialog(parentFrame, "Player " + "red" + " wins!");
                            } else if (winner == 2) {
                                JOptionPane.showMessageDialog(parentFrame, "Player " + "blue" + " wins!");
                            }
                            return;
                        }
                        currentPlayer = 3 - currentPlayer; // schimb jucatorul
                        repaint();
                        return;
                    }
                }
            }
        }
    }

    private void createOffscreenImage() {
        image = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    final void createBoard() {
        numVertices = 6;
        edgeProbability = 1;

        createOffscreenImage();
        createVertices();
        drawLines();
        drawVertices();

        repaint();
    }

    private void createVertices() {
        int x0 = 500 / 2;
        int y0 = 500 / 2;
        int radius = 500 / 2 - 10;
        double alpha = 2 * Math.PI / numVertices;
        x = new int[numVertices];
        y = new int[numVertices];
        for (int i = 0; i < numVertices; i++) {
            x[i] = x0 + (int) (radius * Math.cos(alpha * i));
            y[i] = y0 + (int) (radius * Math.sin(alpha * i));
        }
    }
    // desenez liniile negre cu probabilitatea data(se genereaza un numar random intre 0.0 si 1.1 si se verifica daca
    // numarul este mai mic decat probabilitatea )si le adaug in setul de linii negre
    private void drawLines() {
        graphics.setColor(Color.BLACK);
        for (int i = 0; i < numVertices; i++) {
            for (int j = i + 1; j < numVertices; j++) {
                double random = Math.random();
                if (random <= edgeProbability) {
                    graphics.setStroke(new BasicStroke(5));
                    graphics.drawLine(x[i], y[i], x[j], y[j]);
                    Line2D blackLine = new Line2D.Double(x[i], y[i], x[j], y[j]);
                    blackLines.add(blackLine);
                }
            }
        }
    }

    private void drawVertices() {
        graphics.setColor(Color.BLACK);
        for (int i = 0; i < numVertices; i++) {
            graphics.fillOval(x[i] - 10, y[i] - 10, 20, 20);
        }
    }
    // metoda de verificare daca lina este selectata
    private boolean isLineSelected(int i, int j) {
        for (Line2D selectedLine : selectedLines) {
            if (selectedLine.getP1().equals(new Point(x[i], y[i])) && selectedLine.getP2().equals(new Point(x[j], y[j])) || selectedLine.getP1().equals(new Point(x[j], y[j])) && selectedLine.getP2().equals(new Point(x[i], y[i]))) {
                return true;
            }
        }
        return false;
    }
    // metoda de selectie a liniei
    private void selectLine(Line2D line) {
        selectedLines.add(line);
    }

    // metoda de golire a setului de linii negre pentru reset sau new game
    public void clearBlackLines() {
        blackLines.clear();
    }
    // metoda de verificare daca linia este neagra
    private boolean isBlackLine(Line2D line) {
        for (Line2D blackLine : blackLines) {
            if (line.getP1().equals(blackLine.getP1()) && line.getP2().equals(blackLine.getP2())) {
                return true;
            }
        }
        return false;
    }
    // metoda pentru reset game . Se recoloreaza toata liniile negre . Seturile pentru linii rosii ,albastre si
    // liniile selectate se reseteaza si jucatorul se seteaza inapoi pe rosu (jucatorul 1)
    public void resetLines() {
        for (Line2D line : blackLines) {
            graphics.setColor(Color.BLACK);
            graphics.draw(line);
        }
        selectedLines.clear();
        currentPlayer = 1;
        redLines.clear();
        blueLines.clear();
        repaint();
    }

    @Override
    public void update(Graphics g) {
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, this);
    }
}

