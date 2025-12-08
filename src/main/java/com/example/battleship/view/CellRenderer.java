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
 * Implements sophisticated visual representations for water, ships, hits, misses, and sunk ships.
 *
 * @author Battleship Team
 * @version 1.0
 * @since 2025-12-07
 */
public class CellRenderer {

    private static final int CELL_SIZE = 30;

    /**
     * Renders a cell based on its state with detailed 2D graphics.
     *
     * @param cell The StackPane to render into
     * @param state The current state of the cell
     * @param hideShips Whether to hide ship graphics (for enemy board)
     */
    public static void renderCell(StackPane cell, Board.CellState state, boolean hideShips) {
        cell.getChildren().clear();

        switch (state) {
            case WATER:
                renderWater(cell);
                break;
            case SHIP:
                if (!hideShips) {
                    renderShip(cell);
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
     *
     * @param cell The StackPane to render into
     */
    private static void renderWater(StackPane cell) {
        Rectangle water = new Rectangle(CELL_SIZE - 2, CELL_SIZE - 2);

        // Water gradient: light blue to darker blue
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

        // Add subtle wave lines
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
     * Renders a ship with metallic appearance and details.
     *
     * @param cell The StackPane to render into
     */
    private static void renderShip(StackPane cell) {
        Rectangle shipBody = new Rectangle(CELL_SIZE - 4, CELL_SIZE - 4);

        // Metallic gray gradient
        LinearGradient metalGradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(120, 120, 130)),
                new Stop(0.5, Color.rgb(80, 80, 90)),
                new Stop(1, Color.rgb(60, 60, 70))
        );

        shipBody.setFill(metalGradient);
        shipBody.setArcWidth(8);
        shipBody.setArcHeight(8);
        shipBody.setStroke(Color.rgb(40, 40, 50));
        shipBody.setStrokeWidth(2);

        // Add ship details: rivets
        Circle rivet1 = new Circle(2, Color.rgb(100, 100, 110));
        rivet1.setTranslateX(-6);
        rivet1.setTranslateY(-6);
        rivet1.setStroke(Color.rgb(60, 60, 70));
        rivet1.setStrokeWidth(0.5);

        Circle rivet2 = new Circle(2, Color.rgb(100, 100, 110));
        rivet2.setTranslateX(6);
        rivet2.setTranslateY(6);
        rivet2.setStroke(Color.rgb(60, 60, 70));
        rivet2.setStrokeWidth(0.5);

        // Metallic shine effect
        Rectangle shine = new Rectangle(CELL_SIZE - 10, 3);
        shine.setFill(Color.rgb(200, 200, 210, 0.3));
        shine.setArcWidth(2);
        shine.setArcHeight(2);
        shine.setTranslateY(-5);

        cell.getChildren().addAll(shipBody, shine, rivet1, rivet2);
    }

    /**
     * Renders a hit with fire/explosion effect.
     *
     * @param cell The StackPane to render into
     * @param hideShips Whether to hide the damaged ship underneath
     */
    private static void renderHit(StackPane cell, boolean hideShips) {
        if (!hideShips) {
            // Show damaged ship underneath
            Rectangle damagedShip = new Rectangle(CELL_SIZE - 4, CELL_SIZE - 4);
            damagedShip.setFill(Color.rgb(60, 60, 70, 0.6));
            damagedShip.setArcWidth(8);
            damagedShip.setArcHeight(8);
            cell.getChildren().add(damagedShip);
        }

        // Fire/explosion effect with gradient
        Circle explosion = new Circle(CELL_SIZE / 2.5);

        // Fire gradient: yellow center to red edges
        RadialGradient fireGradient = new RadialGradient(
                0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.YELLOW),
                new Stop(0.3, Color.ORANGE),
                new Stop(0.7, Color.rgb(255, 100, 0)),
                new Stop(1, Color.DARKRED)
        );

        explosion.setFill(fireGradient);
        explosion.setStroke(Color.DARKRED);
        explosion.setStrokeWidth(1.5);

        // Add smoke particles
        Circle smoke1 = new Circle(3, Color.rgb(80, 80, 80, 0.5));
        smoke1.setTranslateX(-5);
        smoke1.setTranslateY(-8);

        Circle smoke2 = new Circle(4, Color.rgb(100, 100, 100, 0.4));
        smoke2.setTranslateX(4);
        smoke2.setTranslateY(-6);

        Circle smoke3 = new Circle(2.5, Color.rgb(90, 90, 90, 0.3));
        smoke3.setTranslateX(0);
        smoke3.setTranslateY(-10);

        cell.getChildren().addAll(explosion, smoke1, smoke2, smoke3);
    }

    /**
     * Renders a sunk ship with detailed wreckage.
     *
     * @param cell The StackPane to render into
     */
    private static void renderSunk(StackPane cell) {
        // Dark wreckage background
        Rectangle wreckage = new Rectangle(CELL_SIZE - 4, CELL_SIZE - 4);
        wreckage.setFill(Color.rgb(30, 30, 40));
        wreckage.setArcWidth(8);
        wreckage.setArcHeight(8);
        wreckage.setStroke(Color.BLACK);
        wreckage.setStrokeWidth(2);

        // Oil spill effect
        Ellipse oilSpill = new Ellipse(CELL_SIZE / 2.2, CELL_SIZE / 2.8);
        oilSpill.setFill(Color.rgb(20, 20, 30, 0.6));
        oilSpill.setStroke(Color.rgb(40, 40, 50));
        oilSpill.setStrokeWidth(1);

        // Red X marking sunk ship
        Line x1 = new Line(-8, -8, 8, 8);
        x1.setStroke(Color.DARKRED);
        x1.setStrokeWidth(3);
        x1.setStrokeLineCap(StrokeLineCap.ROUND);

        Line x2 = new Line(8, -8, -8, 8);
        x2.setStroke(Color.DARKRED);
        x2.setStrokeWidth(3);
        x2.setStrokeLineCap(StrokeLineCap.ROUND);

        // Skull symbol for dramatic effect
        Circle skull = new Circle(4, Color.rgb(200, 200, 200, 0.8));
        skull.setTranslateY(-1);

        Rectangle jaw = new Rectangle(5, 3);
        jaw.setFill(Color.rgb(180, 180, 180, 0.8));
        jaw.setArcWidth(2);
        jaw.setArcHeight(2);
        jaw.setTranslateY(4);

        cell.getChildren().addAll(wreckage, oilSpill, x1, x2, skull, jaw);
    }

    /**
     * Renders a miss with splash effect.
     *
     * @param cell The StackPane to render into
     */
    private static void renderMiss(StackPane cell) {
        // Water splash circles
        Circle splash1 = new Circle(CELL_SIZE / 3.5, Color.rgb(200, 220, 255, 0.5));
        splash1.setStroke(Color.rgb(100, 150, 200));
        splash1.setStrokeWidth(1.5);

        Circle splash2 = new Circle(CELL_SIZE / 5, Color.rgb(180, 210, 255, 0.6));

        // White X to indicate miss
        Line miss1 = new Line(-7, -7, 7, 7);
        miss1.setStroke(Color.WHITE);
        miss1.setStrokeWidth(2.5);
        miss1.setStrokeLineCap(StrokeLineCap.ROUND);

        Line miss2 = new Line(7, -7, -7, 7);
        miss2.setStroke(Color.WHITE);
        miss2.setStrokeWidth(2.5);
        miss2.setStrokeLineCap(StrokeLineCap.ROUND);

        // Add small water droplets
        Circle droplet1 = new Circle(1.5, Color.rgb(150, 200, 255));
        droplet1.setTranslateX(-10);
        droplet1.setTranslateY(-5);

        Circle droplet2 = new Circle(1.5, Color.rgb(150, 200, 255));
        droplet2.setTranslateX(9);
        droplet2.setTranslateY(-7);

        Circle droplet3 = new Circle(1.5, Color.rgb(150, 200, 255));
        droplet3.setTranslateX(-3);
        droplet3.setTranslateY(10);

        cell.getChildren().addAll(splash1, splash2, miss1, miss2, droplet1, droplet2, droplet3);
    }
}