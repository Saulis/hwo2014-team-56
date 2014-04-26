package noobbot.model;

import static java.util.Arrays.stream;

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
    private double previousSlipVelocity;
    private double previousSlipAngle;

    private double previousAngleAcceleration = 0;
    private double maxSlipAngle = 0;
    private double maxAngleAcceleration = 0;
    private static double targetAngleAcceleration = 0.45;
    private double targetSlipAngle = 0;
    private int ticksInCorner = 0;


    public CarMetrics(Track track) {
        this.track = track;
    }

    public void update(Metric metric) {
        previousAcceleration = getCurrentAcceleration();
        previousSpeed = getCurrentSpeed();
        previousSlipVelocity = getSlipVelocity();
        previousSlipAngle = getSlipAngle();
        previousPosition = this.currentPosition;
        previousThrottle = this.currentThrottle;


        this.currentThrottle = metric.getThrottle();
        this.currentPosition = metric.getPosition();

        measureTopspeed();
        measureAngleAcceleration();

        System.out.println(String.format("Metrics - P: %s, Lane: %s, D: %s (%s)", currentPosition.getPieceNumber(), currentPosition.getLane().getDistanceFromCenter(), currentPosition.getInPieceDistance(), track.getPiece(currentPosition).getLength(currentPosition.getLane())));
        System.out.println(String.format("Metrics - Speed: %s, Slip: %s, S.Velocity %s", getCurrentSpeed(), getSlipAngle(), getSlipVelocity()));
        System.out.println(String.format("Metrics - Prev.Angle Acc.: %s, Curr.Angle.Acc %s, Max.Angle Acc. %s, Max.Slip: %s, Ticks: %s", previousAngleAcceleration, targetAngleAcceleration, maxAngleAcceleration, maxSlipAngle, ticksInCorner));
    }

    private void measureAngleAcceleration() {
        if(enteredAnglePiece()) {
            previousAngleAcceleration = getCurrentAngleAcceleration();
            ticksInCorner = 0;
        }

        if(getCurrentPiece().getAngle() != 0) {
            ticksInCorner++;
        }

        maxAngleAcceleration = Math.max(maxAngleAcceleration, getCurrentAngleAcceleration());
        maxSlipAngle = Math.max(maxSlipAngle, Math.abs(getSlipAngle()));

        if(exitedAnglePiece()) {
            if(ticksInCorner > 42) {
                /*
                if(maxSlipAngle > 58 && Math.abs(previousAngleAcceleration - targetAngleAcceleration) < 0.01) {
                  targetAngleAcceleration = previousAngleAcceleration;
                } else if(maxSlipAngle > 50 && Math.abs(previousAngleAcceleration - targetAngleAcceleration) < 0.025) {
                    targetAngleAcceleration = Math.max(previousAngleAcceleration, targetAngleAcceleration);
                } else if(maxSlipAngle <= 50 && Math.abs(previousAngleAcceleration - targetAngleAcceleration) < 0.1) {
                    targetAngleAcceleration += 0.01;
                }*/
                if(maxSlipAngle >= 55) {
                    targetAngleAcceleration -= 0.01;
                } else if(maxSlipAngle > 50 && maxSlipAngle < 55) {
                    targetAngleAcceleration = maxAngleAcceleration;
                } else if(maxSlipAngle <= 50) {
                    targetAngleAcceleration += 0.01;
                }
            }
            maxSlipAngle = 0;
            maxAngleAcceleration = 0;
        }
    }

    private double getCurrentAngleAcceleration() {
        if(getCurrentPiece().getAngle() != 0) {
            AnglePiece currentPiece = (AnglePiece) getCurrentPiece();
            double radius = currentPiece.getEffectiveRadius(currentPosition.getLane());

            return Math.pow(getCurrentSpeed(), 2) / radius;
        }

        return 0;
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
            if(currentSpeed > getTopspeed()) {
                breakingDistance += currentSpeed;
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
