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
    public double getTargetSpeed(Lane lane) {
        return 10;
    }

    @Override
    public double getAngle() {return 0;}

    @Override
    public double getRadius() {
        return 0;
    }

    @Override
    public void complete() {
    }

    @Override
    public void calibrate(double slipAngle) {
    }

    @Override
    public void modifySpeed(double maxSlipAngle) {

    }
}
