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
    private double previousSlipVelocity;
    private double previousSlipAngle;

    public CarMetrics(Track track, TargetAngleSpeed tas) {
        this.track = track;
        this.targetAngleSpeed = tas;
        this.slipAngle = new SlipAngle(track);
    }

    public void update(Metric metric) {
        slipAngle.update(metric.getPosition());
        previousAcceleration = getCurrentAcceleration();
        previousSpeed = getCurrentSpeed();
        previousSlipVelocity = getSlipVelocity();
        previousSlipAngle = slipAngle.getValue();
        previousPosition = this.currentPosition;

        this.currentThrottle = metric.getThrottle();
        this.currentPosition = metric.getPosition();

        this.targetAngleSpeed.calibrate(metric.getPosition(), getCurrentPiece(), slipAngle, getCurrentSpeed(), currentThrottle);
        
        measureTopspeed();
    }

    public double getSlipAcceleration() {
        return getSlipVelocity() - previousSlipVelocity;
    }

    public double getSlipVelocity() {
        if(!hasPreviousPosition()) {
            return 0;
        }

        double velocity = getSlipAngle() - previousSlipAngle;

        if(getSlipAngle() < 0) {
            return velocity * -1;
        }

        return velocity;
    }

    public double getSlipAngle() {
        if(currentPosition == null) {
            return 0;
        }
            return currentPosition.getSlipAngle();
    }

    //Ugly but works
    private void measureTopspeed() {

        //start measuringAcceleration if we start hitting full throttle from zero
        if(getCurrentSpeed() == 0 && this.currentThrottle == 1.0) {
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
            double breakingDistance = currentSpeed * 2;

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
