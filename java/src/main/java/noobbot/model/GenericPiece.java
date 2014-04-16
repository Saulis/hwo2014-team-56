package noobbot.model;

public abstract class GenericPiece implements Piece {

    protected int pieceNumber;
    protected double angle;
    protected double radius;

    public GenericPiece(int pieceNumber, double angle, double radius) {
        this.angle = angle;
        this.pieceNumber = pieceNumber;
        this.radius = radius;
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
    public double getRadius() { return radius;}
    public double getAngle() {return angle;}
}
