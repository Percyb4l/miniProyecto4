module com.example.battleship {
    requires javafx.controls;
    requires javafx.fxml;

    // Permite que JavaFX acceda a la clase Main
    opens com.example.battleship to javafx.fxml;

    // ESTA ES LA LÍNEA QUE TE FALTA:
    // Permite que JavaFX inyecte los botones y eventos en tu ViewController
    opens com.example.battleship.controller to javafx.fxml;

    // Exporta el paquete principal para que el sistema pueda iniciar la App
    exports com.example.battleship;
}