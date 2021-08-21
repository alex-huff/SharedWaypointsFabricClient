package dev.phonis.rtsurvival.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RTTetherOnHoveredWaypoint implements RTPacket {

    public final String waypoint;

    public RTTetherOnHoveredWaypoint(String waypoint) {
        this.waypoint = waypoint;
    }

    @Override
    public byte getID() {
        return Packets.Out.RTTetherOnHoveredWaypointID;
    }

    @Override
    public void toBytes(DataOutputStream dos) throws IOException {
        dos.writeUTF(this.waypoint);
    }

    public static RTTetherOnHoveredWaypoint fromBytes(DataInputStream dis) throws IOException {
        return new RTTetherOnHoveredWaypoint(dis.readUTF());
    }

}
