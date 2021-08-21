package dev.phonis.rtsurvival.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RTTetherClear implements RTPacket {

    @Override
    public byte getID() {
        return Packets.Out.RTTetherClearID;
    }

    @Override
    public void toBytes(DataOutputStream dos) throws IOException {

    }

    public static RTTetherClear fromBytes(DataInputStream dis) {
        return new RTTetherClear();
    }

}
