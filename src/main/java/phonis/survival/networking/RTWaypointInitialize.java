package phonis.survival.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RTWaypointInitialize implements RTPacket {

    public final List<RTWaypoint> waypoints;

    public RTWaypointInitialize(List<RTWaypoint> waypoints) {
        this.waypoints = waypoints;
    }

    @Override
    public byte getID() {
        return Packets.In.RTWaypointInitializeID;
    }

    @Override
    public void toBytes(DataOutputStream dos) throws IOException {
        dos.writeInt(this.waypoints.size());

        for (RTWaypoint waypoint : this.waypoints) {
            waypoint.toBytes(dos);
        }
    }

    public static RTWaypointInitialize fromBytes(DataInputStream dis) throws IOException {
        int length = dis.readInt();
        List<RTWaypoint> waypoints = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            waypoints.add(RTWaypoint.fromBytes(dis));
        }

        return new RTWaypointInitialize(waypoints);
    }

}