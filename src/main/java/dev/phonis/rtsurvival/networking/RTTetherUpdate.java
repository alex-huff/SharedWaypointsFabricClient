package dev.phonis.rtsurvival.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RTTetherUpdate implements RTPacket {

    public final RTTether toUpdate;

    public RTTetherUpdate(RTTether toUpdate) {
        this.toUpdate = toUpdate;
    }

    @Override
    public byte getID() {
        return Packets.In.RTTetherUpdateID;
    }

    @Override
    public void toBytes(DataOutputStream dos) throws IOException {
        this.toUpdate.toBytes(dos);
    }

    public static RTTetherUpdate fromBytes(DataInputStream dis) throws IOException {
        return new RTTetherUpdate(RTTether.fromBytes(dis));
    }

}
