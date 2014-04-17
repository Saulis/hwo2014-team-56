package noobbot.model;

import java.util.List;

public class Track {
	private List<Piece> pieces;
    private List<Lane> lanes;

    public Track(List<Piece> pieces, List<Lane> lanes) {
        this.pieces = pieces;
        this.lanes = lanes;
    }

    public Double getDistanceBetween(Position position1, Position position2) {
        Piece startingPiece = getPieceForPosition(position1);
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

    private Piece getPieceAfter(Piece precedingPiece) {
        int nextPieceIndex = precedingPiece.getNumber() + 1;
        return pieces.get(nextPieceIndex % pieces.size());
    }

    private Piece getPieceForPosition(Position position) {
        return pieces.stream().filter(p -> p.contains(position)).findFirst().get();
    }

    public Piece getPiece(Position position) {
        return pieces.get(position.getPieceNumber());
    }
}
