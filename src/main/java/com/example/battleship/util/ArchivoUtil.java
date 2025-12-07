package com.example.battleship.util;

import com.example.battleship.model.Board;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Clase encargada de la persistencia (HU-5).
 * Maneja archivos serializables (Juego) y planos (Puntuación).
 */
public class ArchivoUtil {

    private static final String DATA_FOLDER = "battleship_data";
    private static final String GAME_FILE = DATA_FOLDER + "/game.ser";
    private static final String SCORE_FILE = DATA_FOLDER + "/score.txt";

    /**
     * Inicializa la carpeta de datos si no existe.
     */
    public static void initDataFolder() {
        try {
            Files.createDirectories(Paths.get(DATA_FOLDER));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Guarda el estado completo del juego (Serializable).
     */
    public static void saveGame(Board playerBoard, Board machineBoard, boolean isPlayerTurn) {
        initDataFolder();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(GAME_FILE))) {
            oos.writeObject(playerBoard);
            oos.writeObject(machineBoard);
            oos.writeBoolean(isPlayerTurn);
            System.out.println("Juego guardado correctamente en: " + GAME_FILE);
        } catch (IOException e) {
            System.err.println("Error guardando el juego serializado: " + e.getMessage());
        }
    }

    /**
     * Guarda el nickname y barcos hundidos (Archivo Plano).
     * Formato: Nickname;BarcosHundidos
     */
    public static void saveScore(String nickname, int sunkenShips) {
        initDataFolder();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCORE_FILE))) {
            writer.write("Nickname: " + nickname);
            writer.newLine();
            writer.write("Barcos Hundidos: " + sunkenShips);
            writer.newLine();
            writer.write("Fecha: " + java.time.LocalDateTime.now());
            System.out.println("Puntuación guardada en texto plano.");
        } catch (IOException e) {
            System.err.println("Error guardando archivo plano: " + e.getMessage());
        }
    }

    /**
     * Carga el juego. Devuelve un array de objetos o null si falla.
     * Estructura del return: [Board player, Board machine, Boolean turn]
     */
    public static Object[] loadGame() {
        if (!Files.exists(Paths.get(GAME_FILE))) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(GAME_FILE))) {
            Board player = (Board) ois.readObject();
            Board machine = (Board) ois.readObject();
            boolean turn = ois.readBoolean();
            return new Object[]{player, machine, turn};
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error cargando partida: " + e.getMessage());
            return null;
        }
    }
}