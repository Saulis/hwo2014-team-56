package noobbot.model;

public class AnglePiece extends GenericPiece {

    private double radius;
    private double angle;
    private TargetAngleSpeed tas;
    private double maxSlipAngle;
    private double speedModifier = 1;

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
        System.out.println("Speed modifier: " + speedModifier);

        return Math.sqrt(CarMetrics.getAngleAcceleration() * speedModifier * getEffectiveRadius(lane));
    }

    private double getCornerLength(double offsetFromCenter) {
        double circleLength = Math.PI * 2 * getEffectiveRadius(offsetFromCenter);

        return circleLength * Math.abs(angle) / 360;
    }

    public double getEffectiveRadius(Lane lane) {
        return getEffectiveRadius(lane.getDistanceFromCenter());
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

    @Override
    public void complete() {
//        if (tas.isCalibrating()) {
//            return;
//        }
//        System.out.println("COMPLETE");
        if (maxSlipAngle > 55) {
//            speedModifier += (45-maxSlipAngle)/9/100;
//            speedModifier -= 0.01;
//            System.out.println("MODIFIER:" + speedModifier);
        }
    }

    @Override
    public void calibrate(double slipAngle) {
//        if (tas.isCalibrating()) {
//            return;
//        }
        maxSlipAngle = Math.max(maxSlipAngle, slipAngle);
    }

    @Override
    public void modifySpeed(double maxSlipAngle, int ticksInCorner) {
//        speedModifier += 0.25;
        double targetSlipAngle = 50;
        int ticksUntilMaxSlipAngle = 40 - ticksInCorner;
        if(ticksUntilMaxSlipAngle > 0) {
            targetSlipAngle = targetSlipAngle - ticksUntilMaxSlipAngle / 4.0;
        }
        speedModifier += (45-maxSlipAngle)/100;
    }
}
