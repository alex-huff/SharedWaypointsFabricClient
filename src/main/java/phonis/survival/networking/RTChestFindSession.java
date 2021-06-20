package phonis.survival.networking;

public class RTChestFindSession implements RTPacket {

    public final RTChestFindLocation closest;
    public final RTChestFindLocation best;
    public final RTChestFindLocation most;

    public RTChestFindSession(RTChestFindLocation closest, RTChestFindLocation best, RTChestFindLocation most) {
        this.closest = closest;
        this.best = best;
        this.most = most;
    }

}
