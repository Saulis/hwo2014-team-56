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

    public double getSegmentLength(Lane lane) {
        return stream(pieces).mapToDouble(p -> p.getLength(lane)).sum();
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
