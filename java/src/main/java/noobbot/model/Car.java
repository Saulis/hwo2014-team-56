package noobbot.model;

import java.util.List;

/**
 * Created by jereketonen on 4/16/14.
 */
public class Car {

    private Position position;
    private Position previousPosition;
    private double previousSpeed = 0;
    private double previousSlipAngle = 0;
    private double currentThrottle = 0;
    private Track track;

    double accelerationMagicNumber = 0.02; //This will be measured real time
    double topspeed = 10; //This will be calculated from acceleration magic number

    public Car(Track track) {
        this.track = track;
    }

    public double setPosition(Position newPosition) {
        previousSpeed = getSpeed();
        previousPosition = position;
        position = newPosition;

        double slipAngle = getSlipAngle();
        double slipAcceleration = previousSlipAngle - slipAngle;
        double trackAngle = getTrackAngle();
        double nextTrackAngle = getNextTrackAngle();
        double acceleration = getAcceleration();


        double speed = getSpeed();

        //double angleAcceleration = (getPieceLength(position) * Math.abs(trackAngle) / 360) / 1.5;
        //System.out.println("ANGLE: " + angleAcceleration);

        //Trying out setting target speed roughly according to angle.. 45 degrees -> 50% of top speed
        double targetSpeed = 10;
        //if(nextTrackAngle != 0) {
            //targetSpeed = (getNextPieceLength() * Math.abs(nextTrackAngle) / 360) / 1.6;
            targetSpeed = topspeed * ((90 - Math.abs(nextTrackAngle)) / 90) * 1.20; //magic magic + 20% boost
        //}
        double speedDiff = targetSpeed - speed;

        //Acceleration estimation testing here...
        //double estimatedAcceleration = (currentThrottle * topspeed - speed) * (1 - accelerationMagicNumber);
        double estimatedAcceleration = (currentThrottle * topspeed - speed) * accelerationMagicNumber;
        double estimatedAcceleration2 = estimatedAcceleration;
        double estimatedSpeed = speed + estimatedAcceleration;
        double brakingDistance = getBrakingDistance(estimatedAcceleration, speed, targetSpeed);
        //breaking ticks
        int ticks = 1;
        if(speedDiff < 0) {

            while(estimatedSpeed > targetSpeed) {
                ticks++;
                brakingDistance += estimatedSpeed;
                estimatedAcceleration2 = 0-estimatedSpeed * accelerationMagicNumber;
                estimatedSpeed = estimatedSpeed + estimatedAcceleration2;
            }
        }

        double nextThrottle = 0;

        if(speedDiff > 1) {
            nextThrottle = 1;
        } else if(speedDiff < -1){// && getDistanceToBrakingPoint() <= brakingDistance) {
            nextThrottle = 0;
        }
        else {
            nextThrottle = targetSpeed / topspeed;
        }

        //If we can estimate deceleration rate we can then calculate the distance required to decelerate to target speed.
        //With the braking distance we can then start braking at the last possible moment.
        //System.out.println(String.format("Piece: %s, Length: %s, Position: %s,  Angle: %s->%s, Throttle: %s->%s, Slip: %s, Speed: %s (%s), Acc: %s (%s)", getPosition().getPiecePosition().pieceIndex, getPieceLength(position), getPosition().getPiecePosition().inPieceDistance, trackAngle, nextTrackAngle, currentThrottle, nextThrottle, slipAngle, speed, targetSpeed, acceleration, estimatedAcceleration));
        System.out.println(String.format("Piece: %s, Length: %s, Position: %s,  Angle: %s->%s, Throttle: %s->%s, Slip: %s (%s)", getPosition().getPiecePosition().pieceIndex, getPieceLength(position), getPosition().getPiecePosition().inPieceDistance, trackAngle, nextTrackAngle, currentThrottle, nextThrottle, slipAngle, slipAcceleration));
        System.out.println(String.format(" S: %s, A: %s, T: %s->%s  %s/%s (%s, %s)", speed, acceleration, currentThrottle, nextThrottle, speedDiff, targetSpeed, ticks, brakingDistance));
        System.out.println(String.format("*S: %s, A: %s", estimatedSpeed, estimatedAcceleration));

        currentThrottle = nextThrottle;
        previousSlipAngle = slipAngle;

        return nextThrottle;
    }

    private double getSlipAngle() {
        return position.getSlipAngle();
    }

    private double getDistanceToBrakingPoint() {
        return getPieceLength(position) - position.getInPieceDistance(); // magic number to brake later
    }

    private double getBrakingDistance(double estimatedAcceleration, double currentSpeed, double targetSpeed) {
        double estimatedSpeed = currentSpeed + estimatedAcceleration;
        double breakingDistance = currentSpeed;

        if(targetSpeed - currentSpeed < 0) {
            while(estimatedSpeed > targetSpeed) {
                breakingDistance += estimatedSpeed;
                estimatedAcceleration = 0-estimatedSpeed * accelerationMagicNumber;
                estimatedSpeed = estimatedSpeed + estimatedAcceleration;
                System.out.println(String.format("target: %s, est: %s", targetSpeed, estimatedSpeed));
            }
        }

        return breakingDistance;
    }

    private Piece getNextPiece() {
        int pieceIndex = getPosition().getPieceNumber();
        int nextPieceIndex = 0;
        if(pieceIndex + 1 < track.getPieces().size()) {
            nextPieceIndex = pieceIndex + 1;
        }

        return track.getPieces().get(nextPieceIndex);

    }

    private double getNextTrackAngle() {
        return getNextPiece().getAngle();
    }

    private double getTrackAngle() {
        List<Piece> pieces = track.getPieces();
        Piece piece = pieces.get(getPosition().getPieceNumber());

        return piece.getAngle();
    }

    public Position getPosition() {
        return position;
    }

    public double getSpeed() {
        if(previousPosition == null) {
            return 0;
        }

        if(previousPosition.getPieceNumber() == position.getPieceNumber()) {
            return position.getInPieceDistance() - previousPosition.getInPieceDistance();
        } else {
            double length = getPieceLength(previousPosition);
            return position.getInPieceDistance() + (length - previousPosition.getInPieceDistance());
        }
    }

    public double getAcceleration() {
        return getSpeed() - previousSpeed;
    }

    //TODO: this method would probably go to Track
    private double getPieceLength(Position piecePosition) {
        Piece piece = track.getPieces().get(piecePosition.getPieceNumber());

        if(piece.getAngle() != 0) {
            return Math.abs(piece.getAngle()) / 360 * 2 * Math.PI * getEffectiveRadius(track.getLanes().get(0), piece); // Hardcoded lane value.
        } else {
            return piece.getLength(0); //Hardcoded lane value.
        }
    }

    private double getNextPieceLength() {
        Piece piece = getNextPiece();

        if(piece.getAngle() != 0) {
            return Math.abs(piece.getAngle()) / 360 * 2 * Math.PI * getEffectiveRadius(track.getLanes().get(0), piece); // Hardcoded lane value.
        } else {
            return piece.getLength(0); //Hardcoded lane value.
        }
    }

    //TODO: this method would probably go to Track
    private double getEffectiveRadius(Lane lane, Piece piece) {
        if(isLeftTurn(piece)) {
            return piece.getRadius() +lane.getDistanceFromCenter();
        }

        return piece.getRadius() - lane.getDistanceFromCenter();
    }

    //TODO: this method would go to AnglePiece
    private boolean isLeftTurn(Piece piece) {
        return piece.getAngle() < 0;
    }

}
