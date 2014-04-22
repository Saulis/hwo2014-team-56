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

    public double getSegmentLength(Lane lane, boolean switchIsUsed) {
        double totalDistanceOfPieces = stream(pieces).mapToDouble(p -> p.getLength(lane)).sum();

        return totalDistanceOfPieces + getDistanceAddedBySwitching(lane, switchIsUsed);
    }

    private double getDistanceAddedBySwitching(Lane lane, boolean switchIsUsed) {
        //Switch should always be the first piece of a segment.
        if(switchIsUsed && pieces[0].hasSwitch()) {
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
}
