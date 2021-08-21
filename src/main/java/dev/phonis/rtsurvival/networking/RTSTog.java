package dev.phonis.rtsurvival.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class RTSTog implements RTPacket {

    @Override
    public byte getID() {
        return Packets.Out.RTSTogID;
    }

    @Override
    public void toBytes(DataOutputStream dos) {

    }

    public static RTSTog fromBytes(DataInputStream dis) {
        return new RTSTog();
    }

}
