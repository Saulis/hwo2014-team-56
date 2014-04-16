package noobbot.model;

public class StraightPiece extends GenericPiece {

    private double length;

    public StraightPiece(double length, int pieceNumber) {
        super(pieceNumber, 0, 0);
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
