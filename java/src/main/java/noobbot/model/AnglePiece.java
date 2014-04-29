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
    	if (speedModifier > 1) {
    		System.out.println("Speed modifier: " + speedModifier);
    	}
    	
        return Math.sqrt(CarMetrics.getAngleAcceleration() * getEffectiveRadius(lane));// * speedModifier;
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
        if (tas.isCalibrating()) {
            return;
        }
        if (maxSlipAngle < 50) {
            speedModifier += (50-maxSlipAngle)/9/100;
        }
    }

    @Override
    public void calibrate(double slipAngle) {
        if (tas.isCalibrating()) {
            return;
        }
        maxSlipAngle = Math.max(maxSlipAngle, slipAngle);
    }
}
