package noobbot.model;

/**
 * Created by Saulis on 18/04/14.
 */
public class ThrottleControl {
    private CarMetrics metrics;

    public ThrottleControl(CarMetrics metrics) {

        this.metrics = metrics;
    }

    public double getThrottle(double currentSpeed, double targetSpeed) {
        double diff = targetSpeed - currentSpeed;
        if(diff > 0.2) {
            printDebug(currentSpeed, targetSpeed, diff, "ACCELERATING");

            return 1.0;
        } else if(diff < -0.2){
            printDebug(currentSpeed, targetSpeed, diff, "BRAKING");

            return 0.0;
        }
        else {
            printDebug(currentSpeed, targetSpeed, diff, "STABILIZING");

            return targetSpeed / metrics.getTopspeed();
        }
    }

    private void printDebug(double currentSpeed, double targetSpeed, double diff, String status) {
        System.out.println(String.format("Throttle: %s %s->%s (%s)", status, currentSpeed, targetSpeed, diff));
    }
}
