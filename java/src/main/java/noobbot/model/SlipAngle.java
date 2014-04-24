package noobbot.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlipAngle {
    private Track track;

    // Configuration
    // Balance acceleration fluctuations with coefficients for latest change, the one before, etc.
    private List<Integer> accelerationWeights = Arrays.asList(60, 40);
    private int historySize = 60;
    
    // Status
    private boolean isLeftAngle;
    private double angleRadius;
    private List<Double> angleHistory = new ArrayList<Double>();

    private List<Double> values = new ArrayList<Double>();
    
    public SlipAngle(Track track) {
        this.track = track;
    }

    public void update(Position newPosition) {
        updateHistory(newPosition.getSlipAngle());
        if (angleChangedSide(newPosition) || angleChangedRadius(newPosition)) {
            changeAngle(newPosition);
        }
    }

    public double getAcceleration() {
        if (angleHistory.size() < accelerationWeights.size() + 2) {
            return 0;
        }
        
        double acceleration = 0;
        for (int i = 0; i < accelerationWeights.size(); i++) {
            acceleration += accelerationWeights.get(i) * getAcceleration(i);
        }
        acceleration /= accelerationWeights.stream().reduce(0, (a, b) -> a + b);
        
        return acceleration;
    }

    public double getChange() {
        if (angleHistory.isEmpty()) {
            return 0;
        }
        else if (angleHistory.size() == 1) {
            return angleHistory.get(0);
        }
        
        return angleHistory.get(angleHistory.size()-1) - angleHistory.get(angleHistory.size()-2);
    }

    public double getValue() {
        if (values.isEmpty()) {
            return 0;
        }
        return values.get(values.size()-1);
    }
    
    public void setAccelerationWeights(List<Integer> accelerationWeights) {
        this.accelerationWeights = accelerationWeights;
    }
    
    /**
     * Gets previous acceleration - 0 == latest, 1 == previous, etc.
     */
    private double getAcceleration(int i) {
        double laterChange = getChange(i);
        double earlierChange = getChange(i+1);
        
        return laterChange - earlierChange;
    }

    /**
     * Gets previous change in slip angle - 0 == latest, 1 == previous, etc.
     */
    private double getChange(int i) {
        double laterValue = getValue(i);
        double earlierValue = getValue(i+1);
        return laterValue - earlierValue;
    }


    /**
     * Gets previous slip angle value - 0 == latest, 1 == previous, etc.
     */
    private Double getValue(int i) {
        return angleHistory.get(angleHistory.size() - (i + 1));
    }

    private void updateHistory(double slipAngle) {
        angleHistory.add(slipAngle);
        if (angleHistory.size() > historySize) {
            angleHistory.remove(0);
        }
    }

    private boolean angleChangedRadius(Position newPosition) {
        return angleRadius != getAngleRadius(newPosition);
    }

    private double getAngleRadius(Position newPosition) {
        return track.getPiece(newPosition).getRadius();
    }

    private boolean angleChangedSide(Position newPosition) {
        boolean newAngleIsLeft = isAngleLeft(newPosition);
        return isLeftAngle != newAngleIsLeft;
    }

    private boolean isAngleLeft(Position newPosition) {
        return track.getPiece(newPosition).getAngle() < 0;
    }

    private void changeAngle(Position newPosition) {
        isLeftAngle = isAngleLeft(newPosition);
        angleRadius = getAngleRadius(newPosition);
        angleHistory.clear();
        values.clear();
    }
}
