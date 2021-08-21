package dev.phonis.rtsurvival.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RTTetherRemove implements RTPacket {

    public final RTTether toRemove;

    public RTTetherRemove(RTTether toRemove) {
        this.toRemove = toRemove;
    }

    @Override
    public byte getID() {
        return Packets.In.RTTetherRemoveID;
    }

    @Override
    public void toBytes(DataOutputStream dos) throws IOException {
        this.toRemove.toBytes(dos);
    }

    public static RTTetherRemove fromBytes(DataInputStream dis) throws IOException {
        return new RTTetherRemove(RTTether.fromBytes(dis));
    }

}
