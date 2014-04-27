package noobbot.model;

/**
 * Created by Saulis on 24/04/14.
 */
public class AntiLockBrakes {
    private CarMetrics metrics;

    public AntiLockBrakes(CarMetrics metrics) {

        this.metrics = metrics;
    }

    public boolean shouldBrake(TargetSpeed targetSpeed) {
        double slipAngle = Math.abs(metrics.getSlipAngle());
        double slipVelocity = metrics.getSlipVelocity();
        double timeUntilCrash = (60 - slipAngle) / slipVelocity;

        return targetSpeed.getDistanceToTarget() <= targetSpeed.getBrakingDistance() || (timeUntilCrash >= 0 && timeUntilCrash < 5) || Math.abs(slipVelocity) > 4;
    }
}
