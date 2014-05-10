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

    private boolean pieceHasSwitch(int i) {
        return segment.getPieces()[i].hasSwitch();
    }

    private boolean isPieceRightCorner(int i) {
        return hasPiece(i) && segment.getPieces()[i].getAngle() > 0;
    }

    private boolean isPieceLeftCorner(int i) {
        return hasPiece(i) && segment.getPieces()[i].getAngle() < 0;
    }

    private boolean hasPiece(int i) {
        return segment.getPieces().length > i;
    }

    public boolean hasSwitchWhenTurningIntoARightCorner() {
        return pieceHasSwitch(0) && isPieceRightCorner(0) && isPieceRightCorner(1);
    }

    public boolean hasSwitchJustBeforeRightCorner() {
        return pieceHasSwitch(0) && isPieceRightCorner(1);
    }

    public boolean hasSwitchWhenTurningIntoALeftCorner() {
        return pieceHasSwitch(0) && isPieceLeftCorner(0) && isPieceLeftCorner(1);
    }

    public boolean hasSwitchJustBeforeLeftCorner() {
        return pieceHasSwitch(0) && isPieceLeftCorner(1);
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

    public boolean endsInRightCorner() {
        return segment.getLastPiece().getAngle() > 0;
    }

    public boolean endsInLeftCorner() {
        return segment.getLastPiece().getAngle() < 0;
    }

}
