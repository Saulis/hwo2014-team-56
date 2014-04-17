package noobbot.model;

/**
 * Created by Saulis on 17/04/14.
 */
public class Metric {
    private final Position position;
    private final double throttle;

    public Metric(Position position, double throttle) {

        this.position = position;
        this.throttle = throttle;
    }

    public Position getPosition() {
        return position;
    }

    public double getThrottle() {
        return throttle;
    }
}
