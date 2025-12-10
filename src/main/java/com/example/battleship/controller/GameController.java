package com.example.battleship.controller;

import com.example.battleship.exceptions.InvalidShipPlacementException;
import com.example.battleship.interfaces.IBattleShipGame;
import com.example.battleship.model.Board;
import com.example.battleship.model.Coordinate;
import com.example.battleship.model.Ship;
import com.example.battleship.patterns.GameObserver;
import com.example.battleship.patterns.ShootingStrategy;
import com.example.battleship.util.ArchivoUtil;
import javafx.application.Platform;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Main controller for the Battleship game.
 * Handles game logic, AI turns, persistence, and observer notifications.
 * Implements MVC architecture and Observer/Strategy patterns.
 *
 * @author Battleship Team
 * @version 1.0
 * @since 2025-12-07
 */
public class GameController implements IBattleShipGame {

    private Board playerBoard;
    private Board machineBoard;
    private boolean isPlayerTurn;
    private Queue<Ship> shipsToPlace;
    private Random random;
    private String playerNickname = "Jugador 1";
    private Runnable onMachineTurnFinished;

    // Observer Pattern: List of observers
    private List<GameObserver> observers;

    // Strategy Pattern: AI shooting strategy
    private ShootingStrategy shootingStrategy;

    /**
     * Constructs a new GameController with default settings.
     * Initializes boards, fleet, and default AI strategy.
     */
    public GameController() {
        this.random = new Random();
        this.shipsToPlace = new LinkedList<>();
        this.observers = new ArrayList<>();
        this.playerBoard = new Board();
        this.machineBoard = new Board();
        this.isPlayerTurn = true;

        // Default strategy: Random (Easy)
        this.shootingStrategy = new com.example.battleship.patterns.RandomStrategy();

        initializeFleet();
    }

    /**
     * Loads a saved game from persistent storage.
     *
     * @return true if game loaded successfully, false otherwise
     */
    public boolean loadGameFromSave() {
        Object[] loadedData = ArchivoUtil.loadGame();
        if (loadedData != null) {
            this.playerBoard = (Board) loadedData[0];
            this.machineBoard = (Board) loadedData[1];
            this.isPlayerTurn = true; // Always player's turn after loading
            this.shipsToPlace.clear();

            notifyBoardChanged(true);
            notifyBoardChanged(false);

            return true;
        }
        return false;
    }

    /**
     * Resets the game to initial state.
     * Clears boards and reinitializes fleet.
     */
    public void resetGame() {
        this.playerBoard = new Board();
        this.machineBoard = new Board();
        this.isPlayerTurn = true;
        this.shipsToPlace.clear();
        initializeFleet();
    }

    /**
     * Sets the player's nickname for score tracking.
     *
     * @param nickname The player's nickname
     */
    public void setPlayerNickname(String nickname) {
        this.playerNickname = nickname;
    }

    /**
     * Sets callback for when machine turn finishes.
     *
     * @param callback The callback to execute
     */
    public void setOnMachineTurnFinished(Runnable callback) {
        this.onMachineTurnFinished = callback;
    }

    /**
     * Sets the AI difficulty level.
     * IMPROVED: Resets strategy state when changing difficulty.
     *
     * @param difficulty "EASY", "MEDIUM", or "HARD"
     */
    public void setDifficulty(String difficulty) {
        switch (difficulty.toUpperCase()) {
            case "EASY":
                shootingStrategy = new com.example.battleship.patterns.RandomStrategy();
                System.out.println("🟢 AI Difficulty: EASY (Random shooting)");
                break;
            case "MEDIUM":
                shootingStrategy = new com.example.battleship.patterns.SmartStrategy();
                System.out.println("🟡 AI Difficulty: MEDIUM (Targets after hit)");
                break;
            case "HARD":
                shootingStrategy = new com.example.battleship.patterns.HuntTargetStrategy();
                System.out.println("🔴 AI Difficulty: HARD (Hunt & Target - Aggressive pursuit)");
                break;
            default:
                shootingStrategy = new com.example.battleship.patterns.RandomStrategy();
        }
    }

    /**
     * Initializes the fleet of ships to be placed.
     */
    private void initializeFleet() {
        shipsToPlace.add(new Ship("Carrier", 4));
        shipsToPlace.add(new Ship("Submarine", 3));
        shipsToPlace.add(new Ship("Submarine", 3));
        shipsToPlace.add(new Ship("Destroyer", 2));
        shipsToPlace.add(new Ship("Destroyer", 2));
        shipsToPlace.add(new Ship("Destroyer", 2));
        shipsToPlace.add(new Ship("Frigate", 1));
        shipsToPlace.add(new Ship("Frigate", 1));
        shipsToPlace.add(new Ship("Frigate", 1));
        shipsToPlace.add(new Ship("Frigate", 1));
    }

    /**
     * Gets the next ship to be placed.
     *
     * @return The next ship, or null if all ships are placed
     */
    public Ship getNextShipToPlace() {
        return shipsToPlace.peek();
    }

    @Override
    public void startNewGame() {
        placeMachineShipsRandomly();
        saveGame();
        notifyBoardChanged(false);
    }

    @Override
    public void placeShip(Coordinate start, int length, boolean isHorizontal) throws InvalidShipPlacementException {
        if (shipsToPlace.isEmpty()) {
            throw new InvalidShipPlacementException("All ships are already placed!");
        }
        Ship shipToPlace = shipsToPlace.peek();
        playerBoard.placeShip(shipToPlace, start, isHorizontal);
        shipsToPlace.poll();

        notifyBoardChanged(true);
    }

    @Override
    public boolean shoot(Coordinate target) {
        if (!isPlayerTurn) return false;

        Board.CellState state = machineBoard.getGrid().get(target);
        if (state != Board.CellState.WATER && state != Board.CellState.SHIP) {
            return false;
        }

        boolean hit = processShot(machineBoard, target);

        // Notify observers about the shot
        Ship ship = machineBoard.getShipPlacement().get(target);
        boolean isSunk = ship != null && ship.isSunk();
        notifyShotFired(hit, isSunk);
        notifyBoardChanged(false);

        saveGame();

        if (!hit) {
            isPlayerTurn = false;
            notifyTurnChanged(false);
            startMachineTurn();
        }

        return hit;
    }

    /**
     * Processes a shot on the given board.
     *
     * @param board The board to shoot at
     * @param target The target coordinate
     * @return true if hit, false if miss
     */
    private boolean processShot(Board board, Coordinate target) {
        Board.CellState currentState = board.getGrid().get(target);
        if (currentState == Board.CellState.SHIP) {
            board.getGrid().put(target, Board.CellState.HIT);
            Ship ship = board.getShipPlacement().get(target);
            if (ship != null) {
                ship.registerHit();
                if (ship.isSunk()) markShipAsSunk(board, ship);
            }
            return true;
        } else if (currentState == Board.CellState.WATER) {
            board.getGrid().put(target, Board.CellState.MISS);
            return false;
        }
        return false;
    }

    /**
     * Marks all coordinates of a sunk ship.
     *
     * @param board The board containing the ship
     * @param ship The ship that was sunk
     */
    private void markShipAsSunk(Board board, Ship ship) {
        for (Coordinate coord : ship.getCoordinates()) {
            board.getGrid().put(coord, Board.CellState.SUNK);
        }
    }

    /**
     * Starts the machine's turn in a separate thread.
     * IMPROVED: Now notifies the AI strategy about hits and sinks.
     */
    private void startMachineTurn() {
        Thread machineThread = new Thread(() -> {
            try {
                Thread.sleep(1000);

                boolean turnEnded = false;
                while (!turnEnded && !isGameOver()) {
                    // Use strategy pattern for shot selection
                    Coordinate target = shootingStrategy.getNextShot(playerBoard);

                    Board.CellState state = playerBoard.getGrid().get(target);
                    if (state == Board.CellState.HIT || state == Board.CellState.MISS || state == Board.CellState.SUNK) {
                        continue;
                    }

                    boolean hit = processShot(playerBoard, target);

                    // IMPROVED: Notify AI about the result
                    if (hit) {
                        Ship ship = playerBoard.getShipPlacement().get(target);
                        boolean isSunk = ship != null && ship.isSunk();

                        // Notify strategy about the hit
                        if (shootingStrategy instanceof com.example.battleship.patterns.HuntTargetStrategy) {
                            ((com.example.battleship.patterns.HuntTargetStrategy) shootingStrategy).registerHit(target);

                            if (isSunk && ship != null) {
                                ((com.example.battleship.patterns.HuntTargetStrategy) shootingStrategy).registerSunk(ship.getCoordinates());
                            }
                        } else if (shootingStrategy instanceof com.example.battleship.patterns.SmartStrategy) {
                            ((com.example.battleship.patterns.SmartStrategy) shootingStrategy).registerHit(target);
                        }
                    }

                    // Notify observers
                    Ship ship = playerBoard.getShipPlacement().get(target);
                    boolean isSunk = ship != null && ship.isSunk();
                    Platform.runLater(() -> {
                        notifyShotFired(hit, isSunk);
                        notifyBoardChanged(true);
                    });

                    saveGame();

                    Platform.runLater(() -> {
                        if (onMachineTurnFinished != null) onMachineTurnFinished.run();
                    });

                    if (hit) {
                        Thread.sleep(800);
                    } else {
                        turnEnded = true;
                    }
                }

                if (isGameOver()) {
                    boolean playerWon = machineBoard.getGrid().values().stream()
                            .noneMatch(state -> state == Board.CellState.SHIP);
                    Platform.runLater(() -> notifyGameOver(playerWon));
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                isPlayerTurn = true;
                Platform.runLater(() -> notifyTurnChanged(true));
            }
        });

        machineThread.setDaemon(true);
        machineThread.start();
    }

    /**
     * Places machine ships randomly on the board.
     */
    private void placeMachineShipsRandomly() {
        Queue<Ship> machineFleet = new LinkedList<>();
        machineFleet.add(new Ship("Carrier", 4));
        machineFleet.add(new Ship("Submarine", 3));
        machineFleet.add(new Ship("Submarine", 3));
        machineFleet.add(new Ship("Destroyer", 2));
        machineFleet.add(new Ship("Destroyer", 2));
        machineFleet.add(new Ship("Destroyer", 2));
        machineFleet.add(new Ship("Frigate", 1));
        machineFleet.add(new Ship("Frigate", 1));
        machineFleet.add(new Ship("Frigate", 1));
        machineFleet.add(new Ship("Frigate", 1));

        while (!machineFleet.isEmpty()) {
            Ship ship = machineFleet.peek();
            boolean placed = false;
            while (!placed) {
                int row = random.nextInt(10);
                int col = random.nextInt(10);
                boolean horizontal = random.nextBoolean();
                try {
                    Ship newShipInstance = new Ship(ship.getType(), ship.getSize());
                    machineBoard.placeShip(newShipInstance, new Coordinate(row, col), horizontal);
                    placed = true;
                    machineFleet.poll();
                } catch (InvalidShipPlacementException e) {}
            }
        }
    }

    @Override
    public boolean isGameOver() {
        boolean playerAlive = playerBoard.getGrid().containsValue(Board.CellState.SHIP);
        boolean machineAlive = machineBoard.getGrid().containsValue(Board.CellState.SHIP);
        return !playerAlive || !machineAlive;
    }

    @Override
    public void saveGame() {
        ArchivoUtil.saveGame(playerBoard, machineBoard, isPlayerTurn);
        int enemyShipsSunk = countSunkShips(machineBoard);
        ArchivoUtil.saveScore(playerNickname, enemyShipsSunk);
    }

    @Override
    public void loadGame() {}

    /**
     * Counts the number of sunk ships on a board.
     *
     * @param board The board to count from
     * @return Number of sunk ships
     */
    private int countSunkShips(Board board) {
        return (int) board.getShipPlacement().values().stream()
                .distinct()
                .filter(Ship::isSunk)
                .count();
    }

    // Observer Pattern Methods

    /**
     * Adds an observer to be notified of game events.
     *
     * @param observer The observer to add
     */
    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    /**
     * Removes an observer from notifications.
     *
     * @param observer The observer to remove
     */
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifies observers that a board has changed.
     *
     * @param isPlayerBoard true for player board, false for machine
     */
    private void notifyBoardChanged(boolean isPlayerBoard) {
        for (GameObserver observer : observers) {
            observer.onBoardChanged(isPlayerBoard);
        }
    }

    /**
     * Notifies observers that a shot was fired.
     *
     * @param isHit true if hit, false if miss
     * @param isSunk true if ship was sunk
     */
    private void notifyShotFired(boolean isHit, boolean isSunk) {
        for (GameObserver observer : observers) {
            observer.onShotFired(isHit, isSunk);
        }
    }

    /**
     * Notifies observers that the game is over.
     *
     * @param playerWon true if player won, false if machine won
     */
    private void notifyGameOver(boolean playerWon) {
        for (GameObserver observer : observers) {
            observer.onGameOver(playerWon);
        }
    }

    /**
     * Notifies observers that the turn has changed.
     *
     * @param isPlayerTurn true if player's turn, false for machine
     */
    private void notifyTurnChanged(boolean isPlayerTurn) {
        for (GameObserver observer : observers) {
            observer.onTurnChanged(isPlayerTurn);
        }
    }

    // Getters

    public Board getPlayerBoard() { return playerBoard; }
    public Board getMachineBoard() { return machineBoard; }
    public boolean isPlayerTurn() { return isPlayerTurn; }
}