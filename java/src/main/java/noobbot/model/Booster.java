package noobbot.model;

/**
 * Created by Saulis on 22/04/14.
 */
public class Booster {
    public double addBoost(double throttle, double slipAngle, double slipVelocity) {
        double slipDiff = 60 - Math.abs(slipAngle);
        double timeUntilCrash = Math.abs(slipDiff / slipVelocity);

        if(weShouldBoost(slipAngle, slipVelocity))  {
            System.out.println(String.format("DRIFTING MODE: %s->1.0", throttle));
            return 1.0;
        }

        return throttle;
    }

    public boolean weShouldBoost(double slipAngle, double slipVelocity) {
     return slipAngle == 0 || (slipVelocity < 0.1 && slipVelocity > -2.5); //if too much yliheittoa, restrict slipVelocity even more
    }
}