package noobbot.model;

/**
 * Created by Saulis on 20/04/14.
 */
public class Stabilizer {
    private CarMetrics metrics;

    public Stabilizer(CarMetrics metrics) {

        this.metrics = metrics;
    }

    public double stabilize(double throttle, double slipAngle, double slipVelocity) {
        if(isUnstable(slipAngle, slipVelocity)) {
            System.out.println(String.format("STABILIZING: %s->0.0", throttle));
            return 0.0;
        }

        return throttle;

    }

    public boolean isUnstable(double slipAngle, double slipVelocity) {
        double slipDiff = 60 - Math.abs(slipAngle);
        double timeUntilCrash = Math.abs(slipDiff / slipVelocity);

        return metrics.getSlipAcceleration() > 0.2  || (timeUntilCrash < 6 && slipVelocity > 0);
    }
}
