package com.example.battleship.view;

import com.example.battleship.model.Board;
import com.example.battleship.model.Coordinate;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

/**
 * Panel that shows a preview of the ship to be placed.
 * Displays the ship's appearance and orientation before placement.
 *
 * @author Battleship Team
 * @version 1.0
 * @since 2025-12-09
 */
public class ShipPreviewPanel {

    private static final int CELL_SIZE = 30;

    /**
     * Creates a preview grid showing the ship to be placed.
     *
     * @param shipType Type of ship (Carrier, Submarine, etc.)
     * @param shipSize Size of the ship in cells
     * @param isHorizontal Orientation of the ship
     * @return GridPane with ship preview
     */
    public static GridPane createPreview(String shipType, int shipSize, boolean isHorizontal) {
        GridPane previewGrid = new GridPane();
        previewGrid.setStyle("-fx-background-color: #34495e; -fx-padding: 5;");

        // Create cells based on orientation
        if (isHorizontal) {
            for (int col = 0; col < shipSize; col++) {
                StackPane cell = createPreviewCell(shipType, col, shipSize, true);
                previewGrid.add(cell, col, 0);
            }
        } else {
            for (int row = 0; row < shipSize; row++) {
                StackPane cell = createPreviewCell(shipType, row, shipSize, false);
                previewGrid.add(cell, 0, row);
            }
        }

        return previewGrid;
    }

    /**
     * Creates a single preview cell with ship rendering.
     *
     * @param shipType Type of ship
     * @param position Position in the ship (0 = bow, size-1 = stern)
     * @param totalSize Total size of the ship
     * @param isHorizontal Orientation
     * @return StackPane with rendered ship part
     */
    private static StackPane createPreviewCell(String shipType, int position,
                                               int totalSize, boolean isHorizontal) {
        StackPane cell = new StackPane();
        cell.setPrefSize(CELL_SIZE, CELL_SIZE);
        cell.setStyle("-fx-border-color: #7f8c8d; -fx-border-width: 0.5;");

        // Render the ship part
        Object[] shipInfo = new Object[]{shipType, position, totalSize, isHorizontal};
        CellRenderer.renderCell(cell, Board.CellState.SHIP, false, shipInfo);

        return cell;
    }
}
