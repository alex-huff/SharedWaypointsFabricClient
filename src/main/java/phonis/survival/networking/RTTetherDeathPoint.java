package phonis.survival.networking;

public class RTTetherDeathPoint extends RTTether {

    public RTTetherDeathPoint(RTLocation location) {
        super(location);
    }

    @Override
    public boolean equals(Object other) {
        // death locations do not need to be overridden
        if (other instanceof RTTetherDeathPoint) {
            return ((RTTetherDeathPoint) other).location.equals(this.location);
        }

        return false;
    }

}
