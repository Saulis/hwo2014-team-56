package noobbot.model;

public class StraightPiece implements Piece {

    private double length;
    private int pieceNumber;

    public StraightPiece(double length, int pieceNumber) {
        this.length = length;
        this.pieceNumber = pieceNumber;
    }
    
    @Override
    public Double getDistanceTo(Position position) {
        return position.getInPieceDistance();
    }

    @Override
    public Double getDistanceFrom(Position position) {
        return length - position.getInPieceDistance();
    }

    @Override
    public boolean contains(Position position) {
        return pieceNumber == position.getPieceNumber();
    }

    @Override
    public int getNumber() {
        return pieceNumber;
    }

    @Override
    public double getLength(int laneNumber) {
        return length;
    }

}
