package dev.phonis.sharedwaypoints.client.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SWWaypointInitialize implements SWPacket
{

    public final List<SWWaypoint> waypoints;

    public SWWaypointInitialize(List<SWWaypoint> waypoints)
    {
        this.waypoints = waypoints;
    }

    @Override
    public byte getID()
    {
        return Packets.In.SWWaypointInitializeID;
    }

    @Override
    public void toBytes(DataOutputStream dos) throws IOException
    {
        dos.writeInt(this.waypoints.size());

        for (SWWaypoint waypoint : this.waypoints)
        {
            waypoint.toBytes(dos);
        }
    }

    public static SWWaypointInitialize fromBytes(DataInputStream dis) throws IOException
    {
        int              length    = dis.readInt();
        List<SWWaypoint> waypoints = new ArrayList<>(length);

        for (int i = 0; i < length; i++)
        {
            waypoints.add(SWWaypoint.fromBytes(dis));
        }

        return new SWWaypointInitialize(waypoints);
    }

}