package sismanis.fitnesstracker;

import javafx.beans.property.*;
import java.time.LocalDate;

public class TrainingSession {
    private final ObjectProperty<LocalDate> date;
    private final StringProperty type;
    private final IntegerProperty durationMinutes;
    private final IntegerProperty caloriesBurned;
    private final DoubleProperty distanceKm;
    private final StringProperty notes;
    private final IntegerProperty goalDurationMinutes;
    private final IntegerProperty goalCaloriesBurned;

    public TrainingSession(LocalDate date, String type, int durationMinutes, int caloriesBurned, double distanceKm, String notes, int goalDuration, int goalCalories) {
        this.date = new SimpleObjectProperty<>(date);
        this.type = new SimpleStringProperty(type);
        this.durationMinutes = new SimpleIntegerProperty(durationMinutes);
        this.caloriesBurned = new SimpleIntegerProperty(caloriesBurned);
        this.distanceKm = new SimpleDoubleProperty(distanceKm);
        this.notes = new SimpleStringProperty(notes);
        this.goalDurationMinutes = new SimpleIntegerProperty(goalDuration);
        this.goalCaloriesBurned = new SimpleIntegerProperty(goalCalories);
    }

    public LocalDate getDate() { return date.get(); }
    public String getType() { return type.get(); }
    public int getDurationMinutes() { return durationMinutes.get(); }
    public int getCaloriesBurned() { return caloriesBurned.get(); }
    public double getDistanceKm() { return distanceKm.get(); }
    public String getNotes() { return notes.get(); }
    public int getGoalDurationMinutes() { return goalDurationMinutes.get(); }
    public int getGoalCaloriesBurned() { return goalCaloriesBurned.get(); }

    public void setDate(LocalDate date) { this.date.set(date); }
    public void setType(String type) { this.type.set(type); }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes.set(durationMinutes); }
    public void setCaloriesBurned(int caloriesBurned) { this.caloriesBurned.set(caloriesBurned); }
    public void setDistanceKm(double distanceKm) { this.distanceKm.set(distanceKm); }
    public void setNotes(String notes) { this.notes.set(notes); }
    public void setGoalDurationMinutes(int goalDurationMinutes) { this.goalDurationMinutes.set(goalDurationMinutes); }
    public void setGoalCaloriesBurned(int goalCaloriesBurned) { this.goalCaloriesBurned.set(goalCaloriesBurned); }

    public ObjectProperty<LocalDate> dateProperty() { return date; }
    public StringProperty typeProperty() { return type; }
    public IntegerProperty durationMinutesProperty() { return durationMinutes; }
    public IntegerProperty caloriesBurnedProperty() { return caloriesBurned; }
    public DoubleProperty distanceKmProperty() { return distanceKm; }
    public StringProperty notesProperty() { return notes; }
    public IntegerProperty goalDurationMinutesProperty() { return goalDurationMinutes; }
    public IntegerProperty goalCaloriesBurnedProperty() { return goalCaloriesBurned; }

    @Override
    public String toString() {
        return String.format("%s - %s (%d min, %.1f %s)",
            getDate(), getType(), getDurationMinutes(), getDistanceKm(),
            (getDistanceKm() > 0 ? "km" : "")
        ).trim();
    }
}