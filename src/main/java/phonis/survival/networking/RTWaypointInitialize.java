package phonis.survival.networking;

import java.util.List;

public class RTWaypointInitialize implements RTPacket {

    public final List<RTWaypoint> waypoints;

    public RTWaypointInitialize(List<RTWaypoint> waypoints) {
        this.waypoints = waypoints;
    }

}