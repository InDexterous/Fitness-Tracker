package sismanis.fitnesstracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ShellController implements Initializable {

    @FXML private AnchorPane contentArea;
    @FXML private Button navWorkoutsButton;
    @FXML private Button navGoalsButton;

    private final ObservableList<TrainingSession> trainingSessions = FXCollections.observableArrayList();
    private final ObservableList<WorkoutGoal> workoutGoals = FXCollections.observableArrayList();
    private final ObservableList<WorkoutTypeDefinition> workoutTypeDefinitions = FXCollections.observableArrayList();

    private WorkoutViewController workoutViewControllerInstance;
    private GoalsViewController goalsViewControllerInstance;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeWorkoutTypeDefinitions();
        loadSampleTrainingData();
        loadSampleGoalData();
        showWorkoutsView(null);
        setActiveNavButton(navWorkoutsButton);
    }

    private void initializeWorkoutTypeDefinitions() {
        workoutTypeDefinitions.add(new WorkoutTypeDefinition("Τρέξιμο", true, true, true, "km"));
        workoutTypeDefinitions.add(new WorkoutTypeDefinition("Ποδήλατο", true, true, true, "km"));
        workoutTypeDefinitions.add(new WorkoutTypeDefinition("Κολύμβηση", true, true, true, "m"));
        workoutTypeDefinitions.add(new WorkoutTypeDefinition("Βάρη", true, true, false, ""));
        workoutTypeDefinitions.add(new WorkoutTypeDefinition("Yoga", true, true, false, ""));
        workoutTypeDefinitions.add(new WorkoutTypeDefinition("HIIT", true, true, false, ""));
    }

    private void loadSampleTrainingData() {
        trainingSessions.add(new TrainingSession(LocalDate.now().minusDays(2), "Τρέξιμο", 30, 300, 5.0, "Καλή προπόνηση", 25, 250));
        trainingSessions.add(new TrainingSession(LocalDate.now().minusDays(1), "Βάρη", 60, 400, 0.0, "Πόδια", 0, 0));
    }

    private void loadSampleGoalData() {
        workoutGoals.add(new WorkoutGoal("Τρέξε 5 ώρες αυτό το μήνα", WorkoutGoal.GoalType.TOTAL_DURATION_MINUTES, 300, LocalDate.now().withDayOfMonth(1), null, "Προετοιμασία για αγώνα"));
        workoutGoals.add(new WorkoutGoal("Κάψε 2000 θερμίδες", WorkoutGoal.GoalType.TOTAL_CALORIES_BURNED, 2000, LocalDate.now().minusWeeks(1), LocalDate.now().plusWeeks(1), "Εβδομαδιαίος στόχος"));
        recalculateAllGoals();
    }

    @FXML
    void showWorkoutsView(ActionEvent event) {
        loadView("/sismanis/fitnesstracker/WorkoutView.fxml", true);
        setActiveNavButton(navWorkoutsButton);
    }

    @FXML
    void showGoalsView(ActionEvent event) {
        loadView("/sismanis/fitnesstracker/GoalsView.fxml", false);
        setActiveNavButton(navGoalsButton);
    }

    private void setActiveNavButton(Button activeButton) {
        navWorkoutsButton.setStyle("-fx-font-size: 14px; -fx-base: #d0d0d0;");
        navGoalsButton.setStyle("-fx-font-size: 14px; -fx-base: #d0d0d0;");
        if (activeButton != null) {
            activeButton.setStyle("-fx-font-size: 14px; -fx-base: #a0a0a0; -fx-font-weight: bold;");
        }
    }

    private void loadView(String fxmlPath, boolean isWorkoutView) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent viewRoot = loader.load();

            if (isWorkoutView) {
                workoutViewControllerInstance = loader.getController();
                if (workoutViewControllerInstance != null) {
                    workoutViewControllerInstance.setData(trainingSessions, workoutTypeDefinitions, this);
                }
            } else {
                goalsViewControllerInstance = loader.getController();
                if (goalsViewControllerInstance != null) {
                    goalsViewControllerInstance.setData(workoutGoals, this);
                }
            }
            contentArea.getChildren().setAll(viewRoot);
            AnchorPane.setTopAnchor(viewRoot, 0.0);
            AnchorPane.setBottomAnchor(viewRoot, 0.0);
            AnchorPane.setLeftAnchor(viewRoot, 0.0);
            AnchorPane.setRightAnchor(viewRoot, 0.0);
        } catch (IOException e) {
            showAlert("Σφάλμα Φόρτωσης", "Δεν ήταν δυνατή η φόρτωση της επιλεγμένης όψης.", "Σφάλμα: " + e.getMessage());
        }
    }

    public void recalculateAllGoals() {
        if (workoutGoals == null || trainingSessions == null) return;
        for (WorkoutGoal goal : workoutGoals) {
            goal.resetCurrentValue();
            for (TrainingSession session : trainingSessions) {
                if (session.getDate().isBefore(goal.getStartDate()) ||
                    (goal.getEndDate() != null && session.getDate().isAfter(goal.getEndDate()))) {
                    continue;
                }
                switch (goal.getType()) {
                    case TOTAL_DURATION_MINUTES: goal.updateCurrentValue(session.getDurationMinutes()); break;
                    case TOTAL_CALORIES_BURNED: goal.updateCurrentValue(session.getCaloriesBurned()); break;
                    case TOTAL_DISTANCE_KM: goal.updateCurrentValue(session.getDistanceKm()); break;
                }
            }
        }
        if (goalsViewControllerInstance != null && isGoalsViewActive()) {
             goalsViewControllerInstance.refreshGoalsTable();
        }
    }

    // Βοηθητική μέθοδος για να ελέγξουμε αν η όψη των στόχων είναι ενεργή
    private boolean isGoalsViewActive() {
        if (contentArea.getChildren().isEmpty()) return false;
        Object controllerOfCurrentView = contentArea.getChildren().get(0).getProperties().get("javafx.scene.control.Control Labeled"); // Heuristic
        return goalsViewControllerInstance != null && controllerOfCurrentView == goalsViewControllerInstance; // Not perfect
    }
    
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR); // Τα σφάλματα φόρτωσης είναι ERROR
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}