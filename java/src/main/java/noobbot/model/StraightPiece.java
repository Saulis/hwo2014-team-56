package noobbot.model;

public class StraightPiece extends GenericPiece {

    private double length;
    int pieceNumber;

    public StraightPiece(double length, int pieceNumber) {
        super(pieceNumber);
        this.length = length;
    }
    
    @Override
    public Double getDistanceFrom(Position position) {
        return length - position.getInPieceDistance();
    }

    @Override
    public double getLength(int laneNumber) {
        return length;
    }

}
