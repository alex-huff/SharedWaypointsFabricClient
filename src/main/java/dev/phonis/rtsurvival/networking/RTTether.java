package dev.phonis.rtsurvival.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class RTTether implements RTSerializable {

    public final RTLocation location;

    public RTTether(RTLocation location) {
        this.location = location;
    }

    @Override
    public abstract boolean equals(Object other);

    public abstract byte getTetherID();

    protected abstract void writeTether(DataOutputStream dos) throws IOException;

    @Override
    public void toBytes(DataOutputStream dos) throws IOException {
        dos.writeByte(this.getTetherID());
        this.writeTether(dos);
    }

    public static RTTether fromBytes(DataInputStream dis) throws IOException {
        byte tetherType = dis.readByte();

        switch (tetherType) {
            case RTTetherDeathPoint.ID:
                return RTTetherDeathPoint.fromBytes(dis);
            case RTTetherPlayer.ID:
                return RTTetherPlayer.fromBytes(dis);
            case RTTetherWaypoint.ID:
                return RTTetherWaypoint.fromBytes(dis);
            default:
                return null;
        }
    }

}
