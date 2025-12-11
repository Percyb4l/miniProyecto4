package com.example.battleship.view;

import com.example.battleship.model.Board;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.*;

/**
 * Cell renderer with ships that stay STRICTLY within cell boundaries.
 * NO elements overflow beyond 30x30px, preventing grid distortion.
 *
 * @author Battleship Team
 * @version 10.0 - NO GRID DISTORTION
 */
public class CellRenderer {

    private static final int CELL_SIZE = 30;
    // Safe rendering area: leave 1px margin on each side
    private static final int SAFE_SIZE = 28;
    private static final int MARGIN = 1;

    // Realistic military colors with 3D shading
    private static final Color HULL_TOP = Color.rgb(110, 120, 95);
    private static final Color HULL_SIDE = Color.rgb(85, 95, 75);
    private static final Color HULL_SHADOW = Color.rgb(60, 70, 55);
    private static final Color DECK = Color.rgb(100, 110, 85);
    private static final Color OUTLINE = Color.rgb(35, 40, 30);
    private static final Color ORANGE_LIGHT = Color.rgb(220, 140, 70);
    private static final Color ORANGE_DARK = Color.rgb(180, 100, 50);

    public static void renderCell(StackPane cell, Board.CellState state, boolean hideShips,
                                  Object[] shipInfo) {
        cell.getChildren().clear();

        // Ensure cell stays at exactly 30x30
        cell.setMinSize(CELL_SIZE, CELL_SIZE);
        cell.setMaxSize(CELL_SIZE, CELL_SIZE);
        cell.setPrefSize(CELL_SIZE, CELL_SIZE);

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

                    renderWater(cell);
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
        // Water fills EXACTLY the cell, no overflow
        Rectangle water = new Rectangle(CELL_SIZE, CELL_SIZE);

        LinearGradient oceanGradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(30, 80, 120)),
                new Stop(0.3, Color.rgb(40, 100, 140)),
                new Stop(0.6, Color.rgb(50, 120, 160)),
                new Stop(1, Color.rgb(65, 140, 180))
        );
        water.setFill(oceanGradient);

        InnerShadow depth = new InnerShadow();
        depth.setColor(Color.rgb(20, 60, 90, 0.4));
        depth.setRadius(4);
        water.setEffect(depth);

        cell.getChildren().add(water);

        // Waves stay within bounds
        Path wave = new Path();
        wave.getElements().addAll(
                new MoveTo(2, CELL_SIZE * 0.3),
                new QuadCurveTo(CELL_SIZE * 0.3, CELL_SIZE * 0.25, CELL_SIZE * 0.5, CELL_SIZE * 0.3),
                new QuadCurveTo(CELL_SIZE * 0.7, CELL_SIZE * 0.35, CELL_SIZE - 2, CELL_SIZE * 0.3)
        );
        wave.setStroke(Color.rgb(200, 230, 255, 0.2));
        wave.setStrokeWidth(1);
        cell.getChildren().add(wave);
    }

    private static void renderShipPart(StackPane cell, String type, int position,
                                       int totalSize, boolean isHorizontal) {
        if (!isHorizontal) {
            cell.setRotate(90);
        }

        switch (type) {
            case "Carrier":
                renderCarrier(cell, position, totalSize);
                break;
            case "Submarine":
                renderSubmarine(cell, position, totalSize);
                break;
            case "Destroyer":
                renderDestroyer(cell, position, totalSize);
                break;
            case "Frigate":
                renderFrigate(cell);
                break;
        }
    }

    /**
     * Carrier - All elements stay within [-14, 14] range (28px)
     */
    private static void renderCarrier(StackPane cell, int position, int totalSize) {
        // Hull base - stays within bounds
        Rectangle hullBottom = new Rectangle(SAFE_SIZE, 8);
        hullBottom.setFill(HULL_SHADOW);
        hullBottom.setStroke(null);
        hullBottom.setTranslateY(6);
        cell.getChildren().add(hullBottom);

        Rectangle hullTop = new Rectangle(SAFE_SIZE, 12);
        hullTop.setFill(HULL_TOP);
        hullTop.setStroke(null);
        hullTop.setTranslateY(-1);
        cell.getChildren().add(hullTop);

        if (position == 0) {
            // BOW - Contained within cell
            Polygon bow = new Polygon(
                    -14, 0,    // Left edge of safe area
                    -13, -5,   // Tip
                    -13, 5,    // Bottom tip
                    -14, 0     // Close
            );
            bow.setFill(HULL_SIDE);
            bow.setStroke(OUTLINE);
            bow.setStrokeWidth(1);
            cell.getChildren().add(bow);

            // Orange stripe - contained
            Rectangle stripe = new Rectangle(16, 3);
            stripe.setFill(ORANGE_LIGHT);
            stripe.setStroke(null);
            stripe.setTranslateX(-6);
            stripe.setTranslateY(-6);
            cell.getChildren().add(stripe);

        } else if (position == 1) {
            // DECK 1 - Lines stay within bounds
            for (int i = -8; i <= 6; i += 4) {
                Line deckLine = new Line(-13, i, 13, i);
                deckLine.setStroke(Color.rgb(80, 90, 70, 0.4));
                deckLine.setStrokeWidth(0.5);
                cell.getChildren().add(deckLine);
            }
            renderJet(cell, -6, -3);

        } else if (position == 2) {
            // DECK 2
            for (int i = -8; i <= 6; i += 4) {
                Line deckLine = new Line(-13, i, 13, i);
                deckLine.setStroke(Color.rgb(80, 90, 70, 0.4));
                deckLine.setStrokeWidth(0.5);
                cell.getChildren().add(deckLine);
            }
            renderJet(cell, -6, 2);

        } else if (position == 3) {
            // TOWER - All within bounds
            Rectangle tower = new Rectangle(8, 16);
            tower.setFill(HULL_SIDE);
            tower.setStroke(OUTLINE);
            tower.setStrokeWidth(0.8);
            tower.setTranslateX(4);
            tower.setTranslateY(-1);
            cell.getChildren().add(tower);

            Rectangle towerTop = new Rectangle(9, 2);
            towerTop.setFill(HULL_TOP);
            towerTop.setStroke(OUTLINE);
            towerTop.setStrokeWidth(0.5);
            towerTop.setTranslateX(4);
            towerTop.setTranslateY(-10);
            cell.getChildren().add(towerTop);

            Circle radar = new Circle(2);
            radar.setFill(Color.rgb(120, 120, 130));
            radar.setStroke(OUTLINE);
            radar.setStrokeWidth(0.5);
            radar.setTranslateX(4);
            radar.setTranslateY(-12);
            cell.getChildren().add(radar);
        }
    }

    /**
     * Submarine - All elements contained
     */
    private static void renderSubmarine(StackPane cell, int position, int totalSize) {
        Rectangle hullBottom = new Rectangle(SAFE_SIZE, 5);
        hullBottom.setFill(HULL_SHADOW);
        hullBottom.setStroke(null);
        hullBottom.setTranslateY(6);
        cell.getChildren().add(hullBottom);

        Ellipse hull = new Ellipse(SAFE_SIZE / 2.0, 6);
        hull.setFill(HULL_SIDE);
        hull.setStroke(null);
        hull.setTranslateY(0);
        cell.getChildren().add(hull);

        if (position == 0) {
            // PORTHOLE - Contained
            Circle porthole = new Circle(3);
            porthole.setStroke(OUTLINE);
            porthole.setStrokeWidth(1);
            porthole.setTranslateX(-8);
            porthole.setTranslateY(-2);

            RadialGradient glow = new RadialGradient(
                    0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                    new Stop(0, ORANGE_LIGHT),
                    new Stop(1, ORANGE_DARK)
            );
            porthole.setFill(glow);
            cell.getChildren().add(porthole);

        } else if (position == 1) {
            // TOWER - Contained
            Rectangle tower = new Rectangle(7, 11);
            tower.setFill(HULL_SIDE);
            tower.setStroke(OUTLINE);
            tower.setStrokeWidth(1);
            tower.setArcWidth(2);
            tower.setArcHeight(2);
            tower.setTranslateY(-9);
            cell.getChildren().add(tower);

            Rectangle towerTop = new Rectangle(8, 2);
            towerTop.setFill(HULL_TOP);
            towerTop.setStroke(OUTLINE);
            towerTop.setStrokeWidth(0.5);
            towerTop.setTranslateY(-15);
            cell.getChildren().add(towerTop);

        } else if (position == 2) {
            // PROPELLER - Contained at x=10 (within 14px limit)
            Ellipse propeller = new Ellipse(3, 5);
            propeller.setFill(Color.rgb(55, 65, 60));
            propeller.setStroke(OUTLINE);
            propeller.setStrokeWidth(1);
            propeller.setTranslateX(10);
            cell.getChildren().add(propeller);

            Line propBlade = new Line(10, -5, 10, 5);
            propBlade.setStroke(Color.rgb(70, 80, 75));
            propBlade.setStrokeWidth(2);
            cell.getChildren().add(propBlade);
        }
    }

    /**
     * Destroyer - All contained
     */
    private static void renderDestroyer(StackPane cell, int position, int totalSize) {
        Rectangle hullBottom = new Rectangle(SAFE_SIZE, 4);
        hullBottom.setFill(HULL_SHADOW);
        hullBottom.setStroke(null);
        hullBottom.setTranslateY(8);
        cell.getChildren().add(hullBottom);

        Rectangle hull = new Rectangle(SAFE_SIZE, 14);
        hull.setFill(HULL_SIDE);
        hull.setStroke(null);
        hull.setTranslateY(0);
        cell.getChildren().add(hull);

        Rectangle deck = new Rectangle(SAFE_SIZE, 3);
        deck.setFill(HULL_TOP);
        deck.setStroke(null);
        deck.setTranslateY(-8);
        cell.getChildren().add(deck);

        if (position == 0) {
            // GUN - Barrel stays within bounds
            Circle gunBase = new Circle(4);
            gunBase.setFill(HULL_TOP);
            gunBase.setStroke(OUTLINE);
            gunBase.setStrokeWidth(1);
            gunBase.setTranslateY(-9);
            cell.getChildren().add(gunBase);

            // Barrel ends at -13 (within bounds)
            Line barrel = new Line(-4, -9, -13, -9);
            barrel.setStroke(OUTLINE);
            barrel.setStrokeWidth(2.5);
            cell.getChildren().add(barrel);

        } else if (position == 1) {
            // SUPERSTRUCTURE - Contained
            Rectangle superstructure = new Rectangle(9, 7);
            superstructure.setFill(HULL_SIDE);
            superstructure.setStroke(OUTLINE);
            superstructure.setStrokeWidth(1);
            superstructure.setTranslateY(-5);
            cell.getChildren().add(superstructure);

            Rectangle superTop = new Rectangle(10, 2);
            superTop.setFill(HULL_TOP);
            superTop.setStroke(OUTLINE);
            superTop.setStrokeWidth(0.5);
            superTop.setTranslateY(-10);
            cell.getChildren().add(superTop);
        }
    }

    /**
     * Frigate - Compact, stays within bounds
     */
    private static void renderFrigate(StackPane cell) {
        Ellipse bottom = new Ellipse(10, 5);
        bottom.setFill(HULL_SHADOW);
        bottom.setTranslateY(3);
        cell.getChildren().add(bottom);

        Ellipse hull = new Ellipse(10, 6);
        hull.setFill(HULL_SIDE);
        hull.setStroke(OUTLINE);
        hull.setStrokeWidth(1.2);
        cell.getChildren().add(hull);

        Ellipse top = new Ellipse(8, 3);
        top.setFill(HULL_TOP);
        top.setStroke(OUTLINE);
        top.setStrokeWidth(0.8);
        top.setTranslateY(-5);
        cell.getChildren().add(top);

        Rectangle cabin = new Rectangle(7, 6);
        cabin.setFill(HULL_SIDE);
        cabin.setStroke(OUTLINE);
        cabin.setStrokeWidth(1);
        cell.getChildren().add(cabin);

        Rectangle cabinTop = new Rectangle(8, 2);
        cabinTop.setFill(HULL_TOP);
        cabinTop.setStroke(OUTLINE);
        cabinTop.setStrokeWidth(0.8);
        cabinTop.setTranslateY(-5);
        cell.getChildren().add(cabinTop);
    }

    /**
     * Jet - Small and contained
     */
    private static void renderJet(StackPane cell, double x, double y) {
        double scale = 0.4;

        Polygon jet = new Polygon(
                x, y,
                x + 7 * scale, y - 2 * scale,
                x + 10 * scale, y - 2 * scale,
                x + 12 * scale, y,
                x + 10 * scale, y + 2 * scale,
                x + 7 * scale, y + 2 * scale
        );
        jet.setFill(Color.rgb(80, 80, 90));
        jet.setStroke(OUTLINE);
        jet.setStrokeWidth(0.5);
        cell.getChildren().add(jet);

        Polygon wings = new Polygon(
                x + 5 * scale, y - 4 * scale,
                x + 7 * scale, y - 4 * scale,
                x + 7 * scale, y + 4 * scale,
                x + 5 * scale, y + 4 * scale
        );
        wings.setFill(Color.rgb(70, 70, 80));
        wings.setStroke(OUTLINE);
        wings.setStrokeWidth(0.4);
        cell.getChildren().add(wings);
    }

    private static void renderHit(StackPane cell, boolean hideShips, Object[] shipInfo) {
        renderWater(cell);

        if (!hideShips && shipInfo != null) {
            String type = (String) shipInfo[0];
            int position = (int) shipInfo[1];
            int totalSize = (int) shipInfo[2];
            boolean isHorizontal = (boolean) shipInfo[3];

            renderShipPart(cell, type, position, totalSize, isHorizontal);

            Rectangle damage = new Rectangle(CELL_SIZE, CELL_SIZE);
            damage.setFill(Color.rgb(0, 0, 0, 0.4));
            cell.getChildren().add(damage);
        }

        // Explosion stays within bounds
        Circle explosion = new Circle(11);
        RadialGradient fire = new RadialGradient(
                0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.YELLOW),
                new Stop(0.4, Color.ORANGE),
                new Stop(1, Color.DARKRED)
        );
        explosion.setFill(fire);

        DropShadow explosionShadow = new DropShadow();
        explosionShadow.setRadius(4);
        explosionShadow.setColor(Color.rgb(255, 100, 0, 0.6));
        explosion.setEffect(explosionShadow);

        cell.getChildren().add(explosion);
    }

    private static void renderSunk(StackPane cell, Object[] shipInfo) {
        renderWater(cell);

        // Wreck stays within bounds
        Polygon wreck = new Polygon(-7, -3, 7, -1, 5, 4, -9, 3);
        wreck.setFill(Color.rgb(40, 45, 50));
        wreck.setStroke(Color.rgb(20, 25, 30));
        wreck.setStrokeWidth(1.5);
        cell.getChildren().add(wreck);

        // X marks stay within bounds
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
        renderWater(cell);

        // Splash stays within bounds
        Circle splash = new Circle(11, Color.rgb(220, 240, 255, 0.7));
        splash.setStroke(Color.rgb(180, 220, 250));
        splash.setStrokeWidth(2);
        cell.getChildren().add(splash);

        // X marks stay within bounds
        Line x1 = new Line(-9, -9, 9, 9);
        x1.setStroke(Color.WHITE);
        x1.setStrokeWidth(3.5);
        x1.setStrokeLineCap(StrokeLineCap.ROUND);

        Line x2 = new Line(9, -9, -9, 9);
        x2.setStroke(Color.WHITE);
        x2.setStrokeWidth(3.5);
        x2.setStrokeLineCap(StrokeLineCap.ROUND);

        DropShadow missShadow = new DropShadow();
        missShadow.setRadius(2);
        missShadow.setColor(Color.rgb(255, 255, 255, 0.5));
        x1.setEffect(missShadow);
        x2.setEffect(missShadow);

        cell.getChildren().addAll(x1, x2);
    }
}