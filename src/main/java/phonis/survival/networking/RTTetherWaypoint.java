package phonis.survival.networking;

public class RTTetherWaypoint extends RTTether {

    public final String waypoint;

    public RTTetherWaypoint(String waypoint, RTDimension dimension, double x, double y, double z) {
        super(dimension, x, y, z);

        this.waypoint = waypoint;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof RTTetherWaypoint) {
            RTTetherWaypoint otherTetherWaypoint = (RTTetherWaypoint) other;

            return otherTetherWaypoint.waypoint.equals(this.waypoint);
        }

        return false;
    }

}
