package noobbot.model;

/**
 * Created by Saulis on 21/04/14.
 */
public class TrackRouteSegment {
    private final Lane drivingLane;
    private final TrackSegment segment;

    public TrackRouteSegment(Lane drivingLane, TrackSegment segment) {

        this.drivingLane = drivingLane;
        this.segment = segment;
    }

    public double getSegmentLength() {
        return segment.getSegmentLength(drivingLane);
    }

    public Lane getDrivingLane() {
        return drivingLane;
    }

    public boolean containsPiece(int pieceIndex) {
        return segment.containsPiece(pieceIndex);
    }
}
