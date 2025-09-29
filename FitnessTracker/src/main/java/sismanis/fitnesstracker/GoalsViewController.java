package sismanis.fitnesstracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional; // Προσθήκη για το Optional<ButtonType>
import java.util.ResourceBundle;

public class GoalsViewController implements Initializable {

    @FXML private TextField goalDescriptionField;
    @FXML private ComboBox<WorkoutGoal.GoalType> goalTypeComboBox;
    @FXML private TextField goalTargetValueField;
    @FXML private DatePicker goalStartDatePicker;
    @FXML private DatePicker goalEndDatePicker;
    @FXML private TextArea goalNotesArea;
    @FXML private Button addGoalButton;
    @FXML private Button editGoalButton;
    @FXML private Button deleteGoalButton;
    @FXML private TableView<WorkoutGoal> goalsTable;
    @FXML private TableColumn<WorkoutGoal, String> goalDescriptionCol;
    @FXML private TableColumn<WorkoutGoal, WorkoutGoal.GoalType> goalTypeCol;
    @FXML private TableColumn<WorkoutGoal, Double> goalTargetCol;
    @FXML private TableColumn<WorkoutGoal, Double> goalCurrentCol;
    @FXML private TableColumn<WorkoutGoal, Double> goalProgressCol;
    @FXML private TableColumn<WorkoutGoal, LocalDate> goalStartDateCol;
    @FXML private TableColumn<WorkoutGoal, LocalDate> goalEndDateCol;
    @FXML private TableColumn<WorkoutGoal, Boolean> goalCompletedCol;
    @FXML private TableColumn<WorkoutGoal, String> goalNotesCol;

    private ObservableList<WorkoutGoal> localWorkoutGoals;
    private ShellController shellController; // Δεν χρησιμοποιείται ενεργά σε αυτόν τον controller για callbacks προς το παρόν

    private WorkoutGoal selectedGoalForEdit = null;

    public void setData(ObservableList<WorkoutGoal> goals, ShellController shell) {
        this.localWorkoutGoals = goals;
        this.shellController = shell; // Αποθήκευση για μελλοντική χρήση αν χρειαστεί
        goalsTable.setItems(this.localWorkoutGoals);
         if (this.shellController != null) {
            this.shellController.recalculateAllGoals();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        goalDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        goalTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        goalTargetCol.setCellValueFactory(new PropertyValueFactory<>("targetValue"));
        goalCurrentCol.setCellValueFactory(new PropertyValueFactory<>("currentValue"));
        goalStartDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        goalEndDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        goalCompletedCol.setCellValueFactory(new PropertyValueFactory<>("completed"));
        goalNotesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));

        goalProgressCol.setCellFactory(col -> new TableCell<>() {
            private final ProgressBar progressBar = new ProgressBar();
            private final Label percentageLabel = new Label();
            private final HBox graphicNode = new HBox(5, progressBar, percentageLabel);
            {
                graphicNode.setAlignment(Pos.CENTER_LEFT);
                progressBar.setMinWidth(60);
                progressBar.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(progressBar, Priority.ALWAYS);
                percentageLabel.setMinWidth(35);
            }
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    WorkoutGoal goal = getTableRow().getItem();
                    double progress = goal.getProgressPercentage();
                    progressBar.setProgress(progress);
                    percentageLabel.setText(String.format(" %.0f%%", progress * 100));
                    setGraphic(graphicNode);
                }
            }
        });
        goalProgressCol.setCellValueFactory(cellData -> cellData.getValue().currentValueProperty().asObject());

        editGoalButton.disableProperty().bind(goalsTable.getSelectionModel().selectedItemProperty().isNull());
        deleteGoalButton.disableProperty().bind(goalsTable.getSelectionModel().selectedItemProperty().isNull());

        goalTypeComboBox.setItems(FXCollections.observableArrayList(WorkoutGoal.GoalType.values()));
        if (!goalTypeComboBox.getItems().isEmpty()) {
            goalTypeComboBox.getSelectionModel().selectFirst();
        }
        goalStartDatePicker.setValue(LocalDate.now());
    }

    public void refreshGoalsTable() {
        if (goalsTable != null) {
            goalsTable.refresh();
        }
    }

    @FXML
    private void handleAddGoal(ActionEvent event) {
        try {
            String description = goalDescriptionField.getText().trim();
            WorkoutGoal.GoalType type = goalTypeComboBox.getValue();
            String targetValueStr = goalTargetValueField.getText().trim();
            LocalDate startDate = goalStartDatePicker.getValue();
            LocalDate endDate = goalEndDatePicker.getValue();
            String notes = goalNotesArea.getText();

            if (description.isEmpty()) { showAlert("Σφάλμα Εισαγωγής", "Η περιγραφή είναι υποχρεωτική."); return; }
            // ... (παρόμοιοι έλεγχοι για type, targetValueStr, startDate, endDate) ...
            if (type == null) { showAlert("Σφάλμα Εισαγωγής", "Ο τύπος στόχου είναι υποχρεωτικός."); return; }
            if (targetValueStr.isEmpty()) { showAlert("Σφάλμα Εισαγωγής", "Η τιμή-στόχος είναι υποχρεωτική."); return; }
            if (startDate == null) { showAlert("Σφάλμα Εισαγωγής", "Η ημερομηνία έναρξης είναι υποχρεωτική."); return; }


            double targetValue = Double.parseDouble(targetValueStr);
            if (targetValue <= 0) { showAlert("Σφάλμα Εισαγωγής", "Η τιμή-στόχος πρέπει να είναι θετικός αριθμός."); return; }
            if (endDate != null && endDate.isBefore(startDate)) { showAlert("Σφάλμα Εισαγωγής", "Η ημ/νία λήξης δεν μπορεί να είναι πριν την ημ/νία έναρξης."); return; }


            if (selectedGoalForEdit == null) {
                WorkoutGoal newGoal = new WorkoutGoal(description, type, targetValue, startDate, endDate, notes);
                localWorkoutGoals.add(newGoal);
            } else {
                selectedGoalForEdit.setDescription(description);
                selectedGoalForEdit.setType(type);
                selectedGoalForEdit.setTargetValue(targetValue);
                selectedGoalForEdit.setStartDate(startDate);
                selectedGoalForEdit.setEndDate(endDate);
                selectedGoalForEdit.setNotes(notes);
                goalsTable.refresh();
                selectedGoalForEdit = null;
                addGoalButton.setText("Προσθήκη / Αποθήκευση Στόχου");
            }
            clearGoalInputFields();
            if (shellController != null) shellController.recalculateAllGoals(); // Κρίσιμο για ενημέρωση προόδου
        } catch (NumberFormatException e) {
            showAlert("Σφάλμα Εισαγωγής", "Η τιμή-στόχος πρέπει να είναι έγκυρος αριθμός.");
        }
    }
    
    @FXML
    private void handleEditGoal(ActionEvent event) {
        selectedGoalForEdit = goalsTable.getSelectionModel().getSelectedItem();
        if (selectedGoalForEdit != null) {
            goalDescriptionField.setText(selectedGoalForEdit.getDescription());
            goalTypeComboBox.setValue(selectedGoalForEdit.getType());
            goalTargetValueField.setText(String.valueOf(selectedGoalForEdit.getTargetValue()));
            goalStartDatePicker.setValue(selectedGoalForEdit.getStartDate());
            goalEndDatePicker.setValue(selectedGoalForEdit.getEndDate());
            goalNotesArea.setText(selectedGoalForEdit.getNotes());
            addGoalButton.setText("Αποθήκευση Αλλαγών");
            goalDescriptionField.requestFocus();
        }
    }

    @FXML
    private void handleDeleteGoal(ActionEvent event) {
        WorkoutGoal selectedGoal = goalsTable.getSelectionModel().getSelectedItem();
        if (selectedGoal != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Επιβεβαίωση Διαγραφής");
            confirmation.setHeaderText("Διαγραφή Στόχου");
            confirmation.setContentText("Είστε σίγουροι ότι θέλετε να διαγράψετε το στόχο:\n'" + selectedGoal.getDescription() + "'?");
            
            Optional<ButtonType> result = confirmation.showAndWait(); // Χρήση Optional
            if (result.isPresent() && result.get() == ButtonType.OK) {
                localWorkoutGoals.remove(selectedGoal);
                // Δεν χρειάζεται recalculateAllGoals εδώ, εκτός αν η διαγραφή ενός στόχου επηρεάζει άλλους.
                clearGoalInputFields();
            }
        } else {
            showAlert("Δεν Επιλέχθηκε Στόχος", "Παρακαλώ επιλέξτε ένα στόχο για διαγραφή.");
        }
    }

    private void clearGoalInputFields() {
        goalDescriptionField.clear();
        if (goalTypeComboBox.getItems() != null && !goalTypeComboBox.getItems().isEmpty()) {
            goalTypeComboBox.getSelectionModel().selectFirst();
        }
        goalTargetValueField.clear();
        goalStartDatePicker.setValue(LocalDate.now());
        goalEndDatePicker.setValue(null);
        goalNotesArea.clear();
        selectedGoalForEdit = null;
        addGoalButton.setText("Προσθήκη / Αποθήκευση Στόχου");
        goalDescriptionField.requestFocus();
    }

    private void showAlert(String title, String message) {
        Alert.AlertType alertType = title.toLowerCase().contains("σφάλμα") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION;
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}