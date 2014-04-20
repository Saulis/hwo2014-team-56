package noobbot.model;

import java.util.ArrayList;
import java.util.List;

import noobbot.descriptor.CarPositionsDescriptor.Data.PiecePosition;

public class Car {

    private final CarMetrics carMetrics;
    private final ThrottleControl throttleControl;
    private Position position;
    private double previousSlipAngle = 0;
    private double currentThrottle = 0;
    private Track track;
    private Lane hardcodedLane;

    double accelerationMagicNumber = 0.02; //This will be measured real time
    double topspeed = 10; //This will be calculated from acceleration magic number
    double targetAngleSpeed = 3; //This will be calculated somehow
    TargetAngleSpeed tas;
    int localMaxSlipAngle = 0;
    
    public Car(Track track) {
        this.track = track;
        hardcodedLane = track.getLanes().get(0);
        carMetrics = new CarMetrics(track);
        throttleControl = new ThrottleControl(carMetrics);
        tas = new TargetAngleSpeed(track);
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
        
        tas.calibrate(newPosition, getCurrentPiece(), slipAcceleration, slipAngle, currentAngleSpeed, currentThrottle);
        
        double targetSpeed = 10;

        double nextPieceSpeed = getPieceSpeed(position.getPieceNumber());
        double nextToNextPieceSpeed = getPieceSpeed(position.getPieceNumber()+2);
        double thirdNextPieceSpeed = getPieceSpeed(position.getPieceNumber()+3);
        double fourthNextPieceSpeed = getPieceSpeed(position.getPieceNumber()+4);
        
        double speedDiff = targetSpeed - speed;
        double estimatedAcceleration = (currentThrottle * topspeed - speed) * accelerationMagicNumber;
        double estimatedSpeed = speed + estimatedAcceleration;

        System.out.println(String.format("1: %4.2f\t 2: %4.2f\\t 3: %4.2f\\t 4: %4.2f\\t", nextPieceSpeed, nextToNextPieceSpeed, thirdNextPieceSpeed, fourthNextPieceSpeed));
        
        if (mustSlowDownNow()) {
            targetSpeed = 0.0;
        }
        else if (nextPieceSpeed < targetSpeed){
            targetSpeed = nextPieceSpeed;
        } else if(Math.abs(currentAngleSpeed) > targetAngleSpeed + 0.105) { //tailhappy magic number
            //Straight piece is next but making sure we're not slipping too much by hitting full throttle yet.
            targetSpeed = getPieceLength(position) / (Math.abs(trackAngle)/ targetAngleSpeed);
        }

        double nextThrottle = throttleControl.getThrottle(speed, targetSpeed);

        System.out.println(String.format("Piece: %s, Length: %s, Position: %s,  Angle: %s->%s, Throttle: %s->%s, Slip: %s (%s)", getPosition().getPiecePosition().pieceIndex, getPieceLength(position), getPosition().getPiecePosition().inPieceDistance, trackAngle, nextTrackAngle, currentThrottle, nextThrottle, slipAngle, slipAcceleration));
        System.out.println(String.format(" S: %s, A: %s, T: %s->%s  %s/%s)", speed, acceleration, currentThrottle, nextThrottle, speedDiff, targetSpeed));
        System.out.println(String.format("*S: %s, A: %s", estimatedSpeed, estimatedAcceleration));
        System.out.println(String.format("ANGLE: %s (%s)", currentAngleSpeed, targetAngleSpeed));

//        System.out.println(String.format("BRAKING DISTANCE: %s ", carMetrics.getBrakingDistance(speed, targetSpeed, currentThrottle)));

        currentThrottle = nextThrottle;
        previousSlipAngle = slipAngle;

        return nextThrottle;
    }

    private boolean mustSlowDownNow() {
        for (int i = 1; i <= 3; i++) {
            if (mustSlowDownNowFor(i)) {
                System.out.println(String.format("Braking for piece %2.0f", getPiece(getCurrentPiece().getNumber() + i)));
                return true;
            }
        }
        return false;
    }

    private boolean mustSlowDownNowFor(int pieceFromCurrent) {
        Piece targetPiece = getPiece(position.getPieceNumber() + pieceFromCurrent);
        Position startOfTarget = new TrackPosition(targetPiece, 0, hardcodedLane);
        
        double distance = track.getDistanceBetween(position, startOfTarget);
        
        double currentSpeed = carMetrics.getCurrentSpeed();
        double pieceTargetSpeed = getPieceSpeed(targetPiece.getNumber());
        double brakingDistance = carMetrics.getBrakingDistance(currentSpeed, pieceTargetSpeed, currentThrottle);        

        if (distance - brakingDistance < 0) {
            System.out.println(String.format("Distance: %4.0f, brakingDistance: %4.0fs", distance, brakingDistance));
            return true;
        }
        
        return false;
    }

    private double getPieceSpeed(int pieceNumber) {
        Piece piece = getPiece(pieceNumber);
        double pieceAngle = Math.abs(piece.getAngle());
        
        if (pieceAngle == 0) {
            return 10;
        }
        
        double nextToNextPieceSpeed = piece.getLength(hardcodedLane) / (pieceAngle/ targetAngleSpeed);
        return nextToNextPieceSpeed;
    }

    private Piece getPiece(int index) {
        return track.getPieces().get(index % track.getPieces().size());
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

        return piece.getLength(hardcodedLane);
    }

    private double getNextPieceLength() {
        return getNextPiece().getLength(hardcodedLane);
    }
}
