package noobbot.model;

public class LaneImpl implements Lane {

    private int index;
    private double distanceFromCenter;
    private double laneWidth;

    public LaneImpl(int index, double distanceFromCenter, double laneWidth) {
        this.index = index;

        this.distanceFromCenter = distanceFromCenter;
        this.laneWidth = laneWidth;
    }

    @Override
    public double getDistanceFromCenter() {
        return distanceFromCenter;
    }

    @Override
    public double getLaneWidth() {
        return laneWidth;
    }

    public int getIndex() {
        return index;
    }
}
