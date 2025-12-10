package com.example.battleship.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Controller for the game over screen.
 * Displays victory/defeat information and allows returning to menu or playing again.
 *
 * @author Battleship Team
 * @version 1.0
 * @since 2025-12-09
 */
public class GameOverController {

    @FXML private Label lblTitle;
    @FXML private Label lblMessage;
    @FXML private Label lblStats;
    @FXML private Button btnPlayAgain;
    @FXML private Button btnMainMenu;
    @FXML private VBox containerBox;

    private boolean playerWon;
    private int shipsDestroyed;

    /**
     * Sets the game result data and updates the UI.
     *
     * @param playerWon true if player won
     * @param shipsDestroyed number of enemy ships destroyed
     */
    public void setGameResult(boolean playerWon, int shipsDestroyed) {
        this.playerWon = playerWon;
        this.shipsDestroyed = shipsDestroyed;

        updateUI();
    }

    /**
     * Updates the UI based on game result.
     */
    private void updateUI() {
        if (playerWon) {
            lblTitle.setText("🎉 VICTORY! 🎉");
            lblTitle.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 48px; -fx-font-weight: bold;");
            lblMessage.setText("Congratulations, Admiral!");
            lblMessage.setStyle("-fx-text-fill: #2ecc71;");
            containerBox.setStyle("-fx-background-color: linear-gradient(to bottom, #1e3c72, #2a5298);");
        } else {
            lblTitle.setText("💥 DEFEAT 💥");
            lblTitle.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 48px; -fx-font-weight: bold;");
            lblMessage.setText("Your fleet has been destroyed!");
            lblMessage.setStyle("-fx-text-fill: #c0392b;");
            containerBox.setStyle("-fx-background-color: linear-gradient(to bottom, #4a0000, #8b0000);");
        }

        String nickname = GameSession.getInstance().getPlayerNickname();
        String difficulty = GameSession.getInstance().getDifficulty();

        lblStats.setText(
                "Player: " + nickname + "\n" +
                        "Difficulty: " + difficulty + "\n" +
                        "Enemy Ships Destroyed: " + shipsDestroyed + " / 10\n" +
                        "Success Rate: " + (shipsDestroyed * 10) + "%"
        );
    }

    /**
     * Handles Play Again button click.
     * Starts a new game with same settings.
     */
    @FXML
    private void handlePlayAgain() {
        GameSession.getInstance().setIsNewGame(true);
        NavigationController.getInstance().showGame();
    }

    /**
     * Handles Main Menu button click.
     * Returns to the main menu.
     */
    @FXML
    private void handleMainMenu() {
        NavigationController.getInstance().showMainMenu();
    }
}
