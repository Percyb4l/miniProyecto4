package com.example.battleship.view;

import com.example.battleship.model.Board;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.*;

/**
 * Cell renderer with continuous multi-cell ships (no gaps between cells).
 * Each cell draws its portion extending to cell edges for seamless connection.
 *
 * @author Battleship Team
 * @version 7.0
 */
public class CellRenderer {

    private static final int CELL_SIZE = 30;

    // Military olive green palette
    private static final Color HULL_DARK = Color.rgb(75, 85, 70);
    private static final Color HULL_MEDIUM = Color.rgb(95, 105, 85);
    private static final Color HULL_LIGHT = Color.rgb(115, 125, 100);
    private static final Color OUTLINE = Color.rgb(40, 45, 35);
    private static final Color DECK_LINES = Color.rgb(60, 70, 55);
    private static final Color ORANGE_ACCENT = Color.rgb(200, 120, 60);

    public static void renderCell(StackPane cell, Board.CellState state, boolean hideShips,
                                  Object[] shipInfo) {
        cell.getChildren().clear();

        switch (state) {
            case WATER:
                renderWater(cell);
                break;
            case SHIP:
                if (!hideShips && shipInfo != null) {
                    String type = (String) shipInfo[0];
                    int position = (int) shipInfo[1];
                    int totalSize = (int) shipInfo[2];
                    boolean isHorizontal = (boolean) shipInfo[3];

                    renderShipPart(cell, type, position, totalSize, isHorizontal);
                } else {
                    renderWater(cell);
                }
                break;
            case HIT:
                renderHit(cell, hideShips, shipInfo);
                break;
            case SUNK:
                renderSunk(cell, shipInfo);
                break;
            case MISS:
                renderMiss(cell);
                break;
        }
    }

    private static void renderWater(StackPane cell) {
        Rectangle water = new Rectangle(CELL_SIZE, CELL_SIZE);
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(100, 180, 255)),
                new Stop(0.5, Color.rgb(70, 150, 230)),
                new Stop(1, Color.rgb(50, 120, 200))
        );
        water.setFill(gradient);
        cell.getChildren().add(water);
    }

    private static void renderShipPart(StackPane cell, String type, int position,
                                       int totalSize, boolean isHorizontal) {
        if (!isHorizontal) {
            cell.setRotate(90);
        }

        switch (type) {
            case "Carrier":
                renderCarrierPart(cell, position, totalSize);
                break;
            case "Submarine":
                renderSubmarinePart(cell, position, totalSize);
                break;
            case "Destroyer":
                renderDestroyerPart(cell, position, totalSize);
                break;
            case "Frigate":
                renderFrigatePart(cell);
                break;
        }
    }

    /**
     * CARRIER (4 cells) - Continuous aircraft carrier.
     */
    private static void renderCarrierPart(StackPane cell, int position, int totalSize) {
        if (position == 0) {
            // BOW - Pointed front
            Polygon bow = new Polygon(
                    -15, 0,          // Sharp point
                    -15, -11,
                    15, -11,         // Extends to next cell
                    15, 11,
                    -15, 11
            );
            bow.setFill(HULL_MEDIUM);
            bow.setStroke(OUTLINE);
            bow.setStrokeWidth(2);

            // Orange stripe
            Polygon stripe = new Polygon(
                    -14, -10,
                    14, -10,
                    14, 10,
                    -14, 10
            );
            stripe.setFill(ORANGE_ACCENT);
            stripe.setStroke(OUTLINE);
            stripe.setStrokeWidth(1);

            cell.getChildren().addAll(bow, stripe);

        } else if (position == 1) {
            // SECTION 1 - Flight deck with first jet
            Rectangle deck = new Rectangle(CELL_SIZE, 22);
            deck.setFill(HULL_LIGHT);
            deck.setStroke(OUTLINE);
            deck.setStrokeWidth(2);
            cell.getChildren().add(deck);

            // Deck lines (continuous)
            for (int i = -10; i <= 10; i += 4) {
                Line line = new Line(-15, i, 15, i);
                line.setStroke(DECK_LINES);
                line.setStrokeWidth(0.6);
                cell.getChildren().add(line);
            }

            // First jet
            renderJet(cell, -8, -6);

            // Side extensions
            Rectangle sideL1 = new Rectangle(5, 6);
            sideL1.setFill(HULL_DARK);
            sideL1.setStroke(OUTLINE);
            sideL1.setTranslateX(-12);
            sideL1.setTranslateY(-10);

            Rectangle sideL2 = new Rectangle(5, 6);
            sideL2.setFill(HULL_DARK);
            sideL2.setStroke(OUTLINE);
            sideL2.setTranslateX(-12);
            sideL2.setTranslateY(10);

            cell.getChildren().addAll(sideL1, sideL2);

        } else if (position == 2) {
            // SECTION 2 - Flight deck with second jet
            Rectangle deck = new Rectangle(CELL_SIZE, 22);
            deck.setFill(HULL_LIGHT);
            deck.setStroke(OUTLINE);
            deck.setStrokeWidth(2);
            cell.getChildren().add(deck);

            // Deck lines
            for (int i = -10; i <= 10; i += 4) {
                Line line = new Line(-15, i, 15, i);
                line.setStroke(DECK_LINES);
                line.setStrokeWidth(0.6);
                cell.getChildren().add(line);
            }

            // Second jet
            renderJet(cell, -8, 5);

            // Center line markings
            Line centerLine = new Line(-15, 0, 15, 0);
            centerLine.setStroke(Color.YELLOW);
            centerLine.setStrokeWidth(1.5);
            centerLine.getStrokeDashArray().addAll(4d, 4d);
            cell.getChildren().add(centerLine);

        } else {
            // STERN - Control tower
            Rectangle deck = new Rectangle(CELL_SIZE, 22);
            deck.setFill(HULL_LIGHT);
            deck.setStroke(OUTLINE);
            deck.setStrokeWidth(2);
            cell.getChildren().add(deck);

            // Large tower
            Rectangle tower = new Rectangle(14, 20);
            tower.setFill(HULL_DARK);
            tower.setStroke(OUTLINE);
            tower.setStrokeWidth(1.5);
            tower.setTranslateX(3);

            // Windows
            Rectangle win1 = new Rectangle(3, 2.5);
            win1.setFill(Color.rgb(50, 60, 70));
            win1.setTranslateX(1);
            win1.setTranslateY(-5);

            Rectangle win2 = new Rectangle(3, 2.5);
            win2.setFill(Color.rgb(50, 60, 70));
            win2.setTranslateX(5);
            win2.setTranslateY(-5);

            // Radar domes
            Circle radar1 = new Circle(2.5, Color.rgb(130, 130, 140));
            radar1.setStroke(OUTLINE);
            radar1.setTranslateX(1);
            radar1.setTranslateY(-12);

            Circle radar2 = new Circle(2.5, Color.rgb(130, 130, 140));
            radar2.setStroke(OUTLINE);
            radar2.setTranslateX(5);
            radar2.setTranslateY(-12);

            // Antenna
            Line antenna = new Line(3, -15, 3, -18);
            antenna.setStroke(OUTLINE);
            antenna.setStrokeWidth(1.5);

            // Orange detail
            Rectangle orangeBox = new Rectangle(3, 5);
            orangeBox.setFill(ORANGE_ACCENT);
            orangeBox.setTranslateX(9);
            orangeBox.setTranslateY(7);

            cell.getChildren().addAll(tower, win1, win2, radar1, radar2, antenna, orangeBox);
        }
    }

    private static void renderJet(StackPane cell, double x, double y) {
        // Jet fuselage
        Polygon jet = new Polygon(
                x, y,
                x + 4, y - 2,
                x + 7, y - 2,
                x + 8, y,
                x + 7, y + 2,
                x + 4, y + 2
        );
        jet.setFill(Color.rgb(90, 90, 100));
        jet.setStroke(OUTLINE);
        jet.setStrokeWidth(1);

        // Wings
        Polygon wings = new Polygon(
                x + 3, y - 4,
                x + 5, y - 4,
                x + 5, y + 4,
                x + 3, y + 4
        );
        wings.setFill(Color.rgb(80, 80, 90));
        wings.setStroke(OUTLINE);
        wings.setStrokeWidth(0.8);

        cell.getChildren().addAll(wings, jet);
    }

    /**
     * SUBMARINE (3 cells) - Continuous submarine.
     */
    private static void renderSubmarinePart(StackPane cell, int position, int totalSize) {
        if (position == 0) {
            // BOW - Rounded nose
            Path nose = new Path();
            nose.getElements().addAll(
                    new MoveTo(-15, 0),
                    new QuadCurveTo(-15, -8, -5, -8),
                    new LineTo(15, -8),
                    new LineTo(15, 8),
                    new LineTo(-5, 8),
                    new QuadCurveTo(-15, 8, -15, 0),
                    new ClosePath()
            );
            nose.setFill(HULL_DARK);
            nose.setStroke(OUTLINE);
            nose.setStrokeWidth(2);

            // Orange porthole
            Circle porthole = new Circle(3, ORANGE_ACCENT);
            porthole.setStroke(OUTLINE);
            porthole.setStrokeWidth(1.2);
            porthole.setTranslateX(-8);
            porthole.setTranslateY(-6);

            // Center line
            Line centerLine = new Line(-12, 0, 15, 0);
            centerLine.setStroke(DECK_LINES);
            centerLine.setStrokeWidth(1.8);

            cell.getChildren().addAll(nose, centerLine, porthole);

        } else if (position == 1) {
            // MIDDLE - Conning tower
            Rectangle body = new Rectangle(CELL_SIZE, 16);
            body.setFill(HULL_DARK);
            body.setStroke(OUTLINE);
            body.setStrokeWidth(2);
            cell.getChildren().add(body);

            // Center line continuous
            Line centerLine = new Line(-15, 0, 15, 0);
            centerLine.setStroke(DECK_LINES);
            centerLine.setStrokeWidth(1.8);
            cell.getChildren().add(centerLine);

            // Conning tower
            Rectangle tower = new Rectangle(10, 12);
            tower.setFill(HULL_MEDIUM);
            tower.setStroke(OUTLINE);
            tower.setStrokeWidth(1.5);
            tower.setArcWidth(3);
            tower.setArcHeight(3);
            tower.setTranslateY(-10);

            // Hatch
            Circle hatch = new Circle(2.5, HULL_DARK);
            hatch.setStroke(OUTLINE);
            hatch.setStrokeWidth(1);
            hatch.setTranslateY(-10);

            // Diving planes (wings)
            Rectangle wingL = new Rectangle(8, 4);
            wingL.setFill(HULL_MEDIUM);
            wingL.setStroke(OUTLINE);
            wingL.setStrokeWidth(1.2);
            wingL.setTranslateX(-12);

            Rectangle wingR = new Rectangle(8, 4);
            wingR.setFill(HULL_MEDIUM);
            wingR.setStroke(OUTLINE);
            wingR.setStrokeWidth(1.2);
            wingR.setTranslateX(12);

            cell.getChildren().addAll(tower, hatch, wingL, wingR);

        } else {
            // STERN - Propeller section
            Rectangle body = new Rectangle(CELL_SIZE, 16);
            body.setFill(HULL_DARK);
            body.setStroke(OUTLINE);
            body.setStrokeWidth(2);
            cell.getChildren().add(body);

            // Center line continuous
            Line centerLine = new Line(-15, 0, 15, 0);
            centerLine.setStroke(DECK_LINES);
            centerLine.setStrokeWidth(1.8);
            cell.getChildren().add(centerLine);

            // Propeller housings (5 circles)
            for (int i = 0; i < 5; i++) {
                Circle circle = new Circle(3, HULL_MEDIUM);
                circle.setStroke(OUTLINE);
                circle.setStrokeWidth(1.2);
                circle.setTranslateX(-4);
                circle.setTranslateY(-7 + i * 3.5);
                cell.getChildren().add(circle);
            }

            // Rear stabilizers
            Rectangle stabL = new Rectangle(6, 3);
            stabL.setFill(HULL_MEDIUM);
            stabL.setStroke(OUTLINE);
            stabL.setTranslateX(-10);
            stabL.setTranslateY(-10);

            Rectangle stabR = new Rectangle(6, 3);
            stabR.setFill(HULL_MEDIUM);
            stabR.setStroke(OUTLINE);
            stabR.setTranslateX(-10);
            stabR.setTranslateY(10);

            // Propeller
            Ellipse prop = new Ellipse(4, 6);
            prop.setFill(Color.rgb(55, 65, 60));
            prop.setStroke(OUTLINE);
            prop.setStrokeWidth(1.5);
            prop.setTranslateX(10);

            cell.getChildren().addAll(stabL, stabR, prop);
        }
    }

    /**
     * DESTROYER (2 cells) - Continuous destroyer.
     */
    private static void renderDestroyerPart(StackPane cell, int position, int totalSize) {
        if (position == 0) {
            // BOW - Front section
            Rectangle hull = new Rectangle(CELL_SIZE, 18);
            hull.setFill(HULL_MEDIUM);
            hull.setStroke(OUTLINE);
            hull.setStrokeWidth(2);
            cell.getChildren().add(hull);

            // Front gun turret (top)
            Circle gunBase = new Circle(5, HULL_DARK);
            gunBase.setStroke(OUTLINE);
            gunBase.setStrokeWidth(1.5);
            gunBase.setTranslateY(-10);

            Line gunBarrel = new Line(-5, -10, -12, -10);
            gunBarrel.setStroke(OUTLINE);
            gunBarrel.setStrokeWidth(2.5);

            // Bridge structure
            Polygon bridge = new Polygon(
                    -6, -3,
                    -6, 3,
                    -3, 5,
                    3, 5,
                    6, 3,
                    6, -3,
                    3, -5,
                    -3, -5
            );
            bridge.setFill(HULL_LIGHT);
            bridge.setStroke(OUTLINE);
            bridge.setStrokeWidth(1.5);

            // Antenna cross
            Line antV = new Line(0, -8, 0, -3);
            antV.setStroke(OUTLINE);
            antV.setStrokeWidth(1.5);

            Line antH = new Line(-3, -5.5, 3, -5.5);
            antH.setStroke(OUTLINE);
            antH.setStrokeWidth(1.5);

            cell.getChildren().addAll(gunBase, gunBarrel, bridge, antV, antH);

        } else {
            // STERN - Rear section
            Rectangle hull = new Rectangle(CELL_SIZE, 18);
            hull.setFill(HULL_MEDIUM);
            hull.setStroke(OUTLINE);
            hull.setStrokeWidth(2);
            cell.getChildren().add(hull);

            // Rear structure boxes
            Rectangle box1 = new Rectangle(8, 6);
            box1.setFill(HULL_DARK);
            box1.setStroke(OUTLINE);
            box1.setStrokeWidth(1.3);
            box1.setTranslateY(-7);

            Rectangle box2 = new Rectangle(9, 5);
            box2.setFill(HULL_LIGHT);
            box2.setStroke(OUTLINE);
            box2.setStrokeWidth(1.3);
            box2.setTranslateY(-1);

            // Bottom hatches
            Circle hatch1 = new Circle(2.5, HULL_DARK);
            hatch1.setStroke(OUTLINE);
            hatch1.setStrokeWidth(1.2);
            hatch1.setTranslateX(-5);
            hatch1.setTranslateY(6);

            Circle hatch2 = new Circle(2, HULL_DARK);
            hatch2.setStroke(OUTLINE);
            hatch2.setStrokeWidth(1.2);
            hatch2.setTranslateX(5);
            hatch2.setTranslateY(6);

            // Rear gun turret
            Circle rearGun = new Circle(4, HULL_DARK);
            rearGun.setStroke(OUTLINE);
            rearGun.setStrokeWidth(1.5);
            rearGun.setTranslateY(12);

            Line rearBarrel = new Line(0, 12, 0, 17);
            rearBarrel.setStroke(OUTLINE);
            rearBarrel.setStrokeWidth(2.5);

            cell.getChildren().addAll(box1, box2, hatch1, hatch2, rearGun, rearBarrel);
        }
    }

    /**
     * FRIGATE (1 cell) - Small patrol boat.
     */
    private static void renderFrigatePart(StackPane cell) {
        // Hull
        Ellipse hull = new Ellipse(11, 8);
        hull.setFill(HULL_MEDIUM);
        hull.setStroke(OUTLINE);
        hull.setStrokeWidth(2);
        cell.getChildren().add(hull);

        // Cabin
        Rectangle cabin = new Rectangle(8, 8);
        cabin.setFill(HULL_LIGHT);
        cabin.setStroke(OUTLINE);
        cabin.setStrokeWidth(1.5);
        cabin.setArcWidth(3);
        cabin.setArcHeight(3);

        // Hatch
        Circle hatch = new Circle(2, HULL_DARK);
        hatch.setStroke(OUTLINE);
        hatch.setStrokeWidth(1);

        // Wings/stabilizers
        Rectangle wingL = new Rectangle(7, 3);
        wingL.setFill(HULL_MEDIUM);
        wingL.setStroke(OUTLINE);
        wingL.setStrokeWidth(1.2);
        wingL.setTranslateX(-11);

        Rectangle wingR = new Rectangle(7, 3);
        wingR.setFill(HULL_MEDIUM);
        wingR.setStroke(OUTLINE);
        wingR.setStrokeWidth(1.2);
        wingR.setTranslateX(11);

        // Antenna
        Line antenna = new Line(0, -6, 0, -10);
        antenna.setStroke(OUTLINE);
        antenna.setStrokeWidth(1.2);

        Circle antennaTop = new Circle(1.5, HULL_DARK);
        antennaTop.setStroke(OUTLINE);
        antennaTop.setTranslateY(-10);

        cell.getChildren().addAll(cabin, hatch, wingL, wingR, antenna, antennaTop);
    }

    private static void renderHit(StackPane cell, boolean hideShips, Object[] shipInfo) {
        if (!hideShips && shipInfo != null) {
            String type = (String) shipInfo[0];
            int position = (int) shipInfo[1];
            int totalSize = (int) shipInfo[2];
            boolean isHorizontal = (boolean) shipInfo[3];

            renderShipPart(cell, type, position, totalSize, isHorizontal);

            Rectangle damage = new Rectangle(CELL_SIZE, CELL_SIZE);
            damage.setFill(Color.rgb(0, 0, 0, 0.3));
            cell.getChildren().add(damage);
        }

        Circle explosion = new Circle(11);
        RadialGradient fireGradient = new RadialGradient(
                0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.YELLOW),
                new Stop(0.4, Color.ORANGE),
                new Stop(1, Color.DARKRED)
        );
        explosion.setFill(fireGradient);
        cell.getChildren().add(explosion);

        Circle smoke = new Circle(7, Color.rgb(50, 50, 50, 0.7));
        smoke.setTranslateY(-9);
        cell.getChildren().add(smoke);
    }

    private static void renderSunk(StackPane cell, Object[] shipInfo) {
        Rectangle water = new Rectangle(CELL_SIZE, CELL_SIZE);
        water.setFill(Color.rgb(30, 50, 70));
        cell.getChildren().add(water);

        Polygon wreck = new Polygon(-8, -5, 8, -3, 6, 7, -10, 5);
        wreck.setFill(Color.rgb(45, 50, 55));
        wreck.setStroke(Color.rgb(25, 30, 35));
        wreck.setStrokeWidth(1.5);
        cell.getChildren().add(wreck);

        Line x1 = new Line(-11, -11, 11, 11);
        x1.setStroke(Color.DARKRED);
        x1.setStrokeWidth(3.5);
        x1.setStrokeLineCap(StrokeLineCap.ROUND);

        Line x2 = new Line(11, -11, -11, 11);
        x2.setStroke(Color.DARKRED);
        x2.setStrokeWidth(3.5);
        x2.setStrokeLineCap(StrokeLineCap.ROUND);

        cell.getChildren().addAll(x1, x2);
    }

    private static void renderMiss(StackPane cell) {
        Circle splash = new Circle(11, Color.rgb(200, 220, 255, 0.6));
        splash.setStroke(Color.rgb(100, 150, 200));
        splash.setStrokeWidth(2.5);
        cell.getChildren().add(splash);

        Line x1 = new Line(-9, -9, 9, 9);
        x1.setStroke(Color.WHITE);
        x1.setStrokeWidth(3.5);
        x1.setStrokeLineCap(StrokeLineCap.ROUND);

        Line x2 = new Line(9, -9, -9, 9);
        x2.setStroke(Color.WHITE);
        x2.setStrokeWidth(3.5);
        x2.setStrokeLineCap(StrokeLineCap.ROUND);

        cell.getChildren().addAll(x1, x2);
    }
}