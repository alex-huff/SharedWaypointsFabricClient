package dev.phonis.sharedwaypoints.client.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public
class SWWaypoint implements SWSerializable
{

    public final String     name;
    public final SWLocation location;

    public
    SWWaypoint(String name, SWLocation location)
    {
        this.name     = name;
        this.location = location;
    }

    @Override
    public
    void toBytes(DataOutputStream dos) throws IOException
    {
        dos.writeUTF(this.name);
        this.location.toBytes(dos);
    }

    public static
    SWWaypoint fromBytes(DataInputStream dis) throws IOException
    {
        return new SWWaypoint(dis.readUTF(), SWLocation.fromBytes(dis));
    }

}
