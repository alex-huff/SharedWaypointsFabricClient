package phonis.survival.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RTChestFindSessionClear implements RTPacket {

    @Override
    public byte getID() {
        return Packets.In.RTChestFindSessionClearID;
    }

    @Override
    public void toBytes(DataOutputStream dos) throws IOException {

    }

    public static RTChestFindSessionClear fromBytes(DataInputStream dis) {
        return new RTChestFindSessionClear();
    }

}
