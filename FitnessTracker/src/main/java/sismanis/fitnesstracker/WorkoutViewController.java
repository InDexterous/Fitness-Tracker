package sismanis.fitnesstracker;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional; // Προσθήκη για το Optional<ButtonType>
import java.util.ResourceBundle;

public class WorkoutViewController implements Initializable {

    @FXML private TableView<TrainingSession> sessionsTable;
    @FXML private TableColumn<TrainingSession, LocalDate> dateCol;
    @FXML private TableColumn<TrainingSession, String> typeCol;
    @FXML private TableColumn<TrainingSession, Integer> durationCol;
    @FXML private TableColumn<TrainingSession, Integer> caloriesCol;
    @FXML private TableColumn<TrainingSession, Double> distanceCol;
    @FXML private TableColumn<TrainingSession, Integer> goalDurationCol;
    @FXML private TableColumn<TrainingSession, Integer> goalCaloriesCol;
    @FXML private TableColumn<TrainingSession, String> notesCol;

    @FXML private DatePicker datePicker;
    @FXML private ComboBox<WorkoutTypeDefinition> typeComboBox;
    @FXML private Button manageWorkoutTypesButton;
    @FXML private TextField durationField;
    @FXML private TextField caloriesField;
    @FXML private TextField distanceField;
    @FXML private TextField goalDurationField;
    @FXML private TextField goalCaloriesField;
    @FXML private TextArea notesArea;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private ObservableList<TrainingSession> localTrainingSessions;
    private ObservableList<WorkoutTypeDefinition> localWorkoutTypeDefinitions;
    private ShellController shellController;

    private TrainingSession selectedSessionForEdit = null;

    public void setData(ObservableList<TrainingSession> sessions,
                        ObservableList<WorkoutTypeDefinition> types,
                        ShellController shell) {
        this.localTrainingSessions = sessions;
        this.localWorkoutTypeDefinitions = types;
        this.shellController = shell;

        sessionsTable.setItems(this.localTrainingSessions);
        typeComboBox.setItems(this.localWorkoutTypeDefinitions);

        if (this.localWorkoutTypeDefinitions != null && !this.localWorkoutTypeDefinitions.isEmpty()) {
            typeComboBox.getSelectionModel().selectFirst();
        } else {
            updateTrainingInputFieldsAvailability(null);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        durationCol.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));
        caloriesCol.setCellValueFactory(new PropertyValueFactory<>("caloriesBurned"));
        distanceCol.setCellValueFactory(new PropertyValueFactory<>("distanceKm"));
        goalDurationCol.setCellValueFactory(new PropertyValueFactory<>("goalDurationMinutes"));
        goalCaloriesCol.setCellValueFactory(new PropertyValueFactory<>("goalCaloriesBurned"));
        notesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));

        typeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateTrainingInputFieldsAvailability(newVal);
        });

        editButton.disableProperty().bind(sessionsTable.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.disableProperty().bind(sessionsTable.getSelectionModel().selectedItemProperty().isNull());

        datePicker.setValue(LocalDate.now());
        updateTrainingInputFieldsAvailability(typeComboBox.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void handleAddSession(ActionEvent event) {
        try {
            LocalDate date = datePicker.getValue();
            WorkoutTypeDefinition selectedWorkoutType = typeComboBox.getValue();

            if (date == null) { showAlert("Σφάλμα Εισαγωγής", "Η ημερομηνία είναι υποχρεωτική."); return; }
            if (selectedWorkoutType == null) { showAlert("Σφάλμα Εισαγωγής", "Παρακαλώ επιλέξτε ένα είδος προπόνησης."); return; }
            
            String typeName = selectedWorkoutType.getName();
            int duration = 0, calories = 0, goalDuration = 0, goalCalories = 0;
            double distance = 0.0;

            if (selectedWorkoutType.isTracksDuration()) {
                if (durationField.getText().trim().isEmpty()) { showAlert("Σφάλμα Εισαγωγής", "Η διάρκεια είναι υποχρεωτική."); return; }
                duration = Integer.parseInt(durationField.getText().trim());
            }
            if (selectedWorkoutType.isTracksCalories()) {
                if (caloriesField.getText().trim().isEmpty()) { showAlert("Σφάλμα Εισαγωγής", "Οι θερμίδες είναι υποχρεωτικές."); return; }
                calories = Integer.parseInt(caloriesField.getText().trim());
            }
            if (selectedWorkoutType.isTracksDistance()) {
                if (distanceField.getText().trim().isEmpty()) { showAlert("Σφάλμα Εισαγωγής", "Η απόσταση είναι υποχρεωτική."); return; }
                distance = Double.parseDouble(distanceField.getText().trim());
            }
            if (!goalDurationField.getText().trim().isEmpty()) goalDuration = Integer.parseInt(goalDurationField.getText().trim());
            if (!goalCaloriesField.getText().trim().isEmpty()) goalCalories = Integer.parseInt(goalCaloriesField.getText().trim());
            String notes = notesArea.getText();


            if (selectedSessionForEdit == null) {
                TrainingSession newSession = new TrainingSession(date, typeName, duration, calories, distance, notes, goalDuration, goalCalories);
                localTrainingSessions.add(newSession);
            } else {
                selectedSessionForEdit.setDate(date);
                selectedSessionForEdit.setType(typeName);
                selectedSessionForEdit.setDurationMinutes(duration);
                selectedSessionForEdit.setCaloriesBurned(calories);
                selectedSessionForEdit.setDistanceKm(distance);
                selectedSessionForEdit.setNotes(notes);
                selectedSessionForEdit.setGoalDurationMinutes(goalDuration);
                selectedSessionForEdit.setGoalCaloriesBurned(goalCalories);
                sessionsTable.refresh();
                selectedSessionForEdit = null;
                addButton.setText("Προσθήκη / Αποθήκευση");
            }
            clearTrainingInputFields();
            if (shellController != null) shellController.recalculateAllGoals();
        } catch (NumberFormatException e) {
            showAlert("Σφάλμα Εισαγωγής", "Τα πεδία διάρκειας, θερμίδων, απόστασης και στόχων πρέπει να είναι έγκυροι αριθμοί.");
        }
    }

    @FXML
    private void handleEditSession(ActionEvent event) {
        selectedSessionForEdit = sessionsTable.getSelectionModel().getSelectedItem();
        if (selectedSessionForEdit != null) {
            datePicker.setValue(selectedSessionForEdit.getDate());
            String sessionTypeName = selectedSessionForEdit.getType();
            WorkoutTypeDefinition typeToSelect = localWorkoutTypeDefinitions.stream()
                            .filter(wtd -> wtd.getName().equals(sessionTypeName))
                            .findFirst()
                            .orElse(null);
            typeComboBox.setValue(typeToSelect);

            durationField.setText(typeToSelect != null && typeToSelect.isTracksDuration() ? String.valueOf(selectedSessionForEdit.getDurationMinutes()) : "");
            caloriesField.setText(typeToSelect != null && typeToSelect.isTracksCalories() ? String.valueOf(selectedSessionForEdit.getCaloriesBurned()) : "");
            distanceField.setText(typeToSelect != null && typeToSelect.isTracksDistance() ? String.valueOf(selectedSessionForEdit.getDistanceKm()) : "");
            
            goalDurationField.setText(selectedSessionForEdit.getGoalDurationMinutes() > 0 ? String.valueOf(selectedSessionForEdit.getGoalDurationMinutes()) : "");
            goalCaloriesField.setText(selectedSessionForEdit.getGoalCaloriesBurned() > 0 ? String.valueOf(selectedSessionForEdit.getGoalCaloriesBurned()) : "");
            notesArea.setText(selectedSessionForEdit.getNotes());
            addButton.setText("Αποθήκευση Αλλαγών");
            datePicker.requestFocus();
        }
    }

    @FXML
    private void handleDeleteSession(ActionEvent event) {
        TrainingSession selectedSession = sessionsTable.getSelectionModel().getSelectedItem();
        if (selectedSession != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Επιβεβαίωση Διαγραφής");
            confirmation.setHeaderText("Διαγραφή Προπόνησης");
            confirmation.setContentText("Είστε σίγουροι ότι θέλετε να διαγράψετε την προπόνηση:\n" + selectedSession.getDate() + " - " + selectedSession.getType() + "?");
            
            Optional<ButtonType> result = confirmation.showAndWait(); // Χρήση Optional
            if (result.isPresent() && result.get() == ButtonType.OK) {
                localTrainingSessions.remove(selectedSession);
                if (shellController != null) shellController.recalculateAllGoals();
                clearTrainingInputFields();
            }
        } else {
            showAlert("Δεν Επιλέχθηκε Προπόνηση", "Παρακαλώ επιλέξτε μια προπόνηση για διαγραφή.");
        }
    }

    private void clearTrainingInputFields() {
        datePicker.setValue(LocalDate.now());
        if (typeComboBox.getItems() != null && !typeComboBox.getItems().isEmpty()) {
             typeComboBox.getSelectionModel().selectFirst();
        } else {
            typeComboBox.getSelectionModel().clearSelection();
            updateTrainingInputFieldsAvailability(null);
        }
        goalDurationField.clear();
        goalCaloriesField.clear();
        notesArea.clear();
        selectedSessionForEdit = null;
        addButton.setText("Προσθήκη / Αποθήκευση");
        datePicker.requestFocus();
    }

    @FXML
    private void handleManageWorkoutTypes(ActionEvent event) {
        showAlert("Πληροφορία", "Η διαχείριση ειδών προπόνησης δεν έχει υλοποιηθεί ακόμα.");
    }

    private void updateTrainingInputFieldsAvailability(WorkoutTypeDefinition selectedType) {
        boolean durationEnabled = selectedType != null && selectedType.isTracksDuration();
        boolean caloriesEnabled = selectedType != null && selectedType.isTracksCalories();
        boolean distanceEnabled = selectedType != null && selectedType.isTracksDistance();

        durationField.setDisable(!durationEnabled);
        caloriesField.setDisable(!caloriesEnabled);
        distanceField.setDisable(!distanceEnabled);

        // Clear fields if editing and new type doesn't support it
        if (selectedSessionForEdit != null && selectedSessionForEdit.getType().equals(selectedType != null ? selectedType.getName() : "")) {
            durationField.setText(durationEnabled ? String.valueOf(selectedSessionForEdit.getDurationMinutes()) : "");
            caloriesField.setText(caloriesEnabled ? String.valueOf(selectedSessionForEdit.getCaloriesBurned()) : "");
            distanceField.setText(distanceEnabled ? String.valueOf(selectedSessionForEdit.getDistanceKm()) : "");
        } else { // New entry or type changed during edit
             if (!durationEnabled) durationField.clear();
             if (!caloriesEnabled) caloriesField.clear();
             if (!distanceEnabled) distanceField.clear();
        }
        
        durationField.setPromptText(durationEnabled ? "π.χ. 30" : "-");
        caloriesField.setPromptText(caloriesEnabled ? "π.χ. 350" : "-");
        distanceField.setPromptText(distanceEnabled ? "π.χ. 5.5 (" + (selectedType != null ? selectedType.getDefaultUnitsForDistance() : "km") + ")" : "-");
    }
    
    private void showAlert(String title, String message) {
        Alert.AlertType alertType = title.toLowerCase().contains("σφάλμα") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION;
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // Καλύτερα να μην έχει header αν το title είναι περιγραφικό
        alert.setContentText(message);
        alert.showAndWait();
    }
}