package noobbot.model;

/**
 * Created by Saulis on 18/04/14.
 */
public class ThrottleControl {
    private final AntiLockBrakes brakes;
    private CarMetrics metrics;
	private boolean isStabilizing = false;

    public ThrottleControl(CarMetrics metrics) {

        this.metrics = metrics;
        brakes = new AntiLockBrakes(metrics);
    }

    public double getThrottle(double currentTargetSpeed, TargetSpeed targetSpeed) {
    	isStabilizing = false;
        double diff = targetSpeed.getTargetSpeed() - metrics.getCurrentSpeed();
        if(brakes.shouldBrake(currentTargetSpeed, targetSpeed)) {
            printDebug(metrics.getCurrentSpeed(), targetSpeed.getTargetSpeed(), diff, "BRAKING");

            return 0.0;
        }
        else if(metrics.getCurrentPiece().getAngle() == 0 || metrics.getSlipAngle().allowsBoost()) { //actual measurement may be a little over the real top speed
            printDebug(metrics.getCurrentSpeed(), targetSpeed.getTargetSpeed(), diff, "ACCELERATING");

            return 1.0;
        } else {
            printDebug(metrics.getCurrentSpeed(), currentTargetSpeed, diff, "STABILIZING");
            isStabilizing = true;
            return Math.min(currentTargetSpeed / metrics.getTopspeed(), 1.0);
        }
    }

    private void printDebug(double currentSpeed, double targetSpeed, double diff, String status) {
        System.out.println(String.format("Throttle: %s %s->%s (%s)", status, currentSpeed, targetSpeed, diff));
    }

	public boolean isStabilizing() {
		return isStabilizing;
	}
}
