module sismanis.fitnesstracker {
    requires javafx.controls;
    requires javafx.fxml;

    opens sismanis.fitnesstracker to javafx.fxml, javafx.base;
    exports sismanis.fitnesstracker;
}