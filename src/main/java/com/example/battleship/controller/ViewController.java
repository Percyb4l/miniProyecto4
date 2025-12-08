package com.example.battleship.controller;

import com.example.battleship.exceptions.InvalidShipPlacementException;
import com.example.battleship.model.Board;
import com.example.battleship.model.Coordinate;
import com.example.battleship.model.Ship;
import com.example.battleship.patterns.GameObserver;
import com.example.battleship.util.ArchivoUtil;
import com.example.battleship.view.CellRenderer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * View controller for the Battleship game interface.
 * Handles user interactions, UI updates, and implements GameObserver.
 *
 * @author Battleship Team
 * @version 1.0
 * @since 2025-12-07
 */
public class ViewController implements Initializable, GameObserver {

    @FXML private GridPane playerGrid;
    @FXML private GridPane machineGrid;
    @FXML private Label lblStatus;
    @FXML private TextArea txtLog;
    @FXML private Button btnStart;
    @FXML private Button btnRotate;

    private GameController gameController;
    private boolean isHorizontalPlacement = true;
    private boolean isGameStarted = false;

    private final int CELL_SIZE = 30;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameController = new GameController();

        // Register this controller as observer
        gameController.addObserver(this);

        // Initialize grids
        initializeGrid(playerGrid, true);
        initializeGrid(machineGrid, false);

        // Set callback for machine turn
        gameController.setOnMachineTurnFinished(() -> {
            refreshBoard(playerGrid, gameController.getPlayerBoard(), false);
            refreshBoard(machineGrid, gameController.getMachineBoard(), true);
            lblStatus.setText(gameController.isGameOver() ? "GAME OVER" : "Your Turn!");
            checkGameOver();
        });

        // Check for saved game
        if (ArchivoUtil.loadGame() != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Saved Game Found");
            alert.setHeaderText("A saved game was found.");
            alert.setContentText("Do you want to continue or start a new game?");

            ButtonType buttonTypeContinue = new ButtonType("Continue");
            ButtonType buttonTypeNew = new ButtonType("New Game");
            alert.getButtonTypes().setAll(buttonTypeContinue, buttonTypeNew);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == buttonTypeContinue) {
                boolean success = gameController.loadGameFromSave();
                if (success) {
                    isGameStarted = true;
                    btnStart.setDisable(true);
                    btnRotate.setDisable(true);
                    lblStatus.setText("Game Resumed. Fire away!");
                    log("Game loaded successfully. Resume battle!");

                    refreshBoard(playerGrid, gameController.getPlayerBoard(), false);
                    refreshBoard(machineGrid, gameController.getMachineBoard(), true);
                }
            } else {
                gameController.resetGame();
                isGameStarted = false;
                log("New game started. Place your fleet.");
                lblStatus.setText("Place: Carrier (4 cells)");
            }
        } else {
            log("Welcome Admiral! Place your ships.");
            lblStatus.setText("Place: Carrier (4 cells)");
        }

        // Setup keyboard events (after scene is available)
        Platform.runLater(() -> setupKeyboardEvents());
    }

    /**
     * Sets up keyboard event handlers.
     */
    private void setupKeyboardEvents() {
        if (playerGrid.getScene() != null) {
            playerGrid.getScene().setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.R) {
                    handleRotate();
                    event.consume();
                } else if (event.getCode() == KeyCode.SPACE) {
                    if (!btnStart.isDisable()) {
                        handleStartGame();
                    }
                    event.consume();
                } else if (event.getCode() == KeyCode.ESCAPE) {
                    // Could implement pause menu here
                    log("ESC pressed - Pause menu not implemented");
                }
            });
        }
    }

    /**
     * Initializes a grid with cells and event handlers.
     *
     * @param grid The GridPane to initialize
     * @param isPlayer true if player grid, false if machine grid
     */
    private void initializeGrid(GridPane grid, boolean isPlayer) {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(CELL_SIZE, CELL_SIZE);
                cell.setStyle("-fx-border-color: #7f8c8d; -fx-border-width: 0.5; -fx-background-color: #3498db;");

                int finalRow = row;
                int finalCol = col;

                cell.setOnMouseClicked(e -> {
                    if (isPlayer) handlePlayerGridClick(finalRow, finalCol);
                    else handleMachineGridClick(finalRow, finalCol);
                });

                grid.add(cell, col, row);
            }
        }
    }

    /**
     * Handles rotation button click.
     */
    @FXML
    private void handleRotate() {
        isHorizontalPlacement = !isHorizontalPlacement;
        btnRotate.setText("Rotate Ship (" + (isHorizontalPlacement ? "Horizontal" : "Vertical") + ")");
        log("Ship orientation: " + (isHorizontalPlacement ? "Horizontal" : "Vertical"));
    }

    /**
     * Handles start game button click.
     */
    @FXML
    private void handleStartGame() {
        if (gameController.getNextShipToPlace() != null) {
            log("You must place all ships first!");
            return;
        }
        isGameStarted = true;
        btnStart.setDisable(true);
        btnRotate.setDisable(true);

        gameController.startNewGame();

        lblStatus.setText("BATTLE STARTED! Your Turn.");
        log("Enemy ships detected. Open fire!");
        refreshBoard(machineGrid, gameController.getMachineBoard(), true);
    }

    /**
     * Handles click on player grid (ship placement).
     *
     * @param row The row clicked
     * @param col The column clicked
     */
    private void handlePlayerGridClick(int row, int col) {
        if (isGameStarted) return;

        Ship currentShip = gameController.getNextShipToPlace();
        if (currentShip == null) return;

        try {
            gameController.placeShip(new Coordinate(row, col), currentShip.getSize(), isHorizontalPlacement);
            refreshBoard(playerGrid, gameController.getPlayerBoard(), false);

            Ship next = gameController.getNextShipToPlace();
            if (next == null) {
                lblStatus.setText("Fleet Ready. Press START (or Space).");
                btnStart.setDisable(false);
                log("All ships placed. Ready for battle!");
            } else {
                lblStatus.setText("Place: " + next.getType() + " (" + next.getSize() + " cells)");
            }

        } catch (InvalidShipPlacementException e) {
            log("Warning: " + e.getMessage());
        }
    }

    /**
     * Handles click on machine grid (shooting).
     *
     * @param row The row clicked
     * @param col The column clicked
     */
    private void handleMachineGridClick(int row, int col) {
        if (!isGameStarted) {
            log("Game not started. Place ships first!");
            return;
        }

        if (!gameController.isPlayerTurn()) {
            log("Wait! Machine is thinking...");
            return;
        }

        boolean shotResult = gameController.shoot(new Coordinate(row, col));

        refreshBoard(machineGrid, gameController.getMachineBoard(), true);

        if (shotResult) {
            log("HIT! Shoot again.");
        } else {
            log("MISS! Enemy turn.");
            lblStatus.setText("Enemy Turn...");
        }

        checkGameOver();
    }

    /**
     * Checks if game is over and shows result.
     */
    private void checkGameOver() {
        if (gameController.isGameOver()) {
            isGameStarted = false;

            boolean playerWon = !gameController.getMachineBoard().getGrid()
                    .containsValue(Board.CellState.SHIP);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(playerWon ? "VICTORY!" : "DEFEAT!");
            alert.setContentText(playerWon ?
                    "Congratulations! You sank all enemy ships!" :
                    "Your fleet has been destroyed!");
            alert.show();

            log(playerWon ? "VICTORY! All enemy ships destroyed!" : "DEFEAT! Fleet destroyed!");
        }
    }

    /**
     * Refreshes the visual representation of a board.
     * Uses CellRenderer for enhanced 2D graphics.
     *
     * @param grid The GridPane to refresh
     * @param board The board data
     * @param hideShips Whether to hide ships (for enemy board)
     */
    private void refreshBoard(GridPane grid, Board board, boolean hideShips) {
        Map<Coordinate, Board.CellState> gridState = board.getGrid();

        for (Node node : grid.getChildren()) {
            if (!(node instanceof StackPane)) continue;

            Integer col = GridPane.getColumnIndex(node);
            Integer row = GridPane.getRowIndex(node);
            if (col == null || row == null) continue;

            StackPane cell = (StackPane) node;
            Coordinate coord = new Coordinate(row, col);
            Board.CellState state = gridState.getOrDefault(coord, Board.CellState.WATER);

            Object[] shipInfo = board.getShipRenderInfo(coord);
            CellRenderer.renderCell(cell, state, hideShips, shipInfo);
        }
    }

    /**
     * Logs a message to the text area.
     *
     * @param msg The message to log
     */
    private void log(String msg) {
        txtLog.appendText("> " + msg + "\n");
        txtLog.setScrollTop(Double.MAX_VALUE);
    }

    // GameObserver Implementation

    @Override
    public void onBoardChanged(boolean isPlayerBoard) {
        Platform.runLater(() -> {
            if (isPlayerBoard) {
                refreshBoard(playerGrid, gameController.getPlayerBoard(), false);
            } else {
                refreshBoard(machineGrid, gameController.getMachineBoard(), true);
            }
        });
    }

    @Override
    public void onShotFired(boolean isHit, boolean isSunk) {
        Platform.runLater(() -> {
            if (isSunk) {
                log("SUNK! Enemy ship destroyed!");
            } else if (isHit) {
                log("HIT! Part of enemy ship damaged.");
            }
        });
    }

    @Override
    public void onGameOver(boolean playerWon) {
        Platform.runLater(() -> {
            lblStatus.setText(playerWon ? "YOU WIN!" : "YOU LOSE!");
            checkGameOver();
        });
    }

    @Override
    public void onTurnChanged(boolean isPlayerTurn) {
        Platform.runLater(() -> {
            lblStatus.setText(isPlayerTurn ? "Your Turn!" : "Enemy Turn...");
        });
    }
}