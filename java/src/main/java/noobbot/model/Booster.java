package noobbot.model;

/**
 * Created by Saulis on 20/04/14.
 */
public class Booster {
    public double addBoost(double throttle, double slipAngle, double slipVelocity) {
        double slipDiff = 60 - Math.abs(slipAngle);
        double timeUntilCrash = Math.abs(slipDiff / slipVelocity);

        if(timeUntilCrash > 15 || slipVelocity < 0) {
            System.out.println(String.format("DRIFTING MODE: %s->1.0", throttle));
            return 1.0;
        }

        return throttle;
    }
}
