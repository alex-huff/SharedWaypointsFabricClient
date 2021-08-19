package phonis.survival.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RTWaypointUpdate implements RTPacket {

    public final RTWaypoint newWaypoint;

    public RTWaypointUpdate(RTWaypoint newWaypoint) {
        this.newWaypoint = newWaypoint;
    }

    @Override
    public byte getID() {
        return Packets.In.RTWaypointUpdateID;
    }

    @Override
    public void toBytes(DataOutputStream dos) throws IOException {
        this.newWaypoint.toBytes(dos);
    }

    public static RTWaypointUpdate fromBytes(DataInputStream dis) throws IOException {
        return new RTWaypointUpdate(RTWaypoint.fromBytes(dis));
    }

}
