package dev.phonis.rtsurvival.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class RTFIC implements RTPacket {

    @Override
    public byte getID() {
        return Packets.Out.RTFICID;
    }

    @Override
    public void toBytes(DataOutputStream dos) {

    }

    public static RTFIC fromBytes(DataInputStream dis) {
        return new RTFIC();
    }

}
