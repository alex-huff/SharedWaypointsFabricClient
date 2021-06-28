package phonis.survival.networking;

public class RTTetherOnHoveredWaypoint implements RTPacket {

    public final String waypoint;

    public RTTetherOnHoveredWaypoint(String waypoint) {
        this.waypoint = waypoint;
    }

}
