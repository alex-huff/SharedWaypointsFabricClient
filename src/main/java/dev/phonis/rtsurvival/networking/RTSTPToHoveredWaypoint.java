package dev.phonis.rtsurvival.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RTSTPToHoveredWaypoint implements RTPacket {

    public final String waypoint;

    public RTSTPToHoveredWaypoint(String waypoint) {
        this.waypoint = waypoint;
    }

    @Override
    public byte getID() {
        return Packets.Out.RTSTPToHoveredWaypointID;
    }

    @Override
    public void toBytes(DataOutputStream dos) throws IOException {
        dos.writeUTF(this.waypoint);
    }

    public static RTSTPToHoveredWaypoint fromBytes(DataInputStream dis) throws IOException {
        return new RTSTPToHoveredWaypoint(dis.readUTF());
    }

}
