package com.example.battleship.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Manages navigation between different screens in the application.
 * Implements centralized view management for better separation of concerns.
 *
 * @author Battleship Team
 * @version 1.0
 * @since 2025-12-09
 */
public class NavigationController {

    private static NavigationController instance;
    private Stage primaryStage;

    private NavigationController() {}

    public static NavigationController getInstance() {
        if (instance == null) {
            instance = new NavigationController();
        }
        return instance;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * Navigates to the main menu screen.
     */
    public void showMainMenu() {
        loadScreen("/com/example/battleship/MenuView.fxml", "Batalla Naval - Menu");
    }

    /**
     * Navigates to the game screen.
     */
    public void showGame() {
        loadScreen("/com/example/battleship/GameView.fxml", "Batalla Naval - Game");
    }

    /**
     * Navigates to the difficulty selection screen.
     */
    public void showDifficultySelection() {
        loadScreen("/com/example/battleship/DifficultyView.fxml", "Batalla Naval - Select Difficulty");
    }

    /**
     * Navigates to the game over screen.
     *
     * @param playerWon true if player won, false if lost
     * @param shipsDestroyed number of enemy ships destroyed
     */
    public void showGameOver(boolean playerWon, int shipsDestroyed) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/battleship/GameOverView.fxml"));
            Parent root = loader.load();

            GameOverController controller = loader.getController();
            controller.setGameResult(playerWon, shipsDestroyed);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Batalla Naval - Game Over");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the pause menu overlay.
     */
    public void showPauseMenu() {
        // This will be handled as a dialog/overlay in the game screen
        // See PauseMenuController for implementation
    }

    /**
     * Generic screen loader.
     *
     * @param fxmlPath Path to the FXML file
     * @param title Window title
     */
    private void loadScreen(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error loading screen: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Gets the current primary stage.
     *
     * @return The primary stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
