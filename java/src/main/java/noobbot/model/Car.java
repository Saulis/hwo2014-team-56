package noobbot.model;

import noobbot.descriptor.GameInitDescriptor;

import java.util.List;

/**
 * Created by jereketonen on 4/16/14.
 */
public class Car {

    private Position position;
    private Position previousPosition;
    private double previousSpeed = 0;
    private double previousThrottle = 0;
    private Track track;

    double accelerationMagicNumber = 0.98; //This will be measured real time
    double topspeed = 10; //This will be calculated from acceleration magic number

    public Car(Track track) {
        this.track = track;
    }

    public double setPosition(Position newPosition) {
        previousSpeed = getSpeed();

        previousPosition = position;
        position = newPosition;

        double slipAngle = position.getSlipAngle();
        double trackAngle = getTrackAngle();
        double nextTrackAngle = getNextTrackAngle();
        double acceleration = getAcceleration();

        //Trying out setting target speed roughly according to angle.. 45 degrees -> 50% of top speed
        double targetSpeed = topspeed * ((90 - Math.abs(nextTrackAngle)) / 90) * 1.2; //magic magic + 20% boost

        double speed = getSpeed();
        double speedDiff = targetSpeed - speed;

        double nextThrottle = 0;
        if(speedDiff > 0.5) {
            nextThrottle = 1;
        } else if(speedDiff < -0.5) {
            nextThrottle = 0;
        }
        else {
            nextThrottle = targetSpeed / topspeed;
        }

        //Acceleration estimation testing here...
        double estimatedAcceleration = (previousThrottle * topspeed - speed) * (1 - accelerationMagicNumber);

        //If we can estimate deceleration rate we can then calculate the distance required to decelerate to target speed.
        //With the braking distance we can then start braking at the last possible moment.

        System.out.println(String.format("Piece: %s, Length: %s, Position: %s,  Angle: %s->%s, Throttle: %s->%s, Slip: %s, Speed: %s (%s), Acc: %s (%s)", getPosition().getPiecePosition().pieceIndex, getPieceLength(position), getPosition().getPiecePosition().inPieceDistance, trackAngle, nextTrackAngle, previousThrottle, nextThrottle, slipAngle, speed, targetSpeed, acceleration, estimatedAcceleration));

        return nextThrottle;
    }

    private double getNextTrackAngle() {
        int pieceIndex = getPosition().getPieceNumber();
        int nextPieceIndex = 0;
        if(pieceIndex + 1 < track.getPieces().size()) {
            nextPieceIndex = pieceIndex + 1;
        }

        return track.getPieces().get(nextPieceIndex).getAngle();
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
