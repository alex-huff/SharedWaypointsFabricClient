package phonis.survival.networking;

public class Packets {

    public static class Out {
        public static final byte RTRegisterID = 0x00;
        public static final byte RTFICID = 0x01;
        public static final byte RTFICClearID = 0x02;
        public static final byte RTSTogID = 0x03;
        public static final byte RTSTPToHoveredWaypointID = 0x04;
        public static final byte RTTetherClearID = 0x05;
        public static final byte RTTetherOnHoveredWaypointID = 0x06;
    }

    public static class In {
        public static final byte RTUnsupportedID = 0x07;
        public static final byte RTWaypointInitializeID = 0x08;
        public static final byte RTWaypointUpdateID = 0x09;
        public static final byte RTWaypointRemoveID = 0x0A;
        public static final byte RTTetherUpdateID = 0x0B;
        public static final byte RTTetherRemoveID = 0x0C;
        public static final byte RTChestFindSessionID = 0x0D;
        public static final byte RTChestFindSessionClearID = 0x0E;
    }

}
