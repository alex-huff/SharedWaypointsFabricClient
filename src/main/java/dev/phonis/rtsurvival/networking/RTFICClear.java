package dev.phonis.rtsurvival.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class RTFICClear implements RTPacket {

    @Override
    public byte getID() {
        return Packets.Out.RTFICClearID;
    }

    @Override
    public void toBytes(DataOutputStream dos) {

    }

    public static RTFICClear fromBytes(DataInputStream dis) {
        return new RTFICClear();
    }

}
