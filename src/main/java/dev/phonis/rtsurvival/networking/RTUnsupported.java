package dev.phonis.rtsurvival.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RTUnsupported implements RTPacket {

    public final int protocolVersion;

    public RTUnsupported(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    @Override
    public byte getID() {
        return Packets.In.RTUnsupportedID;
    }

    @Override
    public void toBytes(DataOutputStream dos) throws IOException {
        dos.writeInt(this.protocolVersion);
    }

    public static RTUnsupported fromBytes(DataInputStream dis) throws IOException {
        return new RTUnsupported(dis.readInt());
    }

}
