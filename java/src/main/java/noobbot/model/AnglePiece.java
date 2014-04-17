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
        double offsetFromCenter = position.getLane().getDistanceFromCenter();
        return getCornerLength(offsetFromCenter) - position.getInPieceDistance();
    }

    @Override
    public double getLength(Lane lane) {
        return getCornerLength(lane.getDistanceFromCenter());
    }
    
    private double getCornerLength(double offsetFromCenter) {
        double circleLength = Math.PI * 2 * (radius + offsetFromCenter);
        cornerLength = circleLength * Math.abs(angle) / 360;
        return cornerLength;
    }

    public double getRadius() { return radius;}
    public double getAngle() {return angle;}

}
