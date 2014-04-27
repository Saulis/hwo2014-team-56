package noobbot.model;

/**
 * Created by Saulis on 22/04/14.
 */
public class Booster {
    public double addBoost(double throttle, SlipAngle slipAngle) {
        double slipDiff = 60 - Math.abs(slipAngle.getValue());
        double timeUntilCrash = Math.abs(slipDiff / slipAngle.getSlipChangeVelocity());

        if(slipAngle.allowsBoost())  {
            System.out.println(String.format("DRIFTING MODE: %s->1.0", throttle));
            return 1.0;
        }

        return throttle;
    }
}