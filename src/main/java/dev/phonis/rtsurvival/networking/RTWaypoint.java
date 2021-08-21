package dev.phonis.rtsurvival.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RTWaypoint implements RTSerializable {

    public final String name;
    public final RTLocation location;

    public RTWaypoint(String name, RTLocation location) {
        this.name = name;
        this.location = location;
    }

    @Override
    public void toBytes(DataOutputStream dos) throws IOException {
        dos.writeUTF(this.name);
        this.location.toBytes(dos);
    }

    public static RTWaypoint fromBytes(DataInputStream dis) throws IOException {
        return new RTWaypoint(
            dis.readUTF(),
            RTLocation.fromBytes(dis)
        );
    }

}
