package phonis.survival.networking;

public class RTSTPToHoveredWaypoint implements RTPacket {

    public final String waypoint;

    public RTSTPToHoveredWaypoint(String waypoint) {
        this.waypoint = waypoint;
    }

}
