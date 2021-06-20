package phonis.survival.networking;

public class RTTetherRemove implements RTPacket {

    public final RTTether toRemove;

    public RTTetherRemove(RTTether toRemove) {
        this.toRemove = toRemove;
    }

}
