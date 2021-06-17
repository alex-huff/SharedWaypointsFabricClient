package phonis.survival.networking;

import java.io.Serializable;
import java.util.UUID;

public class RTWaypoint implements Serializable {

    public final String name;
    public final UUID world;
    public final int x;
    public final int y;
    public final int z;

    public RTWaypoint(String name, UUID world, int x, int y, int z) {
        this.name = name;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

}