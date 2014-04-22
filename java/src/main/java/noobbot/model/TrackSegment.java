package noobbot.model;

import static java.util.Arrays.stream;

/**
 * Created by Saulis on 21/04/14.
 */
public class TrackSegment {
    private Piece[] pieces;

    public TrackSegment(Piece[] pieces) {
        this.pieces = pieces;
    }

    public double getSegmentLength(Lane lane, Lane previousDrivingLane) {
        double totalDistanceOfPieces = stream(pieces).mapToDouble(p -> p.getLength(lane)).sum();

        return totalDistanceOfPieces + getDistanceAddedBySwitching(lane, previousDrivingLane);
    }

    public double getSegmentDrivingTime(Lane lane, Lane previousDrivingLane) {
        double totalDrivingTimeOfPieces = stream(pieces).mapToDouble(p -> p.getDrivingTime(lane, previousDrivingLane)).sum();

        return totalDrivingTimeOfPieces + getDrivingTimeAddedBySwitching(lane, previousDrivingLane);
    }

    private double getDrivingTimeAddedBySwitching(Lane lane, Lane previousDrivingLane) {
        double distanceAddedBySwitching = getDistanceAddedBySwitching(lane, previousDrivingLane);

        return distanceAddedBySwitching / pieces[0].getTargetSpeed(lane, previousDrivingLane);
    }

    private double getDistanceAddedBySwitching(Lane lane, Lane previousDrivingLane) {
        //Switch should always be the first piece of a segment.
        if(lane != previousDrivingLane) {
            double length = pieces[0].getLength(lane);
            double width = lane.getLaneWidth();

            return Math.sqrt(Math.pow(length, 2) + Math.pow(width, 2)) - length;
        } else {
            return 0.0;
        }
    }

    public boolean containsPiece(int pieceIndex) {
        for(Piece piece : pieces) {
            if(piece.getNumber() == pieceIndex) {
                return true;
            }
        }

        return false;
    }

    public boolean hasCorners() {
        for(Piece p : pieces) {
            if(p.getAngle() != 0) {
                return true;
            }
        }

        return false;
    }
}
