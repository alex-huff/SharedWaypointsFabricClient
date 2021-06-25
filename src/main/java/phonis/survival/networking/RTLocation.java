package phonis.survival.networking;

import java.io.Serializable;

public class RTLocation implements Serializable {

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
        if (other instanceof RTLocation) {
            RTLocation otherLocation = (RTLocation) other;

            return this.dimension.equals(otherLocation.dimension) &&
                this.x == otherLocation.x &&
                this.y == otherLocation.y &&
                this.z == otherLocation.z;
        }

        return false;
    }

}
