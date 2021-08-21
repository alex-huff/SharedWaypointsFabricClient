package dev.phonis.rtsurvival.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RTTetherDeathPoint extends RTTether {

    public static final byte ID = 0x00;

    public RTTetherDeathPoint(RTLocation location) {
        super(location);
    }

    @Override
    public boolean equals(Object other) {
        // death locations do not need to be overridden
        if (other instanceof RTTetherDeathPoint) {
            return ((RTTetherDeathPoint) other).location.equals(this.location);
        }

        return false;
    }

    @Override
    public byte getTetherID() {
        return RTTetherDeathPoint.ID;
    }

    @Override
    protected void writeTether(DataOutputStream dos) throws IOException {
        this.location.toBytes(dos);
    }

    public static RTTetherDeathPoint fromBytes(DataInputStream dis) throws IOException {
        return new RTTetherDeathPoint(RTLocation.fromBytes(dis));
    }

}
