package com.example.battleship;

import com.example.battleship.controller.NavigationController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application entry point.
 * Initializes the navigation system and displays the main menu.
 *
 * @author Battleship Team
 * @version 2.0
 * @since 2025-12-09
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Initialize navigation controller
        NavigationController navController = NavigationController.getInstance();
        navController.setPrimaryStage(primaryStage);

        // Configure primary stage
        primaryStage.setTitle("Batalla Naval");
        primaryStage.setResizable(false);

        // Show main menu as initial screen
        navController.showMainMenu();

        // Handle window close event
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Application closing...");
            // Could add save logic here if needed
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}