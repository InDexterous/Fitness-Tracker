package sismanis.fitnesstracker;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.Objects;

public class WorkoutTypeDefinition {
    private final StringProperty name;
    private final BooleanProperty tracksDuration;
    private final BooleanProperty tracksCalories;
    private final BooleanProperty tracksDistance;
    private final StringProperty defaultUnitsForDistance;

    public WorkoutTypeDefinition(String name, boolean tracksDuration, boolean tracksCalories, boolean tracksDistance, String defaultUnitsForDistance) {
        this.name = new SimpleStringProperty(name);
        this.tracksDuration = new SimpleBooleanProperty(tracksDuration);
        this.tracksCalories = new SimpleBooleanProperty(tracksCalories);
        this.tracksDistance = new SimpleBooleanProperty(tracksDistance);
        this.defaultUnitsForDistance = new SimpleStringProperty(
                (tracksDistance && defaultUnitsForDistance != null) ? defaultUnitsForDistance : ""
        );
    }

    public String getName() { return name.get(); }
    public boolean isTracksDuration() { return tracksDuration.get(); }
    public boolean isTracksCalories() { return tracksCalories.get(); }
    public boolean isTracksDistance() { return tracksDistance.get(); }
    public String getDefaultUnitsForDistance() { return defaultUnitsForDistance.get(); }

    public StringProperty nameProperty() { return name; }
    public BooleanProperty tracksDurationProperty() { return tracksDuration; }
    public BooleanProperty tracksCaloriesProperty() { return tracksCalories; }
    public BooleanProperty tracksDistanceProperty() { return tracksDistance; }
    public StringProperty defaultUnitsForDistanceProperty() { return defaultUnitsForDistance; }

    public void setName(String name) { this.name.set(name); }
    public void setTracksDuration(boolean tracksDuration) { this.tracksDuration.set(tracksDuration); }
    public void setTracksCalories(boolean tracksCalories) { this.tracksCalories.set(tracksCalories); }
    public void setTracksDistance(boolean tracksDistance) { this.tracksDistance.set(tracksDistance); }
    public void setDefaultUnitsForDistance(String units) {
        if (this.isTracksDistance() && units != null) {
            this.defaultUnitsForDistance.set(units);
        } else if (!this.isTracksDistance()) {
            this.defaultUnitsForDistance.set("");
        }
    }

    @Override
    public String toString() { return getName(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkoutTypeDefinition that = (WorkoutTypeDefinition) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() { return Objects.hash(getName()); }
}