package noobbot.model;

public abstract class GenericPiece implements Piece {

    protected int pieceNumber;
    private boolean hasSwitch;
    protected double angle;
    protected double radius;

    public GenericPiece(int pieceNumber, boolean hasSwitch) {
        this.pieceNumber = pieceNumber;
        this.hasSwitch = hasSwitch;
    }

    public Double getDistanceTo(Position position) {
        return position.getInPieceDistance();
    }

    public boolean contains(Position position) {
        return pieceNumber == position.getPieceNumber();
    }

    public int getNumber() {
        return pieceNumber;
    }

    public boolean hasSwitch() {
        return hasSwitch;
    }

    public double getDrivingTime(Lane lane) {
        return getLength(lane) / getTargetSpeed(lane);
    }
}
