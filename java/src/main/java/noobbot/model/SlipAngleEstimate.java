package noobbot.model;

import java.util.List;

public class SlipAngleEstimate {
    private List<Double> accelerations;
    private double currentAngle;

    public SlipAngleEstimate(double currentAngle, List<Double> accelerations) {
        this.currentAngle = currentAngle;
        this.accelerations = accelerations;
    }
}
