package noobbot.model;

/**
 * Created by Saulis on 24/04/14.
 */
public class AntiLockBrakes {
    private CarMetrics metrics;

    public AntiLockBrakes(CarMetrics metrics) {

        this.metrics = metrics;
    }

    public boolean shouldBrake(double currentTargetSpeed, TargetSpeed targetSpeed) {
        double slipAngle = Math.abs(metrics.getSlipAngle().getValue());
        double slipVelocity = metrics.getSlipAngle().getSlipChangeVelocity();
        double timeUntilCrash = (60 - slipAngle) / slipVelocity;

        return (slipVelocity >= 0 && metrics.getCurrentSpeed() - currentTargetSpeed > 0.1) || targetSpeed.getDistanceToTarget() <= targetSpeed.getBrakingDistance() || (timeUntilCrash >= 0 && timeUntilCrash < 5) || Math.abs(slipVelocity) > 2.6;
    }
}
