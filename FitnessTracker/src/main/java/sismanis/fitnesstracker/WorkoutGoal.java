package sismanis.fitnesstracker;

import javafx.beans.property.*;
import java.time.LocalDate;

public class WorkoutGoal {
    private final StringProperty description;
    private final ObjectProperty<GoalType> type;
    private final DoubleProperty targetValue;
    private final DoubleProperty currentValue;
    private final ObjectProperty<LocalDate> startDate;
    private final ObjectProperty<LocalDate> endDate;
    private final BooleanProperty completed;
    private final StringProperty notes;

    public enum GoalType {
        TOTAL_DURATION_MINUTES("Συνολική Διάρκεια (λεπτά)"),
        TOTAL_CALORIES_BURNED("Συνολικές Θερμίδες"),
        TOTAL_DISTANCE_KM("Συνολική Απόσταση (km)");

        private final String displayName;
        GoalType(String displayName) { this.displayName = displayName; }
        @Override public String toString() { return displayName; }
    }

    public WorkoutGoal(String description, GoalType type, double targetValue, LocalDate startDate, LocalDate endDate, String notes) {
        this.description = new SimpleStringProperty(description);
        this.type = new SimpleObjectProperty<>(type);
        this.targetValue = new SimpleDoubleProperty(targetValue);
        this.currentValue = new SimpleDoubleProperty(0);
        this.startDate = new SimpleObjectProperty<>(startDate);
        this.endDate = new SimpleObjectProperty<>(endDate);
        this.completed = new SimpleBooleanProperty(false);
        this.notes = new SimpleStringProperty(notes);
    }

    public String getDescription() { return description.get(); }
    public GoalType getType() { return type.get(); }
    public double getTargetValue() { return targetValue.get(); }
    public double getCurrentValue() { return currentValue.get(); }
    public LocalDate getStartDate() { return startDate.get(); }
    public LocalDate getEndDate() { return endDate.get(); }
    public boolean isCompleted() { return completed.get(); }
    public String getNotes() { return notes.get(); }

    public StringProperty descriptionProperty() { return description; }
    public ObjectProperty<GoalType> typeProperty() { return type; }
    public DoubleProperty targetValueProperty() { return targetValue; }
    public DoubleProperty currentValueProperty() { return currentValue; }
    public ObjectProperty<LocalDate> startDateProperty() { return startDate; }
    public ObjectProperty<LocalDate> endDateProperty() { return endDate; }
    public BooleanProperty completedProperty() { return completed; }
    public StringProperty notesProperty() { return notes; }

    public void setDescription(String description) { this.description.set(description); }
    public void setType(GoalType type) { this.type.set(type); }
    public void setTargetValue(double targetValue) { this.targetValue.set(targetValue); }
    public void setStartDate(LocalDate startDate) { this.startDate.set(startDate); }
    public void setEndDate(LocalDate endDate) { this.endDate.set(endDate); }
    public void setNotes(String notes) { this.notes.set(notes); }

    public void updateCurrentValue(double amount) {
        if (!isCompleted()) {
            this.currentValue.set(Math.min(this.currentValue.get() + amount, this.targetValue.get()));
            checkIfCompleted();
        }
    }

    public void resetCurrentValue() {
        this.currentValue.set(0);
        this.completed.set(false);
    }

    private void checkIfCompleted() {
        this.completed.set(this.currentValue.get() >= this.targetValue.get());
    }

    public double getProgressPercentage() {
        if (getTargetValue() == 0) return 0;
        return Math.min(1.0, getCurrentValue() / getTargetValue()); // Εξασφαλίζει ότι δεν ξεπερνά το 100% για την progress bar
    }

    @Override
    public String toString() {
        return String.format("%s (%s: %.1f / %.1f)", getDescription(), getType(), getCurrentValue(), getTargetValue());
    }
}