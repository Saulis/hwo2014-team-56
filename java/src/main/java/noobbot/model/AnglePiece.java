package noobbot.model;

public class AnglePiece extends GenericPiece {

    private double cornerLength;
    private double radius;
    private double angle;

    public AnglePiece(double radius, double angle, int pieceNumber) {
        super(pieceNumber);
        this.radius = radius;
        this.angle = angle;
    }

    @Override
    public Double getDistanceFrom(Position position) {
        return getCornerLength() - position.getInPieceDistance();
    }

    @Override
    public double getLength(int laneNumber) {
        return getCornerLength();
    }
    
    private double getCornerLength() {
        if (cornerLength == 0) {
            double circleLength = Math.PI * 2 * radius;
            cornerLength = circleLength * angle / 360;
        }
        return cornerLength;
    }

    public double getRadius() { return radius;}
    public double getAngle() {return angle;}
}