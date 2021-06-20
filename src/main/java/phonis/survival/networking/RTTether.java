package phonis.survival.networking;

import java.io.Serializable;

public abstract class RTTether implements Serializable {

    public final RTDimension dimension;
    private final double x;
    private final double y;
    private final double z;

    public RTTether(RTDimension dimension, double x, double y, double z) {
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    @Override
    public abstract boolean equals(Object other);

}
