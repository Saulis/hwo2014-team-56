package noobbot.model;


public class TargetAngleSpeed {

    boolean inCalibration = true;
    boolean passedFirstTile = false;
    private double targetAngleSpeed = 3; // Considered safe, add failure handling

    public void calibrate(Position newPosition, Piece currentPiece, double slipChange, double slipAngle, double currentAngleSpeed, double currentThrottle) {
        if (newPosition.getPieceNumber() > 0) {
            passedFirstTile = true;
        }
        if (passedFirstTile && newPosition.getPieceNumber() == 0.0) {
            // Settng this limits calibration to first round.
            inCalibration = false;
        }

        if (Math.abs(currentAngleSpeed) > 0 && currentThrottle != 1) {
            if (inCalibration && currentPiece.getAngle() > 0) {
                if (Math.abs(slipChange) < 0.3 && slipAngle < 50) {
                    targetAngleSpeed  += 0.007;
                }
            }
        }
    }

    public double getValue() {
        return targetAngleSpeed;
    }
}
