package noobbot.model;

import java.util.List;

public class Track {
    private final Piece longestStraight;
    private List<Piece> pieces;
    private List<Lane> lanes;

    public Track(List<Piece> pieces, List<Lane> lanes) {
        this.pieces = pieces;
        this.lanes = lanes;

        longestStraight = calculateLongestStraight();
    }

    private Piece calculateLongestStraight() {
        double longestLength = 0;
        Piece longest = null;

        for (Piece piece : getPieces()) {
            double length = getStraightLength(piece);
            if(longestLength < length) {
                longest = piece;
                longestLength = length;
            }
        }

        return longest;
    }

    public Piece getBeginningPieceOfLongestStraight() {
        return longestStraight;
    }

    public double getStraightLength(Piece piece) {
        if(piece.getAngle() != 0) {
            return 0;
        }

        double length = piece.getLength(lanes.get(0)); //lanes all have the same length on a straight piece
        Piece nextPiece = getPieceAfter(piece);
        while(nextPiece.getAngle() == 0) {
            length += nextPiece.getLength(lanes.get(0));
            nextPiece = getPieceAfter(nextPiece);
        }

        return length;
    }

    public Double getDistanceBetween(Position position1, Position position2) {
        Piece startingPiece = getPiece(position1);
        double distance = startingPiece.getDistanceFrom(position1);
                
        Piece nextPiece;
        for (nextPiece = getPieceAfter(startingPiece); ! nextPiece.contains(position2); nextPiece = getPieceAfter(nextPiece)) {
            distance += nextPiece.getLength(position1.getLane());
        };
        
        distance += nextPiece.getDistanceTo(position2);
        
        return distance;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public List<Lane> getLanes() {
        return lanes;
    }

    public Piece getPieceAfter(Piece precedingPiece) {
        int nextPieceIndex = precedingPiece.getNumber() + 1;
        return pieces.get(nextPieceIndex % pieces.size());
    }

    public Piece getPieceBefore(Piece piece) {
        int index = piece.getNumber() - 1;
        return pieces.get(index % pieces.size());
    }


    public Piece getPiece(Position position) {
        return pieces.get(position.getPieceNumber());
    }
}
