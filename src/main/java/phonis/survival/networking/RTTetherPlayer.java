package phonis.survival.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class RTTetherPlayer extends RTTether {

    public static final byte ID = 0x01;

    public final UUID uuid;

    public RTTetherPlayer(UUID uuid, RTLocation location) {
        super(location);

        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof RTTetherPlayer otherTetherPlayer) {
            return otherTetherPlayer.uuid.equals(this.uuid);
        }

        return false;
    }

    @Override
    public byte getTetherID() {
        return RTTetherPlayer.ID;
    }

    @Override
    protected void writeTether(DataOutputStream dos) throws IOException {
        dos.writeUTF(this.uuid.toString());
        this.location.toBytes(dos);
    }

    public static RTTetherPlayer fromBytes(DataInputStream dis) throws IOException {
        return new RTTetherPlayer(
            UUID.fromString(dis.readUTF()),
            RTLocation.fromBytes(dis)
        );
    }

}
