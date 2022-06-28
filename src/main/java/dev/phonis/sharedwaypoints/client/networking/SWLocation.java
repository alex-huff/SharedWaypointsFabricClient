package dev.phonis.sharedwaypoints.client.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public
class SWLocation implements SWSerializable
{

    public final SWDimension dimension;
    public final double      x;
    public final double      y;
    public final double      z;

    public
    SWLocation(SWDimension dimension, double x, double y, double z)
    {
        this.dimension = dimension;
        this.x         = x;
        this.y         = y;
        this.z         = z;
    }

    @Override
    public
    boolean equals(Object other)
    {
        if (other instanceof SWLocation otherLocation)
        {
            return this.dimension.equals(otherLocation.dimension) && this.x == otherLocation.x &&
                   this.y == otherLocation.y && this.z == otherLocation.z;
        }

        return false;
    }

    @Override
    public
    void toBytes(DataOutputStream dos) throws IOException
    {
        dos.writeByte(this.dimension.ordinal());
        dos.writeDouble(this.x);
        dos.writeDouble(this.y);
        dos.writeDouble(this.z);
    }

    public static
    SWLocation fromBytes(DataInputStream dis) throws IOException
    {
        return new SWLocation(SWDimension.fromBytes(dis), dis.readDouble(), dis.readDouble(), dis.readDouble());
    }

}
