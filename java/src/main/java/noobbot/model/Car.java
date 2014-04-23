package noobbot.model;

import java.util.List;

public class Car {

    private final CarMetrics carMetrics;
    private final ThrottleControl throttleControl;
    private Position position;
    private double previousSlipAngle = 0;
    private double currentThrottle = 0;
    private Track track;
    private Navigator navigator;

    double accelerationMagicNumber = 0.02; //This will be measured real time
    double topspeed = 10; //This will be calculated from acceleration magic number
    double targetAngleSpeed = 3.75; //This will be calculated somehow

    public Car(Track track, Navigator navigator) {
        this.track = track;
        this.navigator = navigator;
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

        //double targetSpeed = getCurrentTargetSpeed();
        /*if(nextTrackAngle != 0) {
            //Targeting next angled piece
            targetSpeed = getNextPieceTargetSpeed();
        } /*else if(Math.abs(currentAngleSpeed) > targetAngleSpeed + 0.105) { //tailhappy magic number
            //Straight piece is next but making sure we're not slipping too much by hitting full throttle yet.
            targetSpeed = getCurrentTargetSpeed();
        }*/

        //double estimatedAcceleration = (currentThrottle * topspeed - speed) * accelerationMagicNumber;
        //double estimatedSpeed = speed + estimatedAcceleration;


        double currentTargetSpeed = getCurrentTargetSpeed();
        TargetSpeed targetSpeed = getDistanceToBrakingPoint(currentTargetSpeed, currentThrottle);
        double speedDiff = targetSpeed.getTargetSpeed() - speed;
        double nextThrottle = throttleControl.getThrottle(currentTargetSpeed, trackAngle, speed, targetSpeed);

        System.out.println(String.format("Piece: %s, Length: %s, Position: %s,  Angle: %s->%s, Throttle: %s->%s, Slip: %s (%s)", getPosition().getPiecePosition().pieceIndex, getPieceLength(position), getPosition().getPiecePosition().inPieceDistance, trackAngle, nextTrackAngle, currentThrottle, nextThrottle, slipAngle, slipAcceleration));
        System.out.println(String.format(" S: %s, A: %s, T: %s->%s  %s/%s)", speed, acceleration, currentThrottle, nextThrottle, speedDiff, targetSpeed));
        //System.out.println(String.format("*S: %s, A: %s", estimatedSpeed, estimatedAcceleration));
        System.out.println("ANGLE: " + currentAngleSpeed);

        System.out.println(String.format("BRAKING DISTANCE: %s ", targetSpeed.getBrakingDistance()));
        System.out.println("");

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
        Lane lane = navigator.getLane(piece);

        return piece.getLength(lane);
    }

    private double getCurrentTargetSpeed() {
        Piece currentPiece = getCurrentPiece();
        Lane currentLane = navigator.getCurrentLane();

        return currentPiece.getTargetSpeed(currentLane);
    }

    private TargetSpeed getDistanceToBrakingPoint(double currentTargetSpeed, double currentThrottle) {
        Piece currentPiece = getCurrentPiece();

        Piece nextPiece = track.getPieceAfter(currentPiece);

        TrackRoute selectedRoute = navigator.getSelectedRoute();

        TrackRouteSegment segment = selectedRoute.getSegmentForPiece(nextPiece.getNumber());
        double targetSpeed = nextPiece.getTargetSpeed(segment.getDrivingLane());

        while(targetSpeed == currentTargetSpeed || nextPiece.getAngle() == 0) {
            nextPiece = track.getPieceAfter(nextPiece);
            segment = selectedRoute.getSegmentForPiece(nextPiece.getNumber());

            targetSpeed = nextPiece.getTargetSpeed(segment.getDrivingLane());
        }

        //Piece nextTargetPiece = selectedRoute.getPieceForNextTargetSpeed(currentPiece);
        //Lane nextTargetLane = navigator.getLane(nextTargetPiece);

        double distanceToTarget = selectedRoute.getDistanceBetween(currentPiece, nextPiece) - position.getInPieceDistance();
        //double nextTargetSpeed = nextTargetPiece.getTargetSpeed(nextTargetLane);

        System.out.println(String.format("Next TargetSpeed: i: %s, D: %s, S: %s", nextPiece.getNumber(), distanceToTarget, targetSpeed));

        double brakingDistance = carMetrics.getBrakingDistance(carMetrics.getCurrentSpeed(), targetSpeed, currentThrottle);
 /*
        if(distanceToTarget <= brakingDistance && targetSpeed < currentTargetSpeed) {
            return targetSpeed;
        }



        return currentTargetSpeed; */

        return new TargetSpeed(targetSpeed, distanceToTarget, brakingDistance);
        //return distanceToTarget - brakingDistance;
    }

    private double getNextPieceTargetSpeed() {
        Piece nextPiece = getNextPiece();
        Lane lane = navigator.getLane(nextPiece);
        System.out.println("lane: " + lane.getDistanceFromCenter());
        return nextPiece.getTargetSpeed(lane); //TODO: this will fail if we are not on the driving lane
    }

}
