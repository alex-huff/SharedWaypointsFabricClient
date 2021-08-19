package phonis.survival.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RTRegister implements RTPacket {

    public final int protocolVersion;

    public RTRegister(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    @Override
    public byte getID() {
        return Packets.Out.RTRegisterID;
    }

    @Override
    public void toBytes(DataOutputStream dos) throws IOException {
        dos.writeInt(this.protocolVersion);
    }

    public static RTRegister fromBytes(DataInputStream dis) throws IOException {
        return new RTRegister(dis.readInt());
    }

}
