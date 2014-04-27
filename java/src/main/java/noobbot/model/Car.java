package noobbot.model;

public class Car {

    private final CarMetrics carMetrics;
    private final ThrottleControl throttleControl;
    private Position position;
    private double currentThrottle = 0;
    private Navigator navigator;

    public Car(CarMetrics carMetrics, Navigator navigator, ThrottleControl throttleControl) {
        this.navigator = navigator;
        this.carMetrics = carMetrics;
        this.throttleControl = throttleControl;
    }

    public double setPosition(Position newPosition) {
        position = newPosition;
        
        carMetrics.update(new Metric(newPosition, currentThrottle));

        double currentTargetSpeed = getCurrentTargetSpeed();

        TargetSpeed targetSpeed = getTargetSpeed(currentThrottle);
        double nextThrottle = throttleControl.getThrottle(currentTargetSpeed, targetSpeed);

        currentThrottle = nextThrottle;

        return nextThrottle;
    }

    public Position getPosition() {
        return position;
    }

    private double getCurrentTargetSpeed() {
        Piece currentPiece = navigator.getCurrentPiece();
        Lane currentLane = navigator.getCurrentLane();

        return currentPiece.getTargetSpeed(currentLane);
    }

    private TargetSpeed getTargetSpeed(double currentThrottle) {
        TrackRoute selectedRoute = navigator.getSelectedRoute();
        Piece nextTargetPiece = navigator.getNextTargetPiece();

        TrackRouteSegment segment = selectedRoute.getSegmentForPiece(nextTargetPiece.getNumber());
        double targetSpeed = nextTargetPiece.getTargetSpeed(segment.getDrivingLane());
        double distanceToTarget = navigator.getDistanceToTarget(nextTargetPiece);

        //System.out.println(String.format("Next TargetSpeed: i: %s, D: %s, S: %s", nextTargetPiece.getNumber(), distanceToTarget, targetSpeed));

        double brakingDistance = carMetrics.getBrakingDistance(carMetrics.getCurrentSpeed(), targetSpeed, currentThrottle);


        return new TargetSpeed(targetSpeed, distanceToTarget, brakingDistance);
    }
}
