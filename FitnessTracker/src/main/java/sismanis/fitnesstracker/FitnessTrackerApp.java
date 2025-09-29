package sismanis.fitnesstracker;

import javafx.application.Application;
import javafx.application.Platform; // Προσθήκη για το Platform.exit()
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert; // Προσθήκη για τα Alerts
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class FitnessTrackerApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            String fxmlPath = "/sismanis/fitnesstracker/ShellView.fxml";
            URL fxmlLocation = getClass().getResource(fxmlPath);

            if (fxmlLocation == null) {
                showErrorAlert("Κρίσιμο Σφάλμα", "Δεν βρέθηκε το αρχείο διεπαφής χρήστη.", "Το αρχείο " + fxmlPath.substring(fxmlPath.lastIndexOf('/') + 1) + " είναι απαραίτητο για την εκκίνηση.");
                Platform.exit(); // Κλείσιμο της εφαρμογής
                return; // Έξοδος από τη μέθοδο start
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            primaryStage.setTitle("Παρακολούθηση Προόδου Φυσικής Κατάστασης");
            primaryStage.setScene(new Scene(root)); // Το μέγεθος θα το πάρει από το FXML του ShellView
            primaryStage.show();

        } catch (IOException ioe) {
            showErrorAlert("Σφάλμα Φόρτωσης Διεπαφής", "Παρουσιάστηκε σφάλμα κατά τη φόρτωση της κύριας οθόνης.", ioe.getMessage());
            Platform.exit();
        } catch (Exception e) {
            e.printStackTrace(); // Καλό είναι να μείνει για απρόβλεπτα σφάλματα
            showErrorAlert("Κρίσιμο Σφάλμα Εφαρμογής", "Προέκυψε ένα μη αναμενόμενο σφάλμα κατά την εκκίνηση.", e.getMessage());
            Platform.exit();
        }
    }

    // Βοηθητική μέθοδος για εμφάνιση σφαλμάτων
    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText("Λεπτομέρειες: " + content);
        alert.showAndWait();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}