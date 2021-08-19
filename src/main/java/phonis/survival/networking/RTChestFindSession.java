package phonis.survival.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RTChestFindSession implements RTPacket {

    public final RTLocation closest;
    public final RTLocation best;
    public final RTLocation most;

    public RTChestFindSession(RTLocation closest, RTLocation best, RTLocation most) {
        this.closest = closest;
        this.best = best;
        this.most = most;
    }

    @Override
    public byte getID() {
        return Packets.In.RTChestFindSessionID;
    }

    @Override
    public void toBytes(DataOutputStream dos) throws IOException {
        this.closest.toBytes(dos);
        this.best.toBytes(dos);
        this.most.toBytes(dos);
    }

    public static RTChestFindSession fromBytes(DataInputStream dis) throws IOException {
        return new RTChestFindSession(
            RTLocation.fromBytes(dis),
            RTLocation.fromBytes(dis),
            RTLocation.fromBytes(dis)
        );
    }

}
