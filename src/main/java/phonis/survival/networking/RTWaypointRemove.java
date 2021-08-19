package phonis.survival.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RTWaypointRemove implements RTPacket {

    public final String toRemove;

    public RTWaypointRemove(String toRemove) {
        this.toRemove = toRemove;
    }

    @Override
    public byte getID() {
        return Packets.In.RTWaypointRemoveID;
    }

    @Override
    public void toBytes(DataOutputStream dos) throws IOException {
        dos.writeUTF(this.toRemove);
    }

    public static RTWaypointRemove fromBytes(DataInputStream dis) throws IOException {
        return new RTWaypointRemove(dis.readUTF());
    }

}
