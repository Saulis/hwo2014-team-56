package noobbot.model;

/**
 * Created by Saulis on 21/04/14.
 */
public class TrackRouteSegment {
    private final Lane drivingLane;
    private final TrackSegment segment;
    private Lane previousDrivingLane;

    public TrackRouteSegment(Lane drivingLane, TrackSegment segment) {

        this.drivingLane = drivingLane;
        this.segment = segment;
    }

    public double getSegmentLength() {
        return segment.getSegmentLength(drivingLane, previousDrivingLane);
    }

    public double getSegmentDrivingTime() {
        return segment.getSegmentDrivingTime(drivingLane, previousDrivingLane);
    }

    public Lane getDrivingLane() {
        return drivingLane;
    }

    public boolean containsPiece(int pieceIndex) {
        return segment.containsPiece(pieceIndex);
    }

    public boolean hasCorners() {
        return segment.hasCorners();
    }

    public void setPreviousDrivingLane(Lane previousDrivingLane) {
        this.previousDrivingLane = previousDrivingLane;
    }
}
