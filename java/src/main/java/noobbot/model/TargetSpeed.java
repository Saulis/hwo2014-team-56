package noobbot.model;

/**
 * Created by Saulis on 23/04/14.
 */
public class TargetSpeed {
    private final double targetSpeed;
    private final double distanceToTarget;
    private final double brakingDistance;

    public TargetSpeed(double targetSpeed, double distanceToTarget, double brakingDistance) {

        this.targetSpeed = targetSpeed;
        this.distanceToTarget = distanceToTarget;
        this.brakingDistance = brakingDistance;
    }

    public double getTargetSpeed() {
        return targetSpeed;
    }

    public double getBrakingDistance() {
        return brakingDistance;
    }

    public double getDistanceToTarget() {
        return distanceToTarget;
    }

}
