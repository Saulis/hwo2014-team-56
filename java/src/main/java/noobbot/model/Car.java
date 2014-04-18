package noobbot.model;

import java.util.List;

public class Car {

    private final CarMetrics carMetrics;
    private final ThrottleControl throttleControl;
    private Position position;
    private double previousSlipAngle = 0;
    private double currentThrottle = 0;
    private Track track;

    double accelerationMagicNumber = 0.02; //This will be measured real time
    double topspeed = 10; //This will be calculated from acceleration magic number
    double targetAngleSpeed = 3.75; //This will be calculated somehow

    public Car(Track track) {
        this.track = track;
        carMetrics = new CarMetrics(track);
        throttleControl = new ThrottleControl(carMetrics);
    }

    //Under refucktoring...
    public double setPosition(Position newPosition) {
        position = newPosition;

        carMetrics.update(new Metric(newPosition, currentThrottle));

        double slipAngle = getSlipAngle();
        double slipAcceleration = previousSlipAngle - slipAngle;
        double trackAngle = getTrackAngle();
        double nextTrackAngle = getNextTrackAngle();
        double speed = carMetrics.getCurrentSpeed();
        double acceleration = carMetrics.getCurrentAcceleration();
        double currentAngleSpeed = getCurrentAngleSpeed(speed);

        double targetSpeed = 10;
        if(nextTrackAngle != 0) {
            //Targeting next angled piece
            targetSpeed = getNextPieceLength() / (Math.abs(nextTrackAngle)/ targetAngleSpeed);
        } else if(Math.abs(currentAngleSpeed) > targetAngleSpeed + 0.105) { //tailhappy magic number
            //Straight piece is next but making sure we're not slipping too much by hitting full throttle yet.
            targetSpeed = getPieceLength(position) / (Math.abs(trackAngle)/ targetAngleSpeed);
        }

        double speedDiff = targetSpeed - speed;
        double estimatedAcceleration = (currentThrottle * topspeed - speed) * accelerationMagicNumber;
        double estimatedSpeed = speed + estimatedAcceleration;
        double brakingDistance = getBrakingDistance(estimatedAcceleration, speed, targetSpeed);

        double nextThrottle = throttleControl.getThrottle(speed, targetSpeed);

        System.out.println(String.format("Piece: %s, Length: %s, Position: %s,  Angle: %s->%s, Throttle: %s->%s, Slip: %s (%s)", getPosition().getPiecePosition().pieceIndex, getPieceLength(position), getPosition().getPiecePosition().inPieceDistance, trackAngle, nextTrackAngle, currentThrottle, nextThrottle, slipAngle, slipAcceleration));
        System.out.println(String.format(" S: %s, A: %s, T: %s->%s  %s/%s, B: %s)", speed, acceleration, currentThrottle, nextThrottle, speedDiff, targetSpeed, brakingDistance));
        System.out.println(String.format("*S: %s, A: %s", estimatedSpeed, estimatedAcceleration));
        System.out.println("ANGLE: " + currentAngleSpeed);

        currentThrottle = nextThrottle;
        previousSlipAngle = slipAngle;

        return nextThrottle;
    }

    private double getCurrentAngleSpeed(double speed) {
        if(speed <= 0) {
            return 0;
        }

        //TODO: Can't use piece.getLength(lane) yet because it doesn't take lane into account...
        return getCurrentPiece().getAngle() / (getPieceLength(position) / speed);
    }

    private double getSlipAngle() {
        return position.getSlipAngle();
    }

    private double getBrakingDistance(double estimatedAcceleration, double currentSpeed, double targetSpeed) {
        double estimatedSpeed = currentSpeed + estimatedAcceleration;
        double breakingDistance = currentSpeed;

        if(targetSpeed - currentSpeed < 0) {
            while(estimatedSpeed > targetSpeed) {
                breakingDistance += estimatedSpeed;
                estimatedAcceleration = 0-estimatedSpeed * accelerationMagicNumber;
                estimatedSpeed = estimatedSpeed + estimatedAcceleration;
                //System.out.println(String.format("target: %s, est: %s", targetSpeed, estimatedSpeed));
            }
        }

        return breakingDistance;
    }

    private Piece getCurrentPiece() {
        return track.getPieces().get(getPosition().getPieceNumber());
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

    //TODO: these methods would probably go to Track
    private double getPieceLength(Position piecePosition) {
        Piece piece = track.getPieces().get(piecePosition.getPieceNumber());

        return getPieceLength(piece);
    }

    private double getNextPieceLength() {
        return getPieceLength(getNextPiece());
    }

    private double getPieceLength(Piece piece) {
        if(piece.getAngle() != 0) {
            return Math.abs(piece.getAngle()) / 360 * 2 * Math.PI * getEffectiveRadius(track.getLanes().get(0), piece); //TODO: Hardcoded lane value.
        } else {
            return piece.getLength(track.getLanes().get(0));
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
