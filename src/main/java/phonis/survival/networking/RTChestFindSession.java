package phonis.survival.networking;

public class RTChestFindSession implements RTPacket {

    public final RTLocation closest;
    public final RTLocation best;
    public final RTLocation most;

    public RTChestFindSession(RTLocation closest, RTLocation best, RTLocation most) {
        this.closest = closest;
        this.best = best;
        this.most = most;
    }

}
