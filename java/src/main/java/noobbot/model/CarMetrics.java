package noobbot.model;


/**
 * Created by jereketonen on 4/16/14.
 */
public class CarMetrics {
    //using test track values for fallback because they're the closest we've got.
    private final double topSpeedFallbackValue = 10.0;
    private final double maxAccelerationFallbackValue = 0.2;
    private final double accelerationRatioFallbackValue = 0.02;

    /*
        Sales Notes:
            Jos ratio on 0.98 / 0.02, niin nopeudet voi myös laskea näin:
            kiihtyen nopeus * 1.98, hidastuen nopeus * 0.98

     */

    private Track track;
    private SlipAngle slipAngle;
    private Navigator navigator;
    private TargetAngleSpeed targetAngleSpeed;
    private Position currentPosition;
    private Position previousPosition;
    private double previousSpeed = 0;
    private double topspeed = 0;
    private double maxAcceleration = 0;
    private double accelerationRatio = 0;
    private double currentThrottle;
    private boolean measuringAcceleration = false;
    private double previousAcceleration;

    private double previousAngleAcceleration = 0;
    private double maxSlipAngle = 0;
    private double maxAngleAcceleration = 0;
    private static double targetAngleAcceleration = 0.48;
    private double targetSlipAngle = 0;
    private int ticksInCorner = 0;
    private int ticksUntilMaxSlipAngle = 40;


    public CarMetrics(Track track, Navigator navigator, TargetAngleSpeed tas) {
        this.track = track;
        this.navigator = navigator;
        this.targetAngleSpeed = tas;
        this.slipAngle = new SlipAngle(track);
    }

    public void update(Metric metric) {
        previousAcceleration = getCurrentAcceleration();
        previousSpeed = getCurrentSpeed();
        previousPosition = this.currentPosition;

        this.currentThrottle = metric.getThrottle();
        this.currentPosition = metric.getPosition();
        slipAngle.update(metric.getPosition());

        this.targetAngleSpeed.calibrate(metric.getPosition(), getCurrentPiece(), slipAngle, getCurrentSpeed(), currentThrottle);
        
        measureTopspeed();
        measureAngleAcceleration();

        System.out.println(String.format("Metrics - P: %s, Lane: %s, D: %s (%s)", currentPosition.getPieceNumber(), currentPosition.getLane().getDistanceFromCenter(), currentPosition.getInPieceDistance(), track.getPiece(currentPosition).getLength(currentPosition.getLane())));
        System.out.println(String.format("Metrics - Speed: %s, Slip: %s, S.Velocity %s", getCurrentSpeed(), getSlipAngle().getValue(), slipAngle.getSlipChangeVelocity()));
        System.out.println(String.format("Metrics - Prev.Angle Acc.: %s, Curr.Angle.Acc %s, Max.Angle Acc. %s, Max.Slip: %s, Ticks: %s", previousAngleAcceleration, targetAngleAcceleration, maxAngleAcceleration, maxSlipAngle, ticksInCorner));
    }

    private void measureAngleAcceleration() {
        if(enteredAnglePiece()) {
            previousAngleAcceleration = slipAngle.getAcceleration();
            ticksInCorner = 0;
        }

        if(getCurrentPiece().getAngle() != 0) {
            ticksInCorner++;
        }

        maxAngleAcceleration = Math.max(maxAngleAcceleration, slipAngle.getAcceleration());
        maxSlipAngle = Math.max(maxSlipAngle, Math.abs(getSlipAngle().getValue()));

        if(exitedAnglePiece()) {
            if(ticksInCorner > ticksUntilMaxSlipAngle) {
                if(maxSlipAngle >= 55) {
                    targetAngleAcceleration -= 0.01;
                } else if(maxSlipAngle > 50 && maxSlipAngle < 55 && Math.abs(targetAngleAcceleration - maxAngleAcceleration) <= 0.025) {
                    targetAngleAcceleration = maxAngleAcceleration;
                } else if(maxSlipAngle <= 50) {
                    targetAngleAcceleration += 0.005;
                }
            }
            maxSlipAngle = 0;
            maxAngleAcceleration = 0;
        }
    }

    private boolean enteredAnglePiece() {
        if(previousPosition != null) {
            return getCurrentPiece().getAngle() != 0 && track.getPiece(previousPosition).getAngle() == 0;
        }

        return getCurrentPiece().getAngle() != 0;
    }

    private boolean exitedAnglePiece() {
        if(previousPosition != null) {
            return getCurrentPiece().getAngle() == 0 && track.getPiece(previousPosition).getAngle() != 0;
        }

        return false;
    }

    public static double getAngleAcceleration() {
        return targetAngleAcceleration;
    }

    public SlipAngle getSlipAngle() {
        return slipAngle;
    }

    //Ugly but works
    private void measureTopspeed() {

        //start measuringAcceleration if we start hitting full throttle from zero
        if(getCurrentSpeed() == 0 && this.currentThrottle == 1.0 && topspeed == 0) {
            System.out.println("Metrics: Starting to measure acceleration.");
            measuringAcceleration = true;
        }

        if(measuringAcceleration && currentThrottle != 1.0) {
            System.out.println("Metrics: Stopping acceleration measuring.");
            measuringAcceleration = false;
        }

        if(measuringAcceleration && previousAcceleration > 0) {
            measuringAcceleration = false;

            accelerationRatio = 1 - getCurrentAcceleration() / previousAcceleration;
            maxAcceleration = previousAcceleration;
            topspeed = previousAcceleration / accelerationRatio;

            System.out.println(String.format("Metrics: Acceleration measured. Acceleration ratio: %s, Max Acceleration: %s, Topspeed: %s", accelerationRatio, maxAcceleration, topspeed));
        }
    }

    public double getCurrentSpeed() {
        if(!hasPreviousPosition()) {
            return 0;
        }

        if(previousPositionWasOnCurrentPiece()) {
            return currentPosition.getInPieceDistance() - previousPosition.getInPieceDistance();
        } else {
            return currentPosition.getInPieceDistance() + (getPreviousPieceLength() - previousPosition.getInPieceDistance());
        }
    }

    private double getPreviousPieceLength() {
        return getPieceLength(previousPosition);
    }

    private boolean previousPositionWasOnCurrentPiece() {
        return previousPosition.getPieceNumber() == currentPosition.getPieceNumber();
    }

    private boolean hasPreviousPosition() {
        return previousPosition != null;
    }

    private double getPieceLength(Position piecePosition) {
        Piece piece = track.getPiece(piecePosition);

        return piece.getLength(piecePosition.getLane());
    }

    public double getCurrentAcceleration() {
        return getCurrentSpeed() - previousSpeed;
    }

    public double getTopspeed() {
        if(topspeed > 0) {
            return topspeed;
        }

        return topSpeedFallbackValue;
    }

    public double getAccelerationRatio() {
        if(accelerationRatio > 0) {
            return accelerationRatio;
        }

        return accelerationRatioFallbackValue;
    }

    public double getMaxAcceleration() {
        if(maxAcceleration > 0) {
            return maxAcceleration;
        }

        return maxAccelerationFallbackValue;
    }

    public double getAcceleration(double currentSpeed, double currentThrottle) {
        return (currentThrottle * getTopspeed() - currentSpeed) * getAccelerationRatio();
    }

    public double getSpeed(double currentSpeed, double acceleration) {
        return currentSpeed + acceleration;
    }

    public double getBrakingDistance(double currentSpeed, double targetSpeed, double currentThrottle) {
        double acceleration = getAcceleration(currentSpeed, currentThrottle);
        double speed = getSpeed(currentSpeed, acceleration);

        if(targetSpeed < currentSpeed) {
            double breakingDistance = currentSpeed;

            //Extra braking distance when running with turbo
            if(navigator.isTurboActive()) {
                breakingDistance += currentSpeed * 1.5;
            }

            while(speed > targetSpeed + 0.05) {
                breakingDistance += speed;

                //0.0 is for braking throttle
                acceleration = getAcceleration(speed, 0.0);
                speed = getSpeed(speed, acceleration);
            }

            return breakingDistance;
        }

        return 0;
    }

    public Piece getCurrentPiece() {
        return track.getPiece(currentPosition);
    }
}
