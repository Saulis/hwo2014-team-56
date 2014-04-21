package noobbot.model;

public class LaneImpl implements Lane {

    private int index;
    private double distanceFromCenter;

    public LaneImpl(int index, double distanceFromCenter) {
        this.index = index;

        this.distanceFromCenter = distanceFromCenter;
    }

    @Override
    public double getDistanceFromCenter() {
        return distanceFromCenter;
    }

    public int getIndex() {
        return index;
    }
}
