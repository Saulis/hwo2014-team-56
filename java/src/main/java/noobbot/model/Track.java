package noobbot.model;

import java.util.List;

public class Track {
	private List<Piece> pieces;

    public Track(List<Piece> pieces) {
        this.pieces = pieces;
	}

    public Double getDistanceBetween(Position position1, Position position2) {
        Piece startingPiece = getPieceForPosition(position1);
        double distance = startingPiece.getDistanceFrom(position1);
                
        Piece nextPiece;
        for (nextPiece = getPieceAfter(startingPiece); ! nextPiece.contains(position2); nextPiece = getPieceAfter(nextPiece)) {
            distance += nextPiece.getLength(position1.getLaneNumber());
        };
        
        distance += nextPiece.getDistanceTo(position2);
        
        return distance;
    }

    private Piece getPieceAfter(Piece precedingPiece) {
        int nextPieceIndex = precedingPiece.getNumber() + 1;
        return pieces.get(nextPieceIndex % pieces.size());
    }

    private Piece getPieceForPosition(Position position) {
        return pieces.stream().filter(p -> p.contains(position)).findFirst().get();
    }
}
