package phonis.survival.networking;

public class RTUnsupported implements RTPacket {

    public final int protocolVersion;

    public RTUnsupported(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

}
