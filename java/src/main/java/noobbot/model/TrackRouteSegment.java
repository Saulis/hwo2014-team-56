package noobbot.model;

import java.util.Optional;

/**
 * Created by Saulis on 21/04/14.
 */
public class TrackRouteSegment {
    private final Lane drivingLane;
    private final TrackSegment segment;
    private boolean switchIsUsed = false;

    public TrackRouteSegment(Lane drivingLane, TrackSegment segment) {

        this.drivingLane = drivingLane;
        this.segment = segment;
    }

    public double getSegmentLength() {
        return segment.getSegmentLength(drivingLane, switchIsUsed);
    }

    public double getSegmentDrivingTime() {
        return segment.getSegmentDrivingTime(drivingLane, switchIsUsed);
    }

    public Lane getDrivingLane() {
        return drivingLane;
    }

    public boolean containsPiece(int pieceIndex) {
        return segment.containsPiece(pieceIndex);
    }

    public void setSwitchIsUsed(boolean switchIsUsed) {
        this.switchIsUsed = switchIsUsed;
    }

    public boolean hasCorners() {
        return segment.hasCorners();
    }

    public Optional<Piece> getPiece(int pieceIndex) {
        return segment.getPiece(pieceIndex);
    }

    public Piece getFirstPiece() {
        return segment.getFirstPiece();
    }
}
