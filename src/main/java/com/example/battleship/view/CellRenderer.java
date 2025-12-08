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
 * Utility class for rendering detailed 2D graphics for game cells.
 * Ships rendered in top-down aerial view style.
 *
 * @author Battleship Team
 * @version 3.0
 * @since 2025-12-08
 */
public class CellRenderer {

    private static final int CELL_SIZE = 30;

    /**
     * Renders a cell based on its state with detailed 2D graphics.
     */
    public static void renderCell(StackPane cell, Board.CellState state, boolean hideShips) {
        cell.getChildren().clear();

        switch (state) {
            case WATER:
                renderWater(cell);
                break;
            case SHIP:
                if (!hideShips) {
                    renderTopDownShip(cell);
                } else {
                    renderWater(cell);
                }
                break;
            case HIT:
                renderHit(cell, hideShips);
                break;
            case SUNK:
                renderSunk(cell);
                break;
            case MISS:
                renderMiss(cell);
                break;
        }
    }

    /**
     * Renders water with wave-like gradient effect.
     */
    private static void renderWater(StackPane cell) {
        Rectangle water = new Rectangle(CELL_SIZE - 2, CELL_SIZE - 2);

        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(100, 180, 255)),
                new Stop(0.5, Color.rgb(70, 150, 230)),
                new Stop(1, Color.rgb(50, 120, 200))
        );

        water.setFill(gradient);
        water.setArcWidth(5);
        water.setArcHeight(5);
        water.setStroke(Color.rgb(40, 100, 180));
        water.setStrokeWidth(1);

        Path wavePath = new Path();
        MoveTo moveTo = new MoveTo(5, CELL_SIZE / 2);
        QuadCurveTo curve1 = new QuadCurveTo(CELL_SIZE / 3, CELL_SIZE / 2 - 3,
                CELL_SIZE / 2, CELL_SIZE / 2);
        QuadCurveTo curve2 = new QuadCurveTo(2 * CELL_SIZE / 3, CELL_SIZE / 2 + 3,
                CELL_SIZE - 5, CELL_SIZE / 2);

        wavePath.getElements().addAll(moveTo, curve1, curve2);
        wavePath.setStroke(Color.rgb(120, 200, 255, 0.4));
        wavePath.setStrokeWidth(1.5);

        cell.getChildren().addAll(water, wavePath);
    }

    /**
     * Renders a naval ship from top-down aerial view.
     * Style matches classic battleship board game.
     */
    private static void renderTopDownShip(StackPane cell) {
        // Main hull shape (elongated ellipse/rounded rectangle)
        Path hull = new Path();
        hull.getElements().addAll(
                new MoveTo(-11, 0),                    // Left tip
                new QuadCurveTo(-11, -5, -8, -6),     // Top left curve
                new LineTo(8, -6),                     // Top edge
                new QuadCurveTo(11, -5, 11, 0),       // Top right curve (bow)
                new QuadCurveTo(11, 5, 8, 6),         // Bottom right curve
                new LineTo(-8, 6),                     // Bottom edge
                new QuadCurveTo(-11, 5, -11, 0),      // Bottom left curve (stern)
                new ClosePath()
        );

        // Hull gradient - dark brown/gray wood deck
        LinearGradient hullGradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(120, 90, 70)),   // Brown deck
                new Stop(0.5, Color.rgb(100, 75, 60)),
                new Stop(1, Color.rgb(80, 60, 50))
        );
        hull.setFill(hullGradient);
        hull.setStroke(Color.rgb(60, 45, 35));
        hull.setStrokeWidth(1.5);

        // Center line stripe (keel)
        Line centerLine = new Line(-10, 0, 10, 0);
        centerLine.setStroke(Color.rgb(90, 70, 55));
        centerLine.setStrokeWidth(2);

        // Wooden deck planks (horizontal lines)
        Line plank1 = new Line(-8, -3, 8, -3);
        plank1.setStroke(Color.rgb(110, 85, 65, 0.4));
        plank1.setStrokeWidth(0.8);

        Line plank2 = new Line(-8, 3, 8, 3);
        plank2.setStroke(Color.rgb(110, 85, 65, 0.4));
        plank2.setStrokeWidth(0.8);

        // Front superstructure (bridge/command tower)
        Rectangle bridge = new Rectangle(8, 6);
        bridge.setFill(Color.rgb(140, 140, 150));
        bridge.setStroke(Color.rgb(100, 100, 110));
        bridge.setStrokeWidth(1);
        bridge.setTranslateX(3);

        // Bridge windows (darker rectangles)
        Rectangle window1 = new Rectangle(2, 1.5);
        window1.setFill(Color.rgb(60, 80, 100));
        window1.setTranslateX(1);
        window1.setTranslateY(-1);

        Rectangle window2 = new Rectangle(2, 1.5);
        window2.setFill(Color.rgb(60, 80, 100));
        window2.setTranslateX(5);
        window2.setTranslateY(-1);

        // Main gun turrets (circles) - Front
        Circle frontTurret = new Circle(3);
        frontTurret.setFill(Color.rgb(90, 90, 100));
        frontTurret.setStroke(Color.rgb(60, 60, 70));
        frontTurret.setStrokeWidth(1);
        frontTurret.setTranslateX(-5);

        // Gun barrel (front)
        Rectangle frontBarrel = new Rectangle(4, 1);
        frontBarrel.setFill(Color.rgb(70, 70, 80));
        frontBarrel.setTranslateX(-8);

        // Rear gun turret
        Circle rearTurret = new Circle(2.5);
        rearTurret.setFill(Color.rgb(90, 90, 100));
        rearTurret.setStroke(Color.rgb(60, 60, 70));
        rearTurret.setStrokeWidth(1);
        rearTurret.setTranslateX(8);

        // Smokestacks/Funnels (2 chimneys)
        Ellipse funnel1 = new Ellipse(1.5, 2);
        funnel1.setFill(Color.rgb(80, 80, 90));
        funnel1.setStroke(Color.rgb(50, 50, 60));
        funnel1.setStrokeWidth(0.8);
        funnel1.setTranslateX(-2);
        funnel1.setTranslateY(-3);

        Ellipse funnel2 = new Ellipse(1.5, 2);
        funnel2.setFill(Color.rgb(80, 80, 90));
        funnel2.setStroke(Color.rgb(50, 50, 60));
        funnel2.setStrokeWidth(0.8);
        funnel2.setTranslateX(-2);
        funnel2.setTranslateY(3);

        // Small smoke puffs from funnels
        Circle smoke1 = new Circle(1, Color.rgb(120, 120, 130, 0.5));
        smoke1.setTranslateX(-2);
        smoke1.setTranslateY(-5);

        Circle smoke2 = new Circle(1, Color.rgb(120, 120, 130, 0.5));
        smoke2.setTranslateX(-2);
        smoke2.setTranslateY(5);

        // Lifeboats on sides (small ellipses)
        Ellipse lifeboat1 = new Ellipse(3, 1);
        lifeboat1.setFill(Color.rgb(200, 150, 100));
        lifeboat1.setStroke(Color.rgb(150, 110, 70));
        lifeboat1.setStrokeWidth(0.5);
        lifeboat1.setTranslateX(0);
        lifeboat1.setTranslateY(-5);

        Ellipse lifeboat2 = new Ellipse(3, 1);
        lifeboat2.setFill(Color.rgb(200, 150, 100));
        lifeboat2.setStroke(Color.rgb(150, 110, 70));
        lifeboat2.setStrokeWidth(0.5);
        lifeboat2.setTranslateX(0);
        lifeboat2.setTranslateY(5);

        // Radar/Antenna mast (small circle on top of bridge)
        Circle radarDish = new Circle(1.5);
        radarDish.setFill(Color.rgb(120, 120, 130));
        radarDish.setStroke(Color.rgb(80, 80, 90));
        radarDish.setStrokeWidth(0.5);
        radarDish.setTranslateX(3);
        radarDish.setTranslateY(0);

        // Anchor symbol at front (small details)
        Line anchorLine = new Line(-9, 0, -10, 0);
        anchorLine.setStroke(Color.rgb(60, 60, 70));
        anchorLine.setStrokeWidth(1.5);

        // Hull outline highlight (lighter edge)
        Path hullHighlight = new Path();
        hullHighlight.getElements().addAll(
                new MoveTo(-11, 0),
                new QuadCurveTo(-11, -5, -8, -6),
                new LineTo(8, -6)
        );
        hullHighlight.setStroke(Color.rgb(140, 110, 90, 0.5));
        hullHighlight.setStrokeWidth(1);
        hullHighlight.setFill(null);

        // Add all elements in correct z-order (back to front)
        cell.getChildren().addAll(
                hull,              // Base hull
                centerLine,        // Center stripe
                plank1,            // Deck planks
                plank2,
                hullHighlight,     // Hull edge highlight
                lifeboat1,         // Lifeboats
                lifeboat2,
                funnel1,           // Smokestacks
                funnel2,
                smoke1,            // Smoke
                smoke2,
                bridge,            // Bridge/superstructure
                window1,           // Windows
                window2,
                frontTurret,       // Gun turrets
                frontBarrel,       // Gun barrel
                rearTurret,
                radarDish,         // Radar
                anchorLine         // Anchor detail
        );
    }

    /**
     * Renders a hit with fire/explosion effect.
     */
    private static void renderHit(StackPane cell, boolean hideShips) {
        if (!hideShips) {
            // Show damaged ship underneath (darkened hull)
            Ellipse damagedHull = new Ellipse(11, 6);
            damagedHull.setFill(Color.rgb(40, 30, 25, 0.8));
            damagedHull.setStroke(Color.rgb(20, 15, 10));
            damagedHull.setStrokeWidth(1);
            cell.getChildren().add(damagedHull);
        }

        // Large explosion circle
        Circle explosion = new Circle(CELL_SIZE / 2.5);
        RadialGradient fireGradient = new RadialGradient(
                0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.YELLOW),
                new Stop(0.3, Color.ORANGE),
                new Stop(0.7, Color.rgb(255, 100, 0)),
                new Stop(1, Color.DARKRED)
        );
        explosion.setFill(fireGradient);
        explosion.setStroke(Color.DARKRED);
        explosion.setStrokeWidth(2);

        // Black smoke clouds
        Circle smoke1 = new Circle(4, Color.rgb(60, 60, 60, 0.7));
        smoke1.setTranslateX(-4);
        smoke1.setTranslateY(-6);

        Circle smoke2 = new Circle(5, Color.rgb(80, 80, 80, 0.6));
        smoke2.setTranslateX(5);
        smoke2.setTranslateY(-4);

        Circle smoke3 = new Circle(3, Color.rgb(70, 70, 70, 0.5));
        smoke3.setTranslateX(0);
        smoke3.setTranslateY(-9);

        // Fire sparks
        Circle spark1 = new Circle(1, Color.YELLOW);
        spark1.setTranslateX(-7);
        spark1.setTranslateY(2);

        Circle spark2 = new Circle(1, Color.ORANGE);
        spark2.setTranslateX(6);
        spark2.setTranslateY(-2);

        Circle spark3 = new Circle(0.8, Color.RED);
        spark3.setTranslateX(-2);
        spark3.setTranslateY(7);

        cell.getChildren().addAll(explosion, smoke1, smoke2, smoke3, spark1, spark2, spark3);
    }

    /**
     * Renders a sunk ship with wreckage.
     */
    private static void renderSunk(StackPane cell) {
        // Dark water with oil
        Rectangle darkWater = new Rectangle(CELL_SIZE - 4, CELL_SIZE - 4);
        darkWater.setFill(Color.rgb(30, 50, 70));
        darkWater.setArcWidth(8);
        darkWater.setArcHeight(8);
        darkWater.setStroke(Color.rgb(20, 30, 40));
        darkWater.setStrokeWidth(2);

        // Oil spill (black/brown irregular shape)
        Ellipse oilSpill = new Ellipse(12, 8);
        oilSpill.setFill(Color.rgb(20, 20, 25, 0.7));
        oilSpill.setStroke(Color.rgb(40, 35, 30));
        oilSpill.setStrokeWidth(1);

        // Broken ship pieces floating
        Polygon wreckage1 = new Polygon(
                -7, -4,
                -3, -6,
                -1, -3
        );
        wreckage1.setFill(Color.rgb(60, 50, 40));
        wreckage1.setStroke(Color.rgb(40, 30, 20));

        Polygon wreckage2 = new Polygon(
                4, 3,
                8, 1,
                6, 6
        );
        wreckage2.setFill(Color.rgb(60, 50, 40));
        wreckage2.setStroke(Color.rgb(40, 30, 20));

        Rectangle wreckage3 = new Rectangle(4, 2);
        wreckage3.setFill(Color.rgb(70, 60, 50));
        wreckage3.setTranslateX(-5);
        wreckage3.setTranslateY(4);
        wreckage3.setRotate(25);

        // Red X marking sunk ship
        Line x1 = new Line(-9, -9, 9, 9);
        x1.setStroke(Color.DARKRED);
        x1.setStrokeWidth(3.5);
        x1.setStrokeLineCap(StrokeLineCap.ROUND);

        Line x2 = new Line(9, -9, -9, 9);
        x2.setStroke(Color.DARKRED);
        x2.setStrokeWidth(3.5);
        x2.setStrokeLineCap(StrokeLineCap.ROUND);

        // Bubbles rising from wreck
        Circle bubble1 = new Circle(1.5, Color.rgb(180, 220, 255, 0.6));
        bubble1.setTranslateX(-6);
        bubble1.setTranslateY(-7);

        Circle bubble2 = new Circle(1, Color.rgb(180, 220, 255, 0.5));
        bubble2.setTranslateX(7);
        bubble2.setTranslateY(-5);

        Circle bubble3 = new Circle(1.2, Color.rgb(180, 220, 255, 0.4));
        bubble3.setTranslateX(2);
        bubble3.setTranslateY(-8);

        cell.getChildren().addAll(darkWater, oilSpill, wreckage1, wreckage2, wreckage3,
                x1, x2, bubble1, bubble2, bubble3);
    }

    /**
     * Renders a miss with splash effect.
     */
    private static void renderMiss(StackPane cell) {
        // Water splash circles (concentric)
        Circle splash1 = new Circle(CELL_SIZE / 3.5, Color.rgb(200, 220, 255, 0.5));
        splash1.setStroke(Color.rgb(100, 150, 200));
        splash1.setStrokeWidth(2);

        Circle splash2 = new Circle(CELL_SIZE / 5, Color.rgb(180, 210, 255, 0.7));
        splash2.setStroke(Color.rgb(120, 170, 220));
        splash2.setStrokeWidth(1.5);

        // White X to indicate miss
        Line miss1 = new Line(-8, -8, 8, 8);
        miss1.setStroke(Color.WHITE);
        miss1.setStrokeWidth(3);
        miss1.setStrokeLineCap(StrokeLineCap.ROUND);

        Line miss2 = new Line(8, -8, -8, 8);
        miss2.setStroke(Color.WHITE);
        miss2.setStrokeWidth(3);
        miss2.setStrokeLineCap(StrokeLineCap.ROUND);

        // Water droplets flying
        Circle droplet1 = new Circle(1.5, Color.rgb(150, 200, 255));
        droplet1.setTranslateX(-11);
        droplet1.setTranslateY(-6);

        Circle droplet2 = new Circle(1.5, Color.rgb(150, 200, 255));
        droplet2.setTranslateX(10);
        droplet2.setTranslateY(-8);

        Circle droplet3 = new Circle(1.2, Color.rgb(150, 200, 255));
        droplet3.setTranslateX(-4);
        droplet3.setTranslateY(11);

        Circle droplet4 = new Circle(1, Color.rgb(150, 200, 255));
        droplet4.setTranslateX(7);
        droplet4.setTranslateY(9);

        cell.getChildren().addAll(splash1, splash2, miss1, miss2,
                droplet1, droplet2, droplet3, droplet4);
    }
}