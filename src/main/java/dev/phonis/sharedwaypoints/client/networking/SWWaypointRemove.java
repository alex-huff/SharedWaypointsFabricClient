package dev.phonis.sharedwaypoints.client.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SWWaypointRemove implements SWPacket {

    public final String toRemove;

    public SWWaypointRemove(String toRemove) {
        this.toRemove = toRemove;
    }

    @Override
    public byte getID() {
        return Packets.In.SWWaypointRemoveID;
    }

    @Override
    public void toBytes(DataOutputStream dos) throws IOException {
        dos.writeUTF(this.toRemove);
    }

    public static SWWaypointRemove fromBytes(DataInputStream dis) throws IOException {
        return new SWWaypointRemove(dis.readUTF());
    }

}
