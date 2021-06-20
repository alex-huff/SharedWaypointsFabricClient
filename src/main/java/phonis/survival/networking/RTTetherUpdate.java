package phonis.survival.networking;

public class RTTetherUpdate implements RTPacket {

    public final RTTether toUpdate;

    public RTTetherUpdate(RTTether toUpdate) {
        this.toUpdate = toUpdate;
    }

}
