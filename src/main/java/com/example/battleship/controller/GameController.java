package com.example.battleship.controller;

import com.example.battleship.exceptions.InvalidShipPlacementException;
import com.example.battleship.interfaces.IBattleShipGame;
import com.example.battleship.model.Board;
import com.example.battleship.model.Coordinate;
import com.example.battleship.model.Ship;
import com.example.battleship.util.ArchivoUtil;
import javafx.application.Platform;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

    public class GameController implements IBattleShipGame {

        private Board playerBoard;
        private Board machineBoard;
        private boolean isPlayerTurn;
        private Queue<Ship> shipsToPlace;
        private Random random;
        private String playerNickname = "Jugador 1";
        private Runnable onMachineTurnFinished;

        public GameController() {
            this.random = new Random();
            this.shipsToPlace = new LinkedList<>();
            // YA NO CARGAMOS AQUÍ AUTOMÁTICAMENTE
            // Inicializamos vacío por defecto
            this.playerBoard = new Board();
            this.machineBoard = new Board();
            this.isPlayerTurn = true;
            initializeFleet();
        }

        // NUEVO MÉTODO: Cargar partida explícitamente
        public boolean loadGameFromSave() {
            Object[] loadedData = ArchivoUtil.loadGame();
            if (loadedData != null) {
                this.playerBoard = (Board) loadedData[0];
                this.machineBoard = (Board) loadedData[1];

                // CORRECCIÓN CRÍTICA:
                // Forzamos que sea turno del jugador al cargar.
                // Esto evita el estado "Zombie" donde el juego espera a una máquina que no está corriendo.
                this.isPlayerTurn = true;

                this.shipsToPlace.clear(); // Limpiamos la cola porque el juego ya empezó
                return true; // Carga exitosa
            }
            return false; // No había archivo
        }

        // NUEVO MÉTODO: Reiniciar partida desde cero
        public void resetGame() {
            this.playerBoard = new Board();
            this.machineBoard = new Board();
            this.isPlayerTurn = true;
            this.shipsToPlace.clear();
            initializeFleet();
            // Borrar archivo anterior para no confundir (opcional)
            // new File("battleship_data/game.ser").delete();
        }

        public void setPlayerNickname(String nickname) {
            this.playerNickname = nickname;
        }

        public void setOnMachineTurnFinished(Runnable callback) {
            this.onMachineTurnFinished = callback;
        }

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

        public Ship getNextShipToPlace() {
            return shipsToPlace.peek();
        }

        @Override
        public void startNewGame() {
            placeMachineShipsRandomly();
            saveGame();
        }

        @Override
        public void placeShip(Coordinate start, int length, boolean isHorizontal) throws InvalidShipPlacementException {
            if (shipsToPlace.isEmpty()) {
                throw new InvalidShipPlacementException("All ships are already placed!");
            }
            Ship shipToPlace = shipsToPlace.peek();
            playerBoard.placeShip(shipToPlace, start, isHorizontal);
            shipsToPlace.poll();
        }

        @Override
        public boolean shoot(Coordinate target) {
            if (!isPlayerTurn) return false;

            Board.CellState state = machineBoard.getGrid().get(target);
            if (state != Board.CellState.WATER && state != Board.CellState.SHIP) {
                return false;
            }

            boolean hit = processShot(machineBoard, target);
            saveGame();

            if (!hit) {
                isPlayerTurn = false;
                startMachineTurn();
            }
            return hit;
        }

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

        private void markShipAsSunk(Board board, Ship ship) {
            for (Coordinate coord : ship.getCoordinates()) {
                board.getGrid().put(coord, Board.CellState.SUNK);
            }
        }

        private void startMachineTurn() {
            Thread machineThread = new Thread(() -> {
                try {
                    Thread.sleep(1000); // Pequeña pausa inicial

                    boolean turnEnded = false;
                    // Mientras sea turno de la IA y el juego no haya acabado
                    while (!turnEnded && !isGameOver()) {
                        int row = random.nextInt(10);
                        int col = random.nextInt(10);
                        Coordinate target = new Coordinate(row, col);

                        // Validar disparo (no repetir celda)
                        Board.CellState state = playerBoard.getGrid().get(target);
                        if (state == Board.CellState.HIT || state == Board.CellState.MISS || state == Board.CellState.SUNK) {
                            continue; // Reintentar otro disparo en el bucle
                        }

                        // Realizar disparo
                        boolean hit = processShot(playerBoard, target);
                        saveGame();

                        // Actualizar interfaz gráfica
                        Platform.runLater(() -> {
                            if (onMachineTurnFinished != null) onMachineTurnFinished.run();
                        });

                        if (hit) {
                            Thread.sleep(800); // Si acierta, espera y dispara de nuevo
                        } else {
                            turnEnded = true; // Si falla, termina su turno
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // SALVVIDAS: Pase lo que pase (error o fin de turno),
                    // aseguramos que el jugador recupere el control.
                    isPlayerTurn = true;
                }
            });

            machineThread.setDaemon(true);
            machineThread.start();
        }

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

        @Override public void loadGame() {}

        private int countSunkShips(Board board) {
            return (int) board.getShipPlacement().values().stream().distinct().filter(Ship::isSunk).count();
        }

        public Board getPlayerBoard() { return playerBoard; }
        public Board getMachineBoard() { return machineBoard; }
        public boolean isPlayerTurn() { return isPlayerTurn; }
    }