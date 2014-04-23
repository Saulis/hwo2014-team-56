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

    public double getThrottle(double currentSpeed, double targetSpeed) {
        double diff = targetSpeed - currentSpeed;
        if(diff > 0.2 || targetSpeed >= (metrics.getTopspeed() - 0.01)) { //actual measurement may be a little over the real top speed
            printDebug(currentSpeed, targetSpeed, diff, "ACCELERATING");

            return 1.0;
         } else if(diff > -1.25 && booster.weShouldBoost(metrics.getSlipVelocity())) { //drifting
            printDebug(currentSpeed, targetSpeed, diff, "DRIFTING");

            return 1.0;
         } else if(diff < -0.05){
            printDebug(currentSpeed, targetSpeed, diff, "BRAKING");

            return 0.0;
        }
        else {
            printDebug(currentSpeed, targetSpeed, diff, "STABILIZING");

            return booster.addBoost(targetSpeed / metrics.getTopspeed(), metrics.getSlipAngle(), metrics.getSlipVelocity());
        }
    }

    private void printDebug(double currentSpeed, double targetSpeed, double diff, String status) {
        System.out.println(String.format("Throttle: %s %s->%s (%s)", status, currentSpeed, targetSpeed, diff));
    }
}
