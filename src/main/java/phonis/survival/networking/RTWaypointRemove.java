package phonis.survival.networking;

public class RTWaypointRemove implements RTPacket {

    public final String toRemove;

    public RTWaypointRemove(String toRemove) {
        this.toRemove = toRemove;
    }

}
