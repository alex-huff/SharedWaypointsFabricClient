package phonis.survival.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RTLocation implements RTSerializable {

    public final RTDimension dimension;
    public final double x;
    public final double y;
    public final double z;

    public RTLocation(RTDimension dimension, double x, double y, double z) {
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof RTLocation otherLocation) {
            return this.dimension.equals(otherLocation.dimension) &&
                this.x == otherLocation.x &&
                this.y == otherLocation.y &&
                this.z == otherLocation.z;
        }

        return false;
    }

    @Override
    public void toBytes(DataOutputStream dos) throws IOException {
        dos.writeByte(this.dimension.ordinal());
        dos.writeDouble(this.x);
        dos.writeDouble(this.y);
        dos.writeDouble(this.z);
    }

    public static RTLocation fromBytes(DataInputStream dis) throws IOException {
        return new RTLocation(
            RTDimension.values()[dis.readByte()],
            dis.readDouble(),
            dis.readDouble(),
            dis.readDouble()
        );
    }

}
