package noobbot.model;

import static java.util.Arrays.stream;

/**
 * Created by jereketonen on 4/16/14.
 */
public class CarMetrics {
    //using test track values for fallback because they're the closest we've got.
    private final double topSpeedFallbackValue = 10.0;
    private final double maxAccelertionFallbackValue = 0.2;
    private final double accelerationRatioFallbackValue = 0.02;

    private Track track;
    private Position currentPosition;
    private Position previousPosition;
    private double previousSpeed = 0;
    private double topspeed = 0;
    private double maxAcceleration = 0;
    private double accelerationRatio = 0;
    private double currentThrottle;
    private double previousThrottle;
    private boolean measuringAcceleration = false;
    private double previousAcceleration;

    public CarMetrics(Track track) {
        this.track = track;
    }

    public void update(Metric metric) {
        previousAcceleration = getCurrentAcceleration();
        previousSpeed = getCurrentSpeed();
        previousPosition = this.currentPosition;
        previousThrottle = this.currentThrottle;

        this.currentThrottle = metric.getThrottle();
        this.currentPosition = metric.getPosition();

        measureTopspeed();
    }

    //Ugly but works
    private void measureTopspeed() {

        //start measuringAcceleration if we start hitting full throttle from zero
        if(getCurrentSpeed() == 0 && this.currentThrottle == 1.0) {
            System.out.println("Metrics: Starting to measure acceleration.");
            measuringAcceleration = true;
        }

        if(currentThrottle != 1.0) {
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

        return maxAccelertionFallbackValue;
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

            while(speed > targetSpeed) {
                breakingDistance += speed;

                acceleration = 0-speed * getAccelerationRatio();
                speed = getSpeed(speed, acceleration);
            }

            return breakingDistance;
        }

        return 0;
    }
}
