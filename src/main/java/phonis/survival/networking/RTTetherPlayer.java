package phonis.survival.networking;

import java.util.UUID;

public class RTTetherPlayer extends RTTether {

    public final UUID uuid;

    public RTTetherPlayer(UUID uuid, RTDimension dimension, double x, double y, double z) {
        super(dimension, x, y, z);

        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof RTTetherPlayer) {
            RTTetherPlayer otherTetherPlayer = (RTTetherPlayer) other;

            return otherTetherPlayer.uuid.equals(this.uuid);
        }

        return false;
    }

}
