package phonis.survival.networking;

public class RTWaypointUpdate implements RTPacket {

    public final RTWaypoint newWaypoint;

    public RTWaypointUpdate(RTWaypoint newWaypoint) {
        this.newWaypoint = newWaypoint;
    }

}
