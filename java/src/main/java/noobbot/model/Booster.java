package noobbot.model;

/**
 * Created by Saulis on 22/04/14.
 */
public class Booster {
    public double addBoost(double throttle, double slipAngle, double slipVelocity) {
        double slipDiff = 60 - Math.abs(slipAngle);
        double timeUntilCrash = Math.abs(slipDiff / slipVelocity);

        if(weShouldBoost(slipVelocity))  { //if too much yliheittoa, restrict slipVelocity even more
            System.out.println(String.format("DRIFTING MODE: %s->1.0", throttle));
            return 1.0;
        }

        return throttle;
    }

    public boolean weShouldBoost(double slipVelocity) {
     return slipVelocity < 0 && slipVelocity > -3.5;
    }
}