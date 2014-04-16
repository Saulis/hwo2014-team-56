package noobbot.model;

public class LaneImpl implements Lane {

    private double distanceFromCenter;

    public LaneImpl(double distanceFromCenter) {

        this.distanceFromCenter = distanceFromCenter;
    }

    @Override
    public double getDistanceFromCenter() {
        return distanceFromCenter;
    }
}
