package com.example.battleship.controller;

import com.example.battleship.exceptions.InvalidShipPlacementException;
import com.example.battleship.model.Board;
import com.example.battleship.model.Coordinate;
import com.example.battleship.model.Ship;
import com.example.battleship.util.ArchivoUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class ViewController implements Initializable {

    @FXML private GridPane playerGrid;
    @FXML private GridPane machineGrid;
    @FXML private Label lblStatus;
    @FXML private TextArea txtLog;
    @FXML private Button btnStart;
    @FXML private Button btnRotate;

    private GameController gameController;
    private boolean isHorizontalPlacement = true;
    private boolean isGameStarted = false; // Estado importante

    private final int CELL_SIZE = 30;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameController = new GameController();

        // Inicializar cuadrículas visuales primero
        initializeGrid(playerGrid, true);
        initializeGrid(machineGrid, false);

        // Configurar callback de la IA
        gameController.setOnMachineTurnFinished(() -> {
            refreshBoard(playerGrid, gameController.getPlayerBoard(), false);
            refreshBoard(machineGrid, gameController.getMachineBoard(), true);
            lblStatus.setText(gameController.isGameOver() ? "GAME OVER" : "Your Turn!");
            checkGameOver();
        });

        // -------------------------------------------------------------
        // LOGICA DE PREGUNTA AL INICIAR (HU-5)
        // -------------------------------------------------------------
        // Verificamos si existe archivo sin cargarlo aún
        if (ArchivoUtil.loadGame() != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Partida Guardada");
            alert.setHeaderText("Se ha encontrado una partida guardada.");
            alert.setContentText("¿Quieres seguir con esa o empezar una nueva?");

            ButtonType buttonTypeContinue = new ButtonType("Seguir con esa");
            ButtonType buttonTypeNew = new ButtonType("Empezar Nueva");
            alert.getButtonTypes().setAll(buttonTypeContinue, buttonTypeNew);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == buttonTypeContinue) {
                // Opción 1: Cargar partida
                boolean success = gameController.loadGameFromSave();
                if (success) {
                    isGameStarted = true; // IMPORTANTÍSIMO: Desbloquea los disparos
                    btnStart.setDisable(true);
                    btnRotate.setDisable(true);
                    lblStatus.setText("Partida Reanudada. ¡Dispara!");
                    log("Game loaded successfully. Resume battle!");

                    // Repintar tableros con el estado cargado
                    refreshBoard(playerGrid, gameController.getPlayerBoard(), false);
                    refreshBoard(machineGrid, gameController.getMachineBoard(), true);
                }
            } else {
                // Opción 2: Nueva partida
                gameController.resetGame();
                isGameStarted = false; // Modo colocación
                log("New game started. Place your fleet.");
                lblStatus.setText("Place: Carrier (4 cells)");
            }
        } else {
            // No hay partida guardada, flujo normal
            log("Welcome Admiral! Place your ships.");
            lblStatus.setText("Place: Carrier (4 cells)");
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
                    if (isPlayer) handlePlayerGridClick(finalRow, finalCol);
                    else handleMachineGridClick(finalRow, finalCol);
                });

                grid.add(cell, col, row);
            }
        }
    }

    @FXML
    private void handleRotate() {
        isHorizontalPlacement = !isHorizontalPlacement;
        btnRotate.setText("Rotate Ship (" + (isHorizontalPlacement ? "Horizontal" : "Vertical") + ")");
    }

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

    private void handlePlayerGridClick(int row, int col) {
        if (isGameStarted) return; // Si ya empezó, no deja poner barcos

        Ship currentShip = gameController.getNextShipToPlace();
        if (currentShip == null) return;

        try {
            gameController.placeShip(new Coordinate(row, col), currentShip.getSize(), isHorizontalPlacement);
            refreshBoard(playerGrid, gameController.getPlayerBoard(), false);

            Ship next = gameController.getNextShipToPlace();
            if (next == null) {
                lblStatus.setText("Fleet Ready. Press START.");
                btnStart.setDisable(false);
                log("All ships placed.");
            } else {
                lblStatus.setText("Place: " + next.getType() + " (" + next.getSize() + ")");
            }

        } catch (InvalidShipPlacementException e) {
            log("Warning: " + e.getMessage());
        }
    }

    private void handleMachineGridClick(int row, int col) {
        // Bloqueo si no ha empezado el juego
        if (!isGameStarted) {
            log("Game not started or in placement phase.");
            return;
        }

        // Bloqueo si es turno de la máquina
        if (!gameController.isPlayerTurn()) {
            log("Wait! Machine is thinking...");
            return;
        }

        boolean shotResult = gameController.shoot(new Coordinate(row, col));

        refreshBoard(machineGrid, gameController.getMachineBoard(), true);

        if (shotResult) log("HIT! Shoot again.");
        else {
            log("MISS! Enemy turn.");
            lblStatus.setText("Enemy Turn...");
        }

        checkGameOver();
    }

    private void checkGameOver() {
        if (gameController.isGameOver()) {
            isGameStarted = false;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText("The battle has ended!");
            alert.show();
        }
    }

    private void refreshBoard(GridPane grid, Board board, boolean hideShips) {
        Map<Coordinate, Board.CellState> gridState = board.getGrid();

        for (Node node : grid.getChildren()) {
            if (!(node instanceof StackPane)) continue;

            Integer col = GridPane.getColumnIndex(node);
            Integer row = GridPane.getRowIndex(node);
            if (col == null || row == null) continue;

            StackPane cell = (StackPane) node;
            cell.getChildren().clear();

            Coordinate coord = new Coordinate(row, col);
            Board.CellState state = gridState.getOrDefault(coord, Board.CellState.WATER);

            switch (state) {
                case SHIP:
                    if (!hideShips) {
                        Rectangle ship = new Rectangle(CELL_SIZE - 4, CELL_SIZE - 4, Color.DARKGRAY);
                        ship.setArcWidth(10); ship.setArcHeight(10);
                        cell.getChildren().add(ship);
                    }
                    break;
                case HIT:
                    Circle fire = new Circle(CELL_SIZE / 3, Color.ORANGERED);
                    cell.getChildren().add(fire);
                    if (!hideShips) {
                        Rectangle brokenShip = new Rectangle(CELL_SIZE - 4, CELL_SIZE - 4, Color.DARKGRAY);
                        brokenShip.setOpacity(0.5);
                        cell.getChildren().add(0, brokenShip);
                    }
                    break;
                case SUNK:
                    Rectangle sunk = new Rectangle(CELL_SIZE - 4, CELL_SIZE - 4, Color.BLACK);
                    Line x1 = new Line(-10, -10, 10, 10); x1.setStroke(Color.RED);
                    Line x2 = new Line(10, -10, -10, 10); x2.setStroke(Color.RED);
                    StackPane sunkGroup = new StackPane(sunk, x1, x2);
                    cell.getChildren().add(sunkGroup);
                    break;
                case MISS:
                    Line m1 = new Line(-8, -8, 8, 8); m1.setStroke(Color.WHITE); m1.setStrokeWidth(2);
                    Line m2 = new Line(8, -8, -8, 8); m2.setStroke(Color.WHITE); m2.setStrokeWidth(2);
                    cell.getChildren().addAll(m1, m2);
                    break;
                default: break;
            }
        }
    }

    private void log(String msg) {
        txtLog.appendText("> " + msg + "\n");
        txtLog.setScrollTop(Double.MAX_VALUE);
    }
}