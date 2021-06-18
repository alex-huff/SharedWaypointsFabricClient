package phonis.survival.networking;

import java.io.Serializable;
import java.util.UUID;

public class RTWaypoint implements Serializable {

    public final String name;
    public final UUID world;
    public final RTDimension dimension;
    public final int x;
    public final int y;
    public final int z;

    public RTWaypoint(String name, UUID world, RTDimension dimension, int x, int y, int z) {
        this.name = name;
        this.world = world;
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
    }

}