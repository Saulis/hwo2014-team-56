package noobbot.model;

public class AnglePiece extends GenericPiece {

    private double cornerLength;

    public AnglePiece(double radius, double angle, int pieceNumber) {
        super(pieceNumber, angle, radius);
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
}
