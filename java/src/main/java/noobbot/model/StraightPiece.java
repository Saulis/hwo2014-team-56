package noobbot.model;

public class StraightPiece extends GenericPiece {

    private double length;

    public StraightPiece(double length, int pieceNumber, boolean hasSwitch) {
        super(pieceNumber, hasSwitch);
        this.length = length;
    }
    
    @Override
    public Double getDistanceFrom(Position position) {
        return length - position.getInPieceDistance();
    }

    @Override
    public double getLength(Lane lane) {
        return length;
    }

    @Override
    public double getTargetSpeed(Lane lane, Lane previousDrivingLane) {
        return 10;
    }

    @Override
    public double getAngle() {return 0;}
}
