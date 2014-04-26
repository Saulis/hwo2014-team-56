package noobbot.model;

public class Car {

    private final CarMetrics carMetrics;
    private final ThrottleControl throttleControl;
    private Position position;
    private double currentThrottle = 0;
    private Navigator navigator;

    public Car(CarMetrics carMetrics, Navigator navigator) {
        this.navigator = navigator;
        this.carMetrics = carMetrics;
        throttleControl = new ThrottleControl(carMetrics);
    }

    public double setPosition(Position newPosition) {
        position = newPosition;
        
        carMetrics.update(new Metric(newPosition, currentThrottle));

        double currentTargetSpeed = getCurrentTargetSpeed();

        TargetSpeed targetSpeed = getTargetSpeed(currentThrottle);
        double nextThrottle = throttleControl.getThrottle(currentTargetSpeed, targetSpeed);

        currentThrottle = nextThrottle;

        return Math.min(nextThrottle, 1.0);
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
        Piece nextPiece = navigator.getNextCorner();

        TrackRouteSegment segment = selectedRoute.getSegmentForPiece(nextPiece.getNumber());
        double targetSpeed = nextPiece.getTargetSpeed(segment.getDrivingLane());
        double distanceToTarget = navigator.getDistanceToTarget(nextPiece);

        //System.out.println(String.format("Next TargetSpeed: i: %s, D: %s, S: %s", nextPiece.getNumber(), distanceToTarget, targetSpeed));

        double brakingDistance = carMetrics.getBrakingDistance(carMetrics.getCurrentSpeed(), targetSpeed, currentThrottle);

        return new TargetSpeed(targetSpeed, distanceToTarget, brakingDistance);
    }
}
