package dev.phonis.sharedwaypoints.client.state;

import dev.phonis.sharedwaypoints.client.networking.SWWaypoint;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SWStateManager {

    public static final SWStateManager INSTANCE = new SWStateManager();

    private String hoveredWaypoint;
    private final List<SWWaypoint> waypoints;

    private SWStateManager() {
        this.waypoints = new ArrayList<>();
    }

    public synchronized void withWaypoints(Consumer<List<SWWaypoint>> consumer) {
        consumer.accept(this.waypoints);
    }

    public synchronized void clearState() {
        this.hoveredWaypoint = null;

        this.waypoints.clear();
    }

    public synchronized void initializeWaypoints(List<SWWaypoint> waypoints) {
        this.waypoints.addAll(waypoints);
    }

    // only update a waypoint it exists
    public synchronized void updateWaypoint(SWWaypoint waypoint) {
        if (this.removeWaypoint(waypoint.name)) this.waypoints.add(waypoint);
    }

    public synchronized boolean removeWaypoint(String waypointName) {
        return this.waypoints.removeIf(w -> w.name.equals(waypointName));
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

}
