package noobbot.model;

/**
 * Created by Saulis on 18/04/14.
 */
public class ThrottleControl {
    private final Booster booster;
    private CarMetrics metrics;

    public ThrottleControl(CarMetrics metrics) {

        this.metrics = metrics;
        booster = new Booster();
    }

    public double getThrottle(double currentTargetSpeed, TargetSpeed targetSpeed) {
        double diff = targetSpeed.getTargetSpeed() - metrics.getCurrentSpeed();
        if(targetSpeed.getDistanceToTarget() <= targetSpeed.getBrakingDistance()) {
            printDebug(metrics.getCurrentSpeed(), targetSpeed.getTargetSpeed(), diff, "BRAKING");

            return 0.0;
        }
        else if(metrics.getCurrentPiece().getAngle() == 0 || metrics.getSlipAngle().allowsBoost()) { //actual measurement may be a little over the real top speed
            printDebug(metrics.getCurrentSpeed(), targetSpeed.getTargetSpeed(), diff, "ACCELERATING");

            return 1.0;
        } else {
            printDebug(metrics.getCurrentSpeed(), currentTargetSpeed, diff, "STABILIZING");

            return booster.addBoost((currentTargetSpeed) / metrics.getTopspeed(), metrics.getSlipAngle());
        }
    }

    private void printDebug(double currentSpeed, double targetSpeed, double diff, String status) {
        System.out.println(String.format("Throttle: %s %s->%s (%s)", status, currentSpeed, targetSpeed, diff));
    }
}
