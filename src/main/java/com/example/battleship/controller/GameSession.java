package com.example.battleship.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Singleton class to store game session data across screens.
 * Implements the Singleton pattern for global state management.
 * This class maintains player information, game settings, and statistics
 * that persist throughout the application lifecycle.
 *
 * <p>Design Pattern: Singleton</p>
 * <p>Purpose: Centralized state management without global variables</p>
 * <p>Benefits: Single source of truth, easy access, thread-safe</p>
 *
 * @author Battleship Team
 * @version 1.0
 * @since 2025-12-09
 */
public class GameSession {

    /**
     * The single instance of GameSession (Singleton pattern).
     */
    private static GameSession instance;

    /**
     * Player's nickname/commander name.
     */
    private String playerNickname = "Admiral";

    /**
     * Current difficulty level: EASY, MEDIUM, or HARD.
     */
    private String difficulty = "EASY";

    /**
     * Flag indicating if starting a new game (true) or continuing (false).
     */
    private boolean isNewGame = true;

    /**
     * Number of enemy ships destroyed by the player.
     */
    private int enemyShipsDestroyed = 0;

    /**
     * Number of player ships destroyed by the enemy.
     */
    private int playerShipsDestroyed = 0;

    /**
     * Total number of shots fired by the player.
     */
    private int totalShotsFired = 0;

    /**
     * Number of successful hits by the player.
     */
    private int successfulHits = 0;

    /**
     * Timestamp of when the current game session started.
     */
    private LocalDateTime gameStartTime;

    /**
     * Flag indicating if this is the first launch of the application.
     */
    private boolean isFirstLaunch = true;

    /**
     * Private constructor to prevent instantiation (Singleton pattern).
     */
    private GameSession() {
        this.gameStartTime = LocalDateTime.now();
    }

    /**
     * Gets the single instance of GameSession.
     * Creates the instance if it doesn't exist (lazy initialization).
     *
     * @return The singleton instance of GameSession
     */
    public static GameSession getInstance() {
        if (instance == null) {
            synchronized (GameSession.class) {
                if (instance == null) {
                    instance = new GameSession();
                }
            }
        }
        return instance;
    }

    // ==================== GETTERS AND SETTERS ====================

    /**
     * Gets the player's nickname.
     *
     * @return The player's nickname
     */
    public String getPlayerNickname() {
        return playerNickname;
    }

    /**
     * Sets the player's nickname.
     *
     * @param playerNickname The nickname to set
     */
    public void setPlayerNickname(String playerNickname) {
        if (playerNickname != null && !playerNickname.trim().isEmpty()) {
            this.playerNickname = playerNickname.trim();
        }
    }

    /**
     * Gets the current difficulty level.
     *
     * @return The difficulty level (EASY, MEDIUM, or HARD)
     */
    public String getDifficulty() {
        return difficulty;
    }

    /**
     * Sets the difficulty level.
     *
     * @param difficulty The difficulty to set (EASY, MEDIUM, or HARD)
     */
    public void setDifficulty(String difficulty) {
        if (difficulty != null && !difficulty.trim().isEmpty()) {
            this.difficulty = difficulty.toUpperCase();
        }
    }

    /**
     * Checks if this is a new game.
     *
     * @return true if new game, false if continuing
     */
    public boolean isNewGame() {
        return isNewGame;
    }

    /**
     * Sets whether this is a new game.
     *
     * @param isNewGame true for new game, false for continue
     */
    public void setIsNewGame(boolean isNewGame) {
        this.isNewGame = isNewGame;
        if (isNewGame) {
            // Reset game start time for new games
            this.gameStartTime = LocalDateTime.now();
        }
    }

    /**
     * Gets the number of enemy ships destroyed.
     *
     * @return Number of enemy ships destroyed
     */
    public int getEnemyShipsDestroyed() {
        return enemyShipsDestroyed;
    }

    /**
     * Sets the number of enemy ships destroyed.
     *
     * @param enemyShipsDestroyed Number of ships destroyed
     */
    public void setEnemyShipsDestroyed(int enemyShipsDestroyed) {
        this.enemyShipsDestroyed = Math.max(0, enemyShipsDestroyed);
    }

    /**
     * Gets the number of player ships destroyed.
     *
     * @return Number of player ships destroyed
     */
    public int getPlayerShipsDestroyed() {
        return playerShipsDestroyed;
    }

    /**
     * Sets the number of player ships destroyed.
     *
     * @param playerShipsDestroyed Number of ships destroyed
     */
    public void setPlayerShipsDestroyed(int playerShipsDestroyed) {
        this.playerShipsDestroyed = Math.max(0, playerShipsDestroyed);
    }

    /**
     * Gets the total number of shots fired.
     *
     * @return Total shots fired
     */
    public int getTotalShotsFired() {
        return totalShotsFired;
    }

    /**
     * Increments the total shots fired counter.
     */
    public void incrementShotsFired() {
        this.totalShotsFired++;
    }

    /**
     * Gets the number of successful hits.
     *
     * @return Number of successful hits
     */
    public int getSuccessfulHits() {
        return successfulHits;
    }

    /**
     * Increments the successful hits counter.
     */
    public void incrementSuccessfulHits() {
        this.successfulHits++;
    }

    /**
     * Calculates the accuracy percentage.
     *
     * @return Accuracy as a percentage (0-100)
     */
    public double getAccuracy() {
        if (totalShotsFired == 0) return 0.0;
        return (successfulHits * 100.0) / totalShotsFired;
    }

    /**
     * Gets the game start time.
     *
     * @return The LocalDateTime when the game started
     */
    public LocalDateTime getGameStartTime() {
        return gameStartTime;
    }

    /**
     * Gets formatted game start time.
     *
     * @return Formatted date/time string
     */
    public String getFormattedStartTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return gameStartTime.format(formatter);
    }

    /**
     * Checks if this is the first application launch.
     *
     * @return true if first launch
     */
    public boolean isFirstLaunch() {
        return isFirstLaunch;
    }

    /**
     * Sets the first launch flag.
     *
     * @param isFirstLaunch true if first launch
     */
    public void setFirstLaunch(boolean isFirstLaunch) {
        this.isFirstLaunch = isFirstLaunch;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Resets the session to default values.
     * Useful when starting a completely new game session.
     */
    public void reset() {
        this.playerNickname = "Admiral";
        this.difficulty = "EASY";
        this.isNewGame = true;
        this.enemyShipsDestroyed = 0;
        this.playerShipsDestroyed = 0;
        this.totalShotsFired = 0;
        this.successfulHits = 0;
        this.gameStartTime = LocalDateTime.now();
    }

    /**
     * Resets only the game statistics (keeps player settings).
     * Useful when playing again with same player.
     */
    public void resetStatistics() {
        this.enemyShipsDestroyed = 0;
        this.playerShipsDestroyed = 0;
        this.totalShotsFired = 0;
        this.successfulHits = 0;
        this.gameStartTime = LocalDateTime.now();
    }

    /**
     * Gets a summary of the current session.
     *
     * @return String containing session information
     */
    public String getSessionSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("=== GAME SESSION ===\n");
        summary.append("Player: ").append(playerNickname).append("\n");
        summary.append("Difficulty: ").append(difficulty).append("\n");
        summary.append("Enemy Ships Destroyed: ").append(enemyShipsDestroyed).append("/10\n");
        summary.append("Player Ships Destroyed: ").append(playerShipsDestroyed).append("/10\n");
        summary.append("Total Shots Fired: ").append(totalShotsFired).append("\n");
        summary.append("Successful Hits: ").append(successfulHits).append("\n");
        summary.append("Accuracy: ").append(String.format("%.1f", getAccuracy())).append("%\n");
        summary.append("Started: ").append(getFormattedStartTime()).append("\n");
        return summary.toString();
    }

    /**
     * Prints session information to console (for debugging).
     */
    public void printSessionInfo() {
        System.out.println(getSessionSummary());
    }

    /**
     * Validates if the session data is in a consistent state.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        if (playerNickname == null || playerNickname.trim().isEmpty()) {
            return false;
        }
        if (!difficulty.equals("EASY") && !difficulty.equals("MEDIUM") && !difficulty.equals("HARD")) {
            return false;
        }
        if (enemyShipsDestroyed < 0 || enemyShipsDestroyed > 10) {
            return false;
        }
        if (playerShipsDestroyed < 0 || playerShipsDestroyed > 10) {
            return false;
        }
        if (totalShotsFired < 0 || successfulHits < 0) {
            return false;
        }
        if (successfulHits > totalShotsFired) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GameSession{" +
                "playerNickname='" + playerNickname + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", isNewGame=" + isNewGame +
                ", enemyShipsDestroyed=" + enemyShipsDestroyed +
                ", playerShipsDestroyed=" + playerShipsDestroyed +
                ", totalShotsFired=" + totalShotsFired +
                ", successfulHits=" + successfulHits +
                ", accuracy=" + String.format("%.1f", getAccuracy()) + "%" +
                '}';
    }
}