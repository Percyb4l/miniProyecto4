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
    private String playerNickname = "Jugador 1"; // Default nickname

    // Callback para notificar a la vista (View) cuando la máquina termine su turno
    private Runnable onMachineTurnFinished;

    public GameController() {
        this.random = new Random();
        this.shipsToPlace = new LinkedList<>();

        // HU-5: Intentar cargar partida guardada automáticamente
        Object[] loadedData = ArchivoUtil.loadGame();

        if (loadedData != null) {
            System.out.println("Save file found. Loading game...");
            this.playerBoard = (Board) loadedData[0];
            this.machineBoard = (Board) loadedData[1];
            this.isPlayerTurn = (Boolean) loadedData[2];
            // Si cargamos una partida, asumimos que la fase de colocación ya terminó.
            // La cola shipsToPlace se queda vacía.
        } else {
            System.out.println("No save file found. Starting new game.");
            this.playerBoard = new Board();
            this.machineBoard = new Board();
            this.isPlayerTurn = true;
            initializeFleet(); // Llenar la cola de barcos para colocar
        }
    }

    public void setPlayerNickname(String nickname) {
        this.playerNickname = nickname;
    }

    public void setOnMachineTurnFinished(Runnable callback) {
        this.onMachineTurnFinished = callback;
    }

    private void initializeFleet() {
        // Orden de la flota según HU-1
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
        // Al iniciar el combate, la máquina coloca sus barcos
        placeMachineShipsRandomly();
        saveGame(); // Guardado inicial
    }

    @Override
    public void placeShip(Coordinate start, int length, boolean isHorizontal) throws InvalidShipPlacementException {
        if (shipsToPlace.isEmpty()) {
            throw new InvalidShipPlacementException("All ships are already placed!");
        }

        Ship shipToPlace = shipsToPlace.peek();
        // Intentar colocar en el tablero del jugador
        playerBoard.placeShip(shipToPlace, start, isHorizontal);

        // Si tiene éxito, lo sacamos de la cola
        shipsToPlace.poll();
    }

    @Override
    public boolean shoot(Coordinate target) {
        if (!isPlayerTurn) return false;

        // Validar si ya se disparó ahí (evitar gastar turno en celda repetida)
        Board.CellState state = machineBoard.getGrid().get(target);
        if (state != Board.CellState.WATER && state != Board.CellState.SHIP) {
            return false;
        }

        boolean hit = processShot(machineBoard, target);

        // HU-5: Guardar estado tras jugada del humano
        saveGame();

        // Si falló (Agua), cambio de turno a la máquina
        if (!hit) {
            isPlayerTurn = false;
            startMachineTurn();
        }

        return hit;
    }

    /**
     * Lógica central de disparo: actualiza el tablero y verifica hundimientos.
     */
    private boolean processShot(Board board, Coordinate target) {
        Board.CellState currentState = board.getGrid().get(target);

        if (currentState == Board.CellState.SHIP) {
            board.getGrid().put(target, Board.CellState.HIT);
            // Buscar el objeto barco en esa coordenada
            Ship ship = board.getShipPlacement().get(target);
            if (ship != null) {
                ship.registerHit();
                if (ship.isSunk()) {
                    markShipAsSunk(board, ship);
                }
            }
            return true; // Fue un acierto
        } else if (currentState == Board.CellState.WATER) {
            board.getGrid().put(target, Board.CellState.MISS);
            return false; // Fue agua
        }
        return false;
    }

    private void markShipAsSunk(Board board, Ship ship) {
        for (Coordinate coord : ship.getCoordinates()) {
            board.getGrid().put(coord, Board.CellState.SUNK);
        }
    }

    /**
     * Maneja el turno de la IA en un hilo separado.
     */
    private void startMachineTurn() {
        Thread machineThread = new Thread(() -> {
            try {
                // Simular "pensamiento"
                Thread.sleep(1000);

                boolean turnEnded = false;
                while (!turnEnded && !isGameOver()) {
                    // IA Básica: Disparo aleatorio
                    int row = random.nextInt(10);
                    int col = random.nextInt(10);
                    Coordinate target = new Coordinate(row, col);

                    // Verificar si ya disparó ahí antes
                    Board.CellState state = playerBoard.getGrid().get(target);
                    if (state == Board.CellState.HIT || state == Board.CellState.MISS || state == Board.CellState.SUNK) {
                        continue; // Intentar otra coordenada
                    }

                    boolean hit = processShot(playerBoard, target);

                    // HU-5: Guardar estado tras jugada de la máquina
                    saveGame();

                    // Actualizar UI en el hilo de JavaFX
                    Platform.runLater(() -> {
                        if (onMachineTurnFinished != null) onMachineTurnFinished.run();
                    });

                    // Si acierta, dispara de nuevo tras una pausa. Si falla, termina turno.
                    if (hit) {
                        Thread.sleep(800);
                    } else {
                        turnEnded = true;
                        isPlayerTurn = true;
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        machineThread.setDaemon(true); // El hilo muere si cierras la app
        machineThread.start();
    }

    /**
     * Coloca los barcos de la máquina aleatoriamente (HU-4).
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
                    // Usamos una nueva instancia del barco para el tablero de la máquina
                    Ship newShipInstance = new Ship(ship.getType(), ship.getSize());
                    machineBoard.placeShip(newShipInstance, new Coordinate(row, col), horizontal);
                    placed = true;
                    machineFleet.poll();
                } catch (InvalidShipPlacementException e) {
                    // Reintentar si se superpone o sale del mapa
                }
            }
        }
    }

    @Override
    public boolean isGameOver() {
        // Verificar si quedan celdas tipo SHIP en los tableros
        boolean playerAlive = playerBoard.getGrid().containsValue(Board.CellState.SHIP);
        boolean machineAlive = machineBoard.getGrid().containsValue(Board.CellState.SHIP);
        return !playerAlive || !machineAlive;
    }

    // --------------------------------------------------------
    // HU-5: Implementación de Persistencia
    // --------------------------------------------------------

    @Override
    public void saveGame() {
        // 1. Guardar Binario (.ser) para recuperar el estado exacto
        ArchivoUtil.saveGame(playerBoard, machineBoard, isPlayerTurn);

        // 2. Guardar Plano (.txt) con estadísticas requeridas
        int enemyShipsSunk = countSunkShips(machineBoard);
        ArchivoUtil.saveScore(playerNickname, enemyShipsSunk);
    }

    @Override
    public void loadGame() {
        // Este método se maneja en el constructor para cargar al inicio,
        // pero se mantiene por la interfaz.
    }

    /**
     * Cuenta cuántos barcos únicos han sido hundidos en un tablero dado.
     */
    private int countSunkShips(Board board) {
        // Filtramos por objetos distintos para no contar el mismo barco varias veces
        return (int) board.getShipPlacement().values().stream()
                .distinct()
                .filter(Ship::isSunk)
                .count();
    }

    // Getters para la Vista
    public Board getPlayerBoard() { return playerBoard; }
    public Board getMachineBoard() { return machineBoard; }
    public boolean isPlayerTurn() { return isPlayerTurn; }
}