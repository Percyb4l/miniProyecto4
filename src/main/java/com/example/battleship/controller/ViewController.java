package com.example.battleship.controller;

import com.example.battleship.exceptions.InvalidShipPlacementException;
import com.example.battleship.model.Board;
import com.example.battleship.model.Coordinate;
import com.example.battleship.model.Ship;
import com.example.battleship.patterns.GameObserver;
import com.example.battleship.util.ArchivoUtil;
import com.example.battleship.view.CellRenderer;
import com.example.battleship.view.ShipPreviewPanel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * View controller for the Battleship game interface.
 * Enhanced with ship preview, improved AI, and better UI layout.
 *
 * @author Battleship Team
 * @version 3.0
 * @since 2025-12-09
 */
public class ViewController implements Initializable, GameObserver {

    @FXML private GridPane playerGrid;
    @FXML private GridPane machineGrid;
    @FXML private Label lblStatus;
    @FXML private TextArea txtLog;
    @FXML private Button btnStart;
    @FXML private Button btnRotate;
    @FXML private Button btnPause;
    @FXML private Button btnMainMenu;
    @FXML private CheckBox chkShowEnemyShips;
    @FXML private VBox shipPreviewContainer; // NEW: Container for ship preview

    private GameController gameController;
    private boolean isHorizontalPlacement = true;
    private boolean isGameStarted = false;
    private boolean isPaused = false;
    private boolean showEnemyShips = false;

    private final int CELL_SIZE = 30;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameController = new GameController();
        gameController.addObserver(this);

        // Initialize grids
        initializeGrid(playerGrid, true);
        initializeGrid(machineGrid, false);

        // Get session data
        GameSession session = GameSession.getInstance();
        gameController.setPlayerNickname(session.getPlayerNickname());
        gameController.setDifficulty(session.getDifficulty());

        // Set callback for machine turn
        gameController.setOnMachineTurnFinished(() -> {
            refreshBoard(playerGrid, gameController.getPlayerBoard(), false);
            refreshBoard(machineGrid, gameController.getMachineBoard(), !showEnemyShips);
            lblStatus.setText(gameController.isGameOver() ? "GAME OVER" : "Your Turn!");
            checkGameOver();
        });

        // Check if continuing or new game
        if (!session.isNewGame() && ArchivoUtil.loadGame() != null) {
            boolean success = gameController.loadGameFromSave();
            if (success) {
                isGameStarted = true;
                btnStart.setDisable(true);
                btnRotate.setDisable(true);
                btnPause.setDisable(false);
                lblStatus.setText("Game Resumed. Fire away!");
                log("Game loaded successfully. Resume battle!");

                // Hide preview when game is loaded
                if (shipPreviewContainer != null) {
                    shipPreviewContainer.setVisible(false);
                    shipPreviewContainer.setManaged(false);
                }

                refreshBoard(playerGrid, gameController.getPlayerBoard(), false);
                refreshBoard(machineGrid, gameController.getMachineBoard(), !showEnemyShips);
            }
        } else {
            gameController.resetGame();
            isGameStarted = false;
            btnPause.setDisable(true);
            log("Welcome Admiral " + session.getPlayerNickname() + "! Place your ships.");
            lblStatus.setText("Place: Carrier (4 cells)");

            // Show initial preview
            updateShipPreview();
        }

        // Setup keyboard events
        Platform.runLater(() -> setupKeyboardEvents());

        // HU-3: Setup enemy ships visibility toggle
        if (chkShowEnemyShips != null) {
            chkShowEnemyShips.setOnAction(e -> {
                showEnemyShips = chkShowEnemyShips.isSelected();
                refreshBoard(machineGrid, gameController.getMachineBoard(), !showEnemyShips);
                log(showEnemyShips ? "⚠️ Enemy ships revealed (Verification Mode)" : "Enemy ships hidden");
            });
        }

        // Configure text area to show latest messages
        if (txtLog != null) {
            txtLog.textProperty().addListener((obs, oldVal, newVal) -> {
                txtLog.setScrollTop(Double.MAX_VALUE);
            });
        }
    }

    /**
     * Updates the ship preview panel with the current ship to place.
     */
    private void updateShipPreview() {
        if (shipPreviewContainer == null) return;

        Ship currentShip = gameController.getNextShipToPlace();
        if (currentShip == null) {
            shipPreviewContainer.setVisible(false);
            shipPreviewContainer.setManaged(false);
            return;
        }

        shipPreviewContainer.getChildren().clear();
        shipPreviewContainer.setVisible(true);
        shipPreviewContainer.setManaged(true);

        // Add title
        Label previewTitle = new Label("📦 Next Ship: " + currentShip.getType() +
                " (" + currentShip.getSize() + " cells)");
        previewTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: white; " +
                "-fx-font-size: 12px; -fx-padding: 5;");

        // Add orientation label
        Label orientationLabel = new Label("Orientation: " +
                (isHorizontalPlacement ? "Horizontal ↔️" : "Vertical ↕️"));
        orientationLabel.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 11px;");

        // Create preview grid
        GridPane preview = ShipPreviewPanel.createPreview(
                currentShip.getType(),
                currentShip.getSize(),
                isHorizontalPlacement
        );

        shipPreviewContainer.getChildren().addAll(previewTitle, orientationLabel, preview);
    }

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
                    if (isGameStarted && !isPaused) {
                        handlePause();
                    }
                    event.consume();
                } else if (event.getCode() == KeyCode.P) {
                    if (isGameStarted) {
                        handlePause();
                    }
                    event.consume();
                }
            });
        }
    }

    private void initializeGrid(GridPane grid, boolean isPlayer) {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(CELL_SIZE, CELL_SIZE);
                cell.setStyle("-fx-border-color: #7f8c8d; -fx-border-width: 0.5; -fx-background-color: #3498db;");

                int finalRow = row;
                int finalCol = col;

                cell.setOnMouseClicked(e -> {
                    if (!isPaused) {
                        if (isPlayer) handlePlayerGridClick(finalRow, finalCol);
                        else handleMachineGridClick(finalRow, finalCol);
                    }
                });

                grid.add(cell, col, row);
            }
        }
    }

    @FXML
    private void handleRotate() {
        if (isPaused) return;
        isHorizontalPlacement = !isHorizontalPlacement;
        btnRotate.setText("Rotate Ship (" + (isHorizontalPlacement ? "Horizontal" : "Vertical") + ")");
        log("Ship orientation: " + (isHorizontalPlacement ? "Horizontal ↔️" : "Vertical ↕️"));

        // Update preview
        updateShipPreview();
    }

    @FXML
    private void handleStartGame() {
        if (gameController.getNextShipToPlace() != null) {
            log("❌ You must place all ships first!");
            return;
        }
        isGameStarted = true;
        btnStart.setDisable(true);
        btnRotate.setDisable(true);
        btnPause.setDisable(false);

        // Hide preview container
        if (shipPreviewContainer != null) {
            shipPreviewContainer.setVisible(false);
            shipPreviewContainer.setManaged(false);
        }

        gameController.startNewGame();

        lblStatus.setText("⚔️ BATTLE STARTED! Your Turn.");
        log("🎯 Enemy ships detected. Open fire!");
        refreshBoard(machineGrid, gameController.getMachineBoard(), !showEnemyShips);
    }

    @FXML
    private void handlePause() {
        if (!isGameStarted) return;

        isPaused = !isPaused;

        if (isPaused) {
            showPauseDialog();
        }
    }

    private void showPauseDialog() {
        Alert pauseDialog = new Alert(Alert.AlertType.NONE);
        pauseDialog.setTitle("Game Paused");
        pauseDialog.setHeaderText("⏸️ GAME PAUSED ⏸️");

        ButtonType btnResume = new ButtonType("Resume", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnToMenu = new ButtonType("Main Menu", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType btnExit = new ButtonType("Exit Game", ButtonBar.ButtonData.OTHER);

        pauseDialog.getButtonTypes().setAll(btnResume, btnToMenu, btnExit);

        DialogPane dialogPane = pauseDialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #2c3e50;");
        dialogPane.lookup(".header-panel").setStyle("-fx-background-color: #34495e;");

        Label content = new Label("Game is paused.\nWhat would you like to do?");
        content.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        dialogPane.setContent(content);

        Optional<ButtonType> result = pauseDialog.showAndWait();

        if (result.isPresent()) {
            if (result.get() == btnResume) {
                isPaused = false;
                log("▶️ Game resumed");
            } else if (result.get() == btnToMenu) {
                gameController.saveGame();
                NavigationController.getInstance().showMainMenu();
            } else if (result.get() == btnExit) {
                Alert confirmExit = new Alert(Alert.AlertType.CONFIRMATION);
                confirmExit.setTitle("Exit Game");
                confirmExit.setHeaderText("Are you sure?");
                confirmExit.setContentText("Your progress will be saved.");

                Optional<ButtonType> exitResult = confirmExit.showAndWait();
                if (exitResult.isPresent() && exitResult.get() == ButtonType.OK) {
                    gameController.saveGame();
                    System.exit(0);
                } else {
                    isPaused = false;
                }
            }
        } else {
            isPaused = false;
        }
    }

    @FXML
    private void handleMainMenu() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Return to Menu");
        alert.setHeaderText("Return to Main Menu?");
        alert.setContentText("Your progress will be saved.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            gameController.saveGame();
            NavigationController.getInstance().showMainMenu();
        }
    }

    private void handlePlayerGridClick(int row, int col) {
        if (isGameStarted) return;

        Ship currentShip = gameController.getNextShipToPlace();
        if (currentShip == null) return;

        try {
            gameController.placeShip(new Coordinate(row, col), currentShip.getSize(), isHorizontalPlacement);
            refreshBoard(playerGrid, gameController.getPlayerBoard(), false);

            Ship next = gameController.getNextShipToPlace();
            if (next == null) {
                lblStatus.setText("✅ Fleet Ready. Press START (or Space).");
                btnStart.setDisable(false);
                log("✅ All ships placed. Ready for battle!");
            } else {
                lblStatus.setText("Place: " + next.getType() + " (" + next.getSize() + " cells)");
            }

            // Update preview for next ship
            updateShipPreview();

        } catch (InvalidShipPlacementException e) {
            log("⚠️ Warning: " + e.getMessage());
        }
    }

    private void handleMachineGridClick(int row, int col) {
        if (!isGameStarted) {
            log("❌ Game not started. Place ships first!");
            return;
        }

        if (!gameController.isPlayerTurn()) {
            log("⏳ Wait! Machine is thinking...");
            return;
        }

        boolean shotResult = gameController.shoot(new Coordinate(row, col));

        refreshBoard(machineGrid, gameController.getMachineBoard(), !showEnemyShips);

        if (shotResult) {
            log("💥 HIT! Shoot again.");
        } else {
            log("💧 MISS! Enemy turn.");
            lblStatus.setText("🤖 Enemy Turn...");
        }

        checkGameOver();
    }

    private void checkGameOver() {
        if (gameController.isGameOver()) {
            isGameStarted = false;

            boolean playerWon = !gameController.getMachineBoard().getGrid()
                    .containsValue(Board.CellState.SHIP);

            int enemyShipsDestroyed = countSunkShips(gameController.getMachineBoard());
            GameSession.getInstance().setEnemyShipsDestroyed(enemyShipsDestroyed);

            log(playerWon ? "🎉 VICTORY! All enemy ships destroyed!" : "💀 DEFEAT! Fleet destroyed!");

            Platform.runLater(() -> {
                NavigationController.getInstance().showGameOver(playerWon, enemyShipsDestroyed);
            });
        }
    }

    private int countSunkShips(Board board) {
        return (int) board.getShipPlacement().values().stream()
                .distinct()
                .filter(Ship::isSunk)
                .count();
    }

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

    private void log(String msg) {
        if (txtLog != null) {
            txtLog.appendText("> " + msg + "\n");
        }
    }

    // GameObserver Implementation

    @Override
    public void onBoardChanged(boolean isPlayerBoard) {
        Platform.runLater(() -> {
            if (isPlayerBoard) {
                refreshBoard(playerGrid, gameController.getPlayerBoard(), false);
            } else {
                refreshBoard(machineGrid, gameController.getMachineBoard(), !showEnemyShips);
            }
        });
    }

    @Override
    public void onShotFired(boolean isHit, boolean isSunk) {
        Platform.runLater(() -> {
            if (isSunk) {
                log("💥 SUNK! Enemy ship destroyed!");
            } else if (isHit) {
                log("🎯 HIT! Part of enemy ship damaged.");
            }
        });
    }

    @Override
    public void onGameOver(boolean playerWon) {
        Platform.runLater(() -> {
            lblStatus.setText(playerWon ? "🎉 YOU WIN!" : "💀 YOU LOSE!");
            checkGameOver();
        });
    }

    @Override
    public void onTurnChanged(boolean isPlayerTurn) {
        Platform.runLater(() -> {
            lblStatus.setText(isPlayerTurn ? "⚔️ Your Turn!" : "🤖 Enemy Turn...");
        });
    }
}