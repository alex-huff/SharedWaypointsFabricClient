package phonis.survival.state;

import phonis.survival.networking.RTChestFindSession;
import phonis.survival.networking.RTTether;
import phonis.survival.networking.RTWaypoint;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RTStateManager {

    public static final RTStateManager INSTANCE = new RTStateManager();

    private String hoveredWaypoint;
    private RTChestFindSession chestFindSession;
    private final List<RTWaypoint> waypoints;
    private final List<RTTether> tethers;

    private RTStateManager() {
        this.waypoints = new ArrayList<>();
        this.tethers = new ArrayList<>();
    }

    public synchronized void withTethers(Consumer<List<RTTether>> consumer) {
        consumer.accept(this.tethers);
    }

    public synchronized void withWaypoints(Consumer<List<RTWaypoint>> consumer) {
        consumer.accept(this.waypoints);
    }

    public synchronized void clearState() {
        this.hoveredWaypoint = null;
        this.chestFindSession = null;

        this.waypoints.clear();
        this.tethers.clear();
    }

    public synchronized void initializeWaypoints(List<RTWaypoint> waypoints) {
        this.waypoints.addAll(waypoints);
    }

    public synchronized void updateWaypoint(RTWaypoint waypoint) {
        this.removeWaypoint(waypoint.name);
        this.waypoints.add(waypoint);
    }

    public synchronized void removeWaypoint(String waypointName) {
        this.waypoints.removeIf(w -> w.name.equals(waypointName));
    }

    public synchronized void updateTether(RTTether tether) {
        this.removeTether(tether);
        this.tethers.add(tether);
    }

    public synchronized void removeTether(RTTether tether) {
        this.tethers.removeIf(t -> t.equals(tether));
    }

    public synchronized void updateChestFindSession(RTChestFindSession chestFindSession) {
        this.chestFindSession = chestFindSession;
    }

    public synchronized void clearHoveredWaypoint() {
        this.hoveredWaypoint = null;
    }

    public synchronized String getHoveredWaypoint() {
        return this.hoveredWaypoint;
    }
    
    public synchronized void setHoveredWaypoint(String hoveredWaypoint) {
        this.hoveredWaypoint = hoveredWaypoint;
    }

    public synchronized RTChestFindSession getChestFindSession() {
        return this.chestFindSession;
    }

    public synchronized void clearChestFindSession() {
        this.chestFindSession = null;
    }

}
