package phonis.survival.networking;

import java.io.Serializable;

public class RTWaypoint implements Serializable {

    public final String name;
    public final RTLocation location;

    public RTWaypoint(String name, RTLocation location) {
        this.name = name;
        this.location = location;
    }

}
