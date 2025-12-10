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
 * Cell renderer with 3D perspective ships and realistic water.
 * Ships have depth/height appearance with shadows and gradients.
 *
 * @author Battleship Team
 * @version 8.0
 */
public class CellRenderer {

    private static final int CELL_SIZE = 30;

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

        switch (state) {
            case WATER:
                renderRealisticWater(cell);
                break;
            case SHIP:
                if (!hideShips && shipInfo != null) {
                    String type = (String) shipInfo[0];
                    int position = (int) shipInfo[1];
                    int totalSize = (int) shipInfo[2];
                    boolean isHorizontal = (boolean) shipInfo[3];

                    renderRealisticWater(cell);
                    renderShipPart3D(cell, type, position, totalSize, isHorizontal);
                } else {
                    renderRealisticWater(cell);
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

    private static void renderRealisticWater(StackPane cell) {
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
        depth.setRadius(5);
        water.setEffect(depth);

        cell.getChildren().add(water);

        Path wave = new Path();
        wave.getElements().addAll(
                new MoveTo(0, CELL_SIZE * 0.3),
                new QuadCurveTo(CELL_SIZE * 0.3, CELL_SIZE * 0.25, CELL_SIZE * 0.5, CELL_SIZE * 0.3),
                new QuadCurveTo(CELL_SIZE * 0.7, CELL_SIZE * 0.35, CELL_SIZE, CELL_SIZE * 0.3)
        );
        wave.setStroke(Color.rgb(200, 230, 255, 0.2));
        wave.setStrokeWidth(1);
        cell.getChildren().add(wave);
    }

    private static void renderShipPart3D(StackPane cell, String type, int position,
                                         int totalSize, boolean isHorizontal) {
        if (!isHorizontal) {
            cell.setRotate(90);
        }

        switch (type) {
            case "Carrier":
                renderCarrier3D(cell, position, totalSize);
                break;
            case "Submarine":
                renderSubmarine3D(cell, position, totalSize);
                break;
            case "Destroyer":
                renderDestroyer3D(cell, position, totalSize);
                break;
            case "Frigate":
                renderFrigate3D(cell);
                break;
        }
    }

    private static void renderCarrier3D(StackPane cell, int position, int totalSize) {
        if (position == 0) {
            Polygon hullBottom = new Polygon(
                    -15, 2, -15, 11, 15, 11, 15, 2
            );
            hullBottom.setFill(HULL_SHADOW);
            hullBottom.setStroke(OUTLINE);
            hullBottom.setStrokeWidth(1.5);

            Polygon deckTop = new Polygon(
                    -15, -11, 15, -11, 15, 2, -15, 2
            );
            deckTop.setFill(HULL_TOP);
            deckTop.setStroke(OUTLINE);
            deckTop.setStrokeWidth(1.5);

            Polygon bow = new Polygon(
                    -18, 2, -18, -8, -15, -11, -15, 11, -18, 8
            );
            bow.setFill(HULL_SIDE);
            bow.setStroke(OUTLINE);
            bow.setStrokeWidth(1.5);

            Rectangle orangeTop = new Rectangle(28, 3);
            orangeTop.setFill(ORANGE_LIGHT);
            orangeTop.setStroke(OUTLINE);
            orangeTop.setTranslateY(-8);

            Rectangle orangeSide = new Rectangle(28, 2);
            orangeSide.setFill(ORANGE_DARK);
            orangeSide.setStroke(OUTLINE);
            orangeSide.setTranslateY(-5.5);

            DropShadow shadow = new DropShadow();
            shadow.setRadius(3);
            shadow.setColor(Color.rgb(0, 0, 0, 0.4));
            shadow.setOffsetY(2);
            deckTop.setEffect(shadow);

            cell.getChildren().addAll(hullBottom, bow, deckTop, orangeSide, orangeTop);

        } else if (position == 1 || position == 2) {
            Rectangle bottom = new Rectangle(CELL_SIZE, 5);
            bottom.setFill(HULL_SHADOW);
            bottom.setStroke(OUTLINE);
            bottom.setTranslateY(8);

            Rectangle top = new Rectangle(CELL_SIZE, 20);
            top.setFill(DECK);
            top.setStroke(OUTLINE);
            top.setStrokeWidth(1.5);
            top.setTranslateY(-2);

            for (int i = -10; i <= 8; i += 4) {
                Line line = new Line(-15, i, 15, i);
                line.setStroke(Color.rgb(80, 90, 70, 0.6));
                line.setStrokeWidth(0.7);
                cell.getChildren().add(line);
            }

            if (position == 1) {
                render3DJet(cell, -8, -5);
            } else {
                render3DJet(cell, -8, 3);
            }

            Rectangle sideL = new Rectangle(5, 6);
            sideL.setFill(HULL_SIDE);
            sideL.setStroke(OUTLINE);
            sideL.setTranslateX(-12);
            sideL.setTranslateY(-9);

            Rectangle sideLTop = new Rectangle(5, 2);
            sideLTop.setFill(HULL_TOP);
            sideLTop.setStroke(OUTLINE);
            sideLTop.setTranslateX(-12);
            sideLTop.setTranslateY(-12);

            DropShadow deckShadow = new DropShadow();
            deckShadow.setRadius(2);
            deckShadow.setColor(Color.rgb(0, 0, 0, 0.3));
            deckShadow.setOffsetY(1);
            top.setEffect(deckShadow);

            cell.getChildren().addAll(bottom, top, sideL, sideLTop);

        } else {
            Rectangle bottom = new Rectangle(CELL_SIZE, 5);
            bottom.setFill(HULL_SHADOW);
            bottom.setStroke(OUTLINE);
            bottom.setTranslateY(8);

            Rectangle deck = new Rectangle(CELL_SIZE, 20);
            deck.setFill(DECK);
            deck.setStroke(OUTLINE);
            deck.setStrokeWidth(1.5);
            deck.setTranslateY(-2);

            Rectangle towerSide = new Rectangle(3, 20);
            towerSide.setFill(HULL_SHADOW);
            towerSide.setStroke(OUTLINE);
            towerSide.setTranslateX(11);

            Rectangle towerFront = new Rectangle(12, 20);
            towerFront.setFill(HULL_SIDE);
            towerFront.setStroke(OUTLINE);
            towerFront.setStrokeWidth(1.5);
            towerFront.setTranslateX(4);

            Rectangle towerTop = new Rectangle(14, 3);
            towerTop.setFill(HULL_TOP);
            towerTop.setStroke(OUTLINE);
            towerTop.setTranslateX(3);
            towerTop.setTranslateY(-12);

            Rectangle win1 = new Rectangle(3, 2.5);
            win1.setFill(Color.rgb(40, 50, 60));
            win1.setStroke(OUTLINE);
            win1.setTranslateX(1);
            win1.setTranslateY(-6);

            Rectangle win2 = new Rectangle(3, 2.5);
            win2.setFill(Color.rgb(40, 50, 60));
            win2.setStroke(OUTLINE);
            win2.setTranslateX(6);
            win2.setTranslateY(-6);

            Circle radar1 = new Circle(2.5);
            radar1.setFill(Color.rgb(120, 120, 130));
            radar1.setStroke(OUTLINE);
            radar1.setTranslateX(1);
            radar1.setTranslateY(-14);

            InnerShadow radarShadow = new InnerShadow();
            radarShadow.setColor(Color.rgb(0, 0, 0, 0.3));
            radar1.setEffect(radarShadow);

            Rectangle orangeBox = new Rectangle(3, 5);
            orangeBox.setFill(ORANGE_LIGHT);
            orangeBox.setStroke(OUTLINE);
            orangeBox.setTranslateX(10);
            orangeBox.setTranslateY(7);

            DropShadow towerDropShadow = new DropShadow();
            towerDropShadow.setRadius(3);
            towerDropShadow.setColor(Color.rgb(0, 0, 0, 0.4));
            towerFront.setEffect(towerDropShadow);

            cell.getChildren().addAll(bottom, deck, towerSide, towerFront, towerTop,
                    win1, win2, radar1, orangeBox);
        }
    }

    private static void render3DJet(StackPane cell, double x, double y) {
        Polygon jetShadow = new Polygon(
                x, y + 1, x + 5, y - 1, x + 8, y - 1,
                x + 9, y + 1, x + 8, y + 3, x + 5, y + 3
        );
        jetShadow.setFill(Color.rgb(50, 50, 60));
        jetShadow.setStroke(OUTLINE);
        jetShadow.setStrokeWidth(0.8);

        Polygon jet = new Polygon(
                x, y, x + 5, y - 2, x + 8, y - 2,
                x + 9, y, x + 8, y + 2, x + 5, y + 2
        );
        jet.setFill(Color.rgb(80, 80, 90));
        jet.setStroke(OUTLINE);
        jet.setStrokeWidth(1);

        Polygon wings = new Polygon(
                x + 3, y - 4, x + 5, y - 4, x + 5, y + 4, x + 3, y + 4
        );
        wings.setFill(Color.rgb(70, 70, 80));
        wings.setStroke(OUTLINE);
        wings.setStrokeWidth(0.8);

        cell.getChildren().addAll(wings, jetShadow, jet);
    }

    private static void renderSubmarine3D(StackPane cell, int position, int totalSize) {
        if (position == 0) {
            Ellipse bottom = new Ellipse(12, 5);
            bottom.setFill(HULL_SHADOW);
            bottom.setStroke(OUTLINE);
            bottom.setTranslateY(3);

            Ellipse top = new Ellipse(12, 7);
            top.setFill(HULL_SIDE);
            top.setStroke(OUTLINE);
            top.setStrokeWidth(1.5);
            top.setTranslateY(-1);

            Ellipse highlight = new Ellipse(10, 3);
            highlight.setFill(HULL_TOP);
            highlight.setStroke(OUTLINE);
            highlight.setTranslateY(-5);

            Circle porthole = new Circle(3);
            porthole.setStroke(OUTLINE);
            porthole.setStrokeWidth(1.2);
            porthole.setTranslateX(-8);
            porthole.setTranslateY(-5);

            RadialGradient glow = new RadialGradient(
                    0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                    new Stop(0, ORANGE_LIGHT),
                    new Stop(1, ORANGE_DARK)
            );
            porthole.setFill(glow);

            cell.getChildren().addAll(bottom, top, highlight, porthole);

        } else if (position == 1) {
            Rectangle bottom = new Rectangle(CELL_SIZE, 5);
            bottom.setFill(HULL_SHADOW);
            bottom.setStroke(OUTLINE);
            bottom.setTranslateY(6);

            Rectangle body = new Rectangle(CELL_SIZE, 14);
            body.setFill(HULL_SIDE);
            body.setStroke(OUTLINE);
            body.setStrokeWidth(1.5);
            body.setTranslateY(-1);

            Rectangle topHighlight = new Rectangle(CELL_SIZE, 4);
            topHighlight.setFill(HULL_TOP);
            topHighlight.setStroke(OUTLINE);
            topHighlight.setTranslateY(-7);

            Rectangle towerShadow = new Rectangle(2, 12);
            towerShadow.setFill(HULL_SHADOW);
            towerShadow.setTranslateX(6);
            towerShadow.setTranslateY(-11);

            Rectangle tower = new Rectangle(8, 12);
            tower.setFill(HULL_SIDE);
            tower.setStroke(OUTLINE);
            tower.setStrokeWidth(1.5);
            tower.setArcWidth(3);
            tower.setArcHeight(3);
            tower.setTranslateY(-11);

            Rectangle towerTop = new Rectangle(9, 2);
            towerTop.setFill(HULL_TOP);
            towerTop.setStroke(OUTLINE);
            towerTop.setTranslateY(-18);

            Polygon planeL = new Polygon(
                    -15, -2, -10, -4, -10, 4, -15, 2
            );
            planeL.setFill(HULL_SIDE);
            planeL.setStroke(OUTLINE);

            cell.getChildren().addAll(bottom, body, topHighlight, towerShadow, tower, towerTop, planeL);

        } else {
            Rectangle bottom = new Rectangle(CELL_SIZE, 5);
            bottom.setFill(HULL_SHADOW);
            bottom.setStroke(OUTLINE);
            bottom.setTranslateY(6);

            Rectangle body = new Rectangle(CELL_SIZE, 14);
            body.setFill(HULL_SIDE);
            body.setStroke(OUTLINE);
            body.setStrokeWidth(1.5);
            body.setTranslateY(-1);

            for (int i = 0; i < 5; i++) {
                Circle shadow = new Circle(3);
                shadow.setFill(HULL_SHADOW);
                shadow.setTranslateX(-3);
                shadow.setTranslateY(-6 + i * 3.5);

                Circle housing = new Circle(2.8);
                housing.setFill(HULL_TOP);
                housing.setStroke(OUTLINE);
                housing.setStrokeWidth(1);
                housing.setTranslateX(-4);
                housing.setTranslateY(-6.5 + i * 3.5);

                cell.getChildren().addAll(shadow, housing);
            }

            Ellipse propShadow = new Ellipse(4, 7);
            propShadow.setFill(Color.rgb(40, 50, 45));
            propShadow.setTranslateX(11);

            Ellipse prop = new Ellipse(4, 6);
            prop.setFill(Color.rgb(55, 65, 60));
            prop.setStroke(OUTLINE);
            prop.setStrokeWidth(1.5);
            prop.setTranslateX(10);

            cell.getChildren().addAll(bottom, body, propShadow, prop);
        }
    }

    private static void renderDestroyer3D(StackPane cell, int position, int totalSize) {
        Rectangle bottom = new Rectangle(CELL_SIZE, 4);
        bottom.setFill(HULL_SHADOW);
        bottom.setStroke(OUTLINE);
        bottom.setTranslateY(9);

        Rectangle hull = new Rectangle(CELL_SIZE, 16);
        hull.setFill(HULL_SIDE);
        hull.setStroke(OUTLINE);
        hull.setStrokeWidth(1.5);
        hull.setTranslateY(0);

        Rectangle deck = new Rectangle(CELL_SIZE, 3);
        deck.setFill(HULL_TOP);
        deck.setStroke(OUTLINE);
        deck.setTranslateY(-9);

        cell.getChildren().addAll(bottom, hull, deck);

        if (position == 0) {
            Circle gunShadow = new Circle(5);
            gunShadow.setFill(HULL_SHADOW);
            gunShadow.setTranslateY(-9);

            Circle gun = new Circle(4.5);
            gun.setFill(HULL_TOP);
            gun.setStroke(OUTLINE);
            gun.setStrokeWidth(1.5);
            gun.setTranslateY(-10);

            Line barrel = new Line(-4, -10, -12, -10);
            barrel.setStroke(OUTLINE);
            barrel.setStrokeWidth(2.5);

            cell.getChildren().addAll(gunShadow, gun, barrel);
        } else {
            Rectangle box = new Rectangle(9, 7);
            box.setFill(HULL_SIDE);
            box.setStroke(OUTLINE);
            box.setTranslateY(-5);

            Rectangle boxTop = new Rectangle(10, 2);
            boxTop.setFill(HULL_TOP);
            boxTop.setStroke(OUTLINE);
            boxTop.setTranslateY(-9);

            cell.getChildren().addAll(box, boxTop);
        }
    }

    private static void renderFrigate3D(StackPane cell) {
        Ellipse bottom = new Ellipse(11, 5);
        bottom.setFill(HULL_SHADOW);
        bottom.setTranslateY(3);

        Ellipse hull = new Ellipse(11, 7);
        hull.setFill(HULL_SIDE);
        hull.setStroke(OUTLINE);
        hull.setStrokeWidth(1.5);

        Ellipse top = new Ellipse(9, 3);
        top.setFill(HULL_TOP);
        top.setStroke(OUTLINE);
        top.setTranslateY(-5);

        Rectangle cabin = new Rectangle(8, 7);
        cabin.setFill(HULL_SIDE);
        cabin.setStroke(OUTLINE);
        cabin.setStrokeWidth(1.2);

        Rectangle cabinTop = new Rectangle(9, 2);
        cabinTop.setFill(HULL_TOP);
        cabinTop.setStroke(OUTLINE);
        cabinTop.setTranslateY(-5);

        cell.getChildren().addAll(bottom, hull, top, cabin, cabinTop);
    }

    private static void renderHit(StackPane cell, boolean hideShips, Object[] shipInfo) {
        renderRealisticWater(cell);

        if (!hideShips && shipInfo != null) {
            String type = (String) shipInfo[0];
            int position = (int) shipInfo[1];
            int totalSize = (int) shipInfo[2];
            boolean isHorizontal = (boolean) shipInfo[3];

            renderShipPart3D(cell, type, position, totalSize, isHorizontal);

            Rectangle damage = new Rectangle(CELL_SIZE, CELL_SIZE);
            damage.setFill(Color.rgb(0, 0, 0, 0.4));
            cell.getChildren().add(damage);
        }

        Circle explosion = new Circle(12);
        RadialGradient fire = new RadialGradient(
                0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.YELLOW),
                new Stop(0.4, Color.ORANGE),
                new Stop(1, Color.DARKRED)
        );
        explosion.setFill(fire);

        DropShadow explosionShadow = new DropShadow();
        explosionShadow.setRadius(5);
        explosionShadow.setColor(Color.rgb(255, 100, 0, 0.6));
        explosion.setEffect(explosionShadow);

        cell.getChildren().add(explosion);
    }

    private static void renderSunk(StackPane cell, Object[] shipInfo) {
        renderRealisticWater(cell);

        Polygon wreck = new Polygon(-8, -3, 8, -1, 6, 5, -10, 3);
        wreck.setFill(Color.rgb(40, 45, 50));
        wreck.setStroke(Color.rgb(20, 25, 30));
        wreck.setStrokeWidth(1.5);
        cell.getChildren().add(wreck);

        Line x1 = new Line(-12, -12, 12, 12);
        x1.setStroke(Color.DARKRED);
        x1.setStrokeWidth(4);
        x1.setStrokeLineCap(StrokeLineCap.ROUND);

        Line x2 = new Line(12, -12, -12, 12);
        x2.setStroke(Color.DARKRED);
        x2.setStrokeWidth(4);
        x2.setStrokeLineCap(StrokeLineCap.ROUND);

        cell.getChildren().addAll(x1, x2);
    }

    private static void renderMiss(StackPane cell) {
        renderRealisticWater(cell);

        Circle splash = new Circle(12, Color.rgb(220, 240, 255, 0.7));
        splash.setStroke(Color.rgb(180, 220, 250));
        splash.setStrokeWidth(2.5);
        cell.getChildren().add(splash);

        Line x1 = new Line(-10, -10, 10, 10);
        x1.setStroke(Color.WHITE);
        x1.setStrokeWidth(4);
        x1.setStrokeLineCap(StrokeLineCap.ROUND);

        Line x2 = new Line(10, -10, -10, 10);
        x2.setStroke(Color.WHITE);
        x2.setStrokeWidth(4);
        x2.setStrokeLineCap(StrokeLineCap.ROUND);

        DropShadow missShadow = new DropShadow();
        missShadow.setRadius(3);
        missShadow.setColor(Color.rgb(255, 255, 255, 0.5));
        x1.setEffect(missShadow);
        x2.setEffect(missShadow);

        cell.getChildren().addAll(x1, x2);
    }
}