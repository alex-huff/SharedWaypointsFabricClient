package phonis.survival.networking;

import java.util.UUID;

public class RTTetherPlayer extends RTTether {

    public final UUID uuid;

    public RTTetherPlayer(UUID uuid, RTLocation location) {
        super(location);

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
