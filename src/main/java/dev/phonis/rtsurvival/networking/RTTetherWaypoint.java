package dev.phonis.rtsurvival.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RTTetherWaypoint extends RTTether {

    public static final byte ID = 0x02;

    public final String waypoint;

    public RTTetherWaypoint(String waypoint, RTLocation location) {
        super(location);

        this.waypoint = waypoint;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof RTTetherWaypoint otherTetherWaypoint) {
            return otherTetherWaypoint.waypoint.equals(this.waypoint);
        }

        return false;
    }

    @Override
    public byte getTetherID() {
        return RTTetherWaypoint.ID;
    }

    @Override
    protected void writeTether(DataOutputStream dos) throws IOException {
        dos.writeUTF(this.waypoint);
        this.location.toBytes(dos);
    }

    public static RTTetherWaypoint fromBytes(DataInputStream dis) throws IOException {
        return new RTTetherWaypoint(
            dis.readUTF(),
            RTLocation.fromBytes(dis)
        );
    }

}
