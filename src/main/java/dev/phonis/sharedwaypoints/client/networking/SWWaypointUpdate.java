package dev.phonis.sharedwaypoints.client.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SWWaypointUpdate implements SWPacket {

    public final SWWaypoint newWaypoint;

    public SWWaypointUpdate(SWWaypoint newWaypoint) {
        this.newWaypoint = newWaypoint;
    }

    @Override
    public byte getID() {
        return Packets.In.SWWaypointUpdateID;
    }

    @Override
    public void toBytes(DataOutputStream dos) throws IOException {
        this.newWaypoint.toBytes(dos);
    }

    public static SWWaypointUpdate fromBytes(DataInputStream dis) throws IOException {
        return new SWWaypointUpdate(SWWaypoint.fromBytes(dis));
    }

}
