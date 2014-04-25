package noobbot.model;

public class AnglePiece extends GenericPiece {

    private double radius;
    private double angle;
    private TargetAngleSpeed tas;

    public AnglePiece(double radius, double angle, int pieceNumber, boolean hasSwitch, TargetAngleSpeed tas) {
        super(pieceNumber, hasSwitch);
        this.radius = radius;
        this.angle = angle;
        this.tas = tas;
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

    @Override
    public double getTargetSpeed(Lane lane)
    {
        return getLength(lane) / Math.abs(angle) / tas.getValue();
    }

    private double getCornerLength(double offsetFromCenter) {
        double circleLength = Math.PI * 2 * getEffectiveRadius(offsetFromCenter);

        return circleLength * Math.abs(angle) / 360;
    }

    private double getEffectiveRadius(double offsetFromCenter) {
        if(isLeftTurn()) {
            return getRadius() + offsetFromCenter;
        }

        return getRadius() - offsetFromCenter;
    }

    private boolean isLeftTurn() {
        return getAngle() < 0;
    }

    public double getRadius() { return radius;}
    public double getAngle() {return angle;}

}
