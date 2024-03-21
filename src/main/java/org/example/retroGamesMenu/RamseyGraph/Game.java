package org.example.retroGamesMenu.RamseyGraph;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Set;

public class Game {
    private final Set<Line2D> redLines;
    private final Set<Line2D> blueLines;
    private final Set<Line2D> blackLines;

    public Game(Set<Line2D> redLines, Set<Line2D> blueLines, Set<Line2D> blackLines) {
        this.redLines = redLines;
        this.blueLines = blueLines;
        this.blackLines = blackLines;
    }

    public int getWinner() {

        if (hasTriangle(redLines)) {
            return 1;
        } else if (hasTriangle(blueLines)) {
            return 2;
        } else if (redLines.size() + blueLines.size() == blackLines.size()) {
            return 0;
        } else {
            return -1;
        }
    }

    //generez toate aranjamentele a catre 3 linii din toate combinarile a cate trei linii
    private boolean hasTriangle(Set<Line2D> lines) {
        for (Line2D line1 : lines) {
            for (Line2D line2 : lines) {
                if (line1 == line2) {
                    continue;
                }
                for (Line2D line3 : lines) {
                    if (line1 == line3 || line2 == line3) {
                        continue;
                    }
                    if (formsTriangle(line1, line2, line3)) {
                        return true;
                    }
                    if (formsTriangle(line1, line3, line2)) {
                        return true;
                    }
                    if (formsTriangle(line2, line1, line3)) {
                        return true;
                    }
                    if (formsTriangle(line2, line3, line1)) {
                        return true;
                    }
                    if (formsTriangle(line3, line1, line2)) {
                        return true;
                    }
                    if (formsTriangle(line3, line2, line1)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //verific toate combinatiile posibile de intersectie end-end start-end start-start

    private boolean formsTriangle(Line2D line1, Line2D line2, Line2D line3) {
        double x1 = line1.getX1();
        double y1 = line1.getY1();
        double x2 = line1.getX2();
        double y2 = line1.getY2();

        double x3 = line2.getX1();
        double y3 = line2.getY1();
        double x4 = line2.getX2();
        double y4 = line2.getY2();

        double x5 = line3.getX1();
        double y5 = line3.getY1();
        double x6 = line3.getX2();
        double y6 = line3.getY2();

        if (x2 == x3 && y2 == y3) {
            if (x4 == x5 && y4 == y5) {
                if (x6 == x1 && y6 == y1) {
                    return true;
                }
            }
        }

        if (x2 == x4 && y2 == y4) {
            if (x4 == x6 && y4 == y6) {
                if (x6 == x1 && y6 == y1) {
                    return true;
                }
            }
        }

        if (x1 == x4 && y1 == y4) {
            if (x4 == x5 && y4 == y5) {
                if (x6 == x2 && y6 == y2) {
                    return true;
                }
            }
        }

        if (x1 == x3 && y1 == y3) {
            if (x4 == x5 && y4 == y5) {
                if (x6 == x2 && y6 == y2) {
                    return true;
                }
            }
        }

        return false;
    }

}
