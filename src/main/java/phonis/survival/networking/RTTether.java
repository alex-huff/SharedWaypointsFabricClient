package phonis.survival.networking;

import java.io.Serializable;

public abstract class RTTether implements Serializable {

    public final RTLocation location;

    public RTTether(RTLocation location) {
        this.location = location;
    }

    @Override
    public abstract boolean equals(Object other);

}
