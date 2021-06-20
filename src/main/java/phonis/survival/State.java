package phonis.survival;

import phonis.survival.networking.RTTether;
import phonis.survival.networking.RTWaypoint;

import java.util.List;

public class State {

    public static volatile List<RTWaypoint> waypointState = null;
    public static volatile List<RTTether> tetherState = null;

}