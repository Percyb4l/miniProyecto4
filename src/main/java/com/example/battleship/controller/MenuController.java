package com.example.battleship.controller;

import com.example.battleship.util.ArchivoUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the main menu screen.
 * Handles new game, continue game, difficulty selection, and exit.
 * Implements the initialization of menu options and user interactions.
 *
 * @author Battleship Team
 * @version 1.0
 * @since 2025-12-09
 */
public class MenuController implements Initializable {

    @FXML private Button btnNewGame;
    @FXML private Button btnContinue;
    @FXML private Button btnDifficulty;
    @FXML private Button btnExit;
    @FXML private Label lblDifficulty;
    @FXML private VBox menuContainer;

    private String currentDifficulty = "EASY";

    /**
     * Initializes the menu controller.
     * Checks for saved games and updates UI accordingly.
     *
     * @param location The location used to resolve relative paths
     * @param resources The resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Check if there's a saved game
        boolean hasSavedGame = ArchivoUtil.loadGame() != null;
        btnContinue.setDisable(!hasSavedGame);

        // Load previous difficulty if exists
        String savedDifficulty = GameSession.getInstance().getDifficulty();
        if (savedDifficulty != null && !savedDifficulty.isEmpty()) {
            currentDifficulty = savedDifficulty;
        }

        updateDifficultyLabel();

        // Add hover effects to buttons
        addButtonHoverEffect(btnNewGame);
        addButtonHoverEffect(btnContinue);
        addButtonHoverEffect(btnDifficulty);
        addButtonHoverEffect(btnExit);
    }

    /**
     * Adds hover effect to a button for better UX.
     *
     * @param button The button to add effect to
     */
    private void addButtonHoverEffect(Button button) {
        String originalStyle = button.getStyle();

        button.setOnMouseEntered(e -> {
            button.setStyle(originalStyle + "-fx-scale-x: 1.05; -fx-scale-y: 1.05;");
        });

        button.setOnMouseExited(e -> {
            button.setStyle(originalStyle);
        });
    }

    /**
     * Handles the New Game button click.
     * Prompts for player nickname and starts a new game.
     */
    @FXML
    private void handleNewGame() {
        // Prompt for nickname
        TextInputDialog dialog = new TextInputDialog("Admiral");
        dialog.setTitle("Player Nickname");
        dialog.setHeaderText("🎖️ Enter Your Commander Name");
        dialog.setContentText("Nickname:");

        // Style the dialog
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #2c3e50;");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String nickname = result.get().trim();

            // Validate nickname length
            if (nickname.length() > 20) {
                showAlert("Invalid Nickname",
                        "Nickname too long",
                        "Please enter a nickname with 20 characters or less.",
                        Alert.AlertType.WARNING);
                handleNewGame(); // Retry
                return;
            }

            // Store session data
            GameSession session = GameSession.getInstance();
            session.setPlayerNickname(nickname);
            session.setDifficulty(currentDifficulty);
            session.setIsNewGame(true);
            session.setEnemyShipsDestroyed(0);

            System.out.println("Starting new game with nickname: " + nickname +
                    ", difficulty: " + currentDifficulty);

            // Navigate to game
            NavigationController.getInstance().showGame();
        } else if (result.isPresent()) {
            // Empty nickname entered
            showAlert("Invalid Nickname",
                    "Nickname required",
                    "Please enter a valid nickname to continue.",
                    Alert.AlertType.WARNING);
            handleNewGame(); // Retry
        }
        // If cancelled (result not present), do nothing
    }

    /**
     * Handles the Continue Game button click.
     * Loads the saved game and resumes gameplay.
     */
    @FXML
    private void handleContinue() {
        // Verify saved game still exists
        if (ArchivoUtil.loadGame() == null) {
            showAlert("No Saved Game",
                    "Cannot continue",
                    "No saved game found. Please start a new game.",
                    Alert.AlertType.ERROR);
            btnContinue.setDisable(true);
            return;
        }

        // Set session to load mode
        GameSession session = GameSession.getInstance();
        session.setIsNewGame(false);
        session.setDifficulty(currentDifficulty);

        System.out.println("Continuing saved game with difficulty: " + currentDifficulty);

        // Navigate to game
        NavigationController.getInstance().showGame();
    }

    /**
     * Handles the Difficulty button click.
     * Cycles through difficulty levels: EASY -> MEDIUM -> HARD -> EASY.
     */
    @FXML
    private void handleDifficulty() {
        switch (currentDifficulty) {
            case "EASY":
                currentDifficulty = "MEDIUM";
                break;
            case "MEDIUM":
                currentDifficulty = "HARD";
                break;
            case "HARD":
                currentDifficulty = "EASY";
                break;
            default:
                currentDifficulty = "EASY";
        }

        // Save to session
        GameSession.getInstance().setDifficulty(currentDifficulty);

        updateDifficultyLabel();

        System.out.println("Difficulty changed to: " + currentDifficulty);
    }

    /**
     * Updates the difficulty display label with current difficulty and description.
     */
    private void updateDifficultyLabel() {
        String description = "";
        String emoji = "";

        switch (currentDifficulty) {
            case "EASY":
                description = "Random shooting";
                emoji = "🟢";
                break;
            case "MEDIUM":
                description = "Targets after hit";
                emoji = "🟡";
                break;
            case "HARD":
                description = "Hunt & Target strategy";
                emoji = "🔴";
                break;
        }

        lblDifficulty.setText(emoji + " Difficulty: " + currentDifficulty + " - " + description);
    }

    /**
     * Handles the Exit button click.
     * Shows confirmation dialog and closes the application if confirmed.
     */
    @FXML
    private void handleExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Game");
        alert.setHeaderText("⚓ Leaving Naval Command?");
        alert.setContentText("Are you sure you want to exit Battleship?");

        // Style the dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #2c3e50;");

        // Add custom buttons
        ButtonType btnYes = new ButtonType("Yes, Exit", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnNo = new ButtonType("No, Stay", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnYes, btnNo);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == btnYes) {
            System.out.println("Application closed by user.");
            System.exit(0);
        }
    }

    /**
     * Shows an alert dialog with custom styling.
     *
     * @param title The alert title
     * @param header The alert header text
     * @param content The alert content text
     * @param type The alert type
     */
    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Style the dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #2c3e50;");

        alert.showAndWait();
    }
}