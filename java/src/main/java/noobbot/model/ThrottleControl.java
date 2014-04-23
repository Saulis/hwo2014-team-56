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

    public double getThrottle(double currentTargetSpeed, double trackAngle, double currentSpeed, TargetSpeed targetSpeed) {
        double diff = targetSpeed.getTargetSpeed() - currentSpeed;
        if(targetSpeed.getDistanceToTarget() <= targetSpeed.getBrakingDistance()) {
            printDebug(currentSpeed, targetSpeed.getTargetSpeed(), diff, "BRAKING");

            return 0.0;
        }
        else if(trackAngle == 0 || booster.weShouldBoost(metrics.getSlipAngle(), metrics.getSlipVelocity())) { //actual measurement may be a little over the real top speed
            printDebug(currentSpeed, targetSpeed.getTargetSpeed(), diff, "ACCELERATING");

            return 1.0;
         /*} else if(diff > -1.25 && booster.weShouldBoost(metrics.getSlipVelocity())) { //drifting
            printDebug(currentSpeed, targetSpeed, diff, "DRIFTING");

            return 1.0;*/
        } else {
            printDebug(currentSpeed, currentTargetSpeed, diff, "STABILIZING");

            return booster.addBoost(currentTargetSpeed / metrics.getTopspeed(), metrics.getSlipAngle(), metrics.getSlipVelocity());
        }
    }

    private void printDebug(double currentSpeed, double targetSpeed, double diff, String status) {
        System.out.println(String.format("Throttle: %s %s->%s (%s)", status, currentSpeed, targetSpeed, diff));
    }
}
