package noobbot.model;


public class TargetAngleSpeed {

    boolean inCalibration = true;
    boolean passedFirstTile = false;
    private double targetAngleSpeed = 2.8; // Considered safe, add failure handling

    public void calibrate(Position newPosition, Piece currentPiece, SlipAngle slipAngle, double currentSpeed, double currentThrottle) {
        if (newPosition.getPieceNumber() > 0) {
            passedFirstTile = true;
        }
        if (passedFirstTile && newPosition.getPieceNumber() == 0.0) {
            // Setting this limits calibration to first round.
            //inCalibration = false;
        }

        double currentAngleSpeed =  currentPiece.getAngle() / (currentPiece.getLength(newPosition.getLane()) / currentSpeed);
        System.out.println(String.format("P: %2s\tAngle: %4s\tA: %8.6f\tC: %8.6f\tSA: %5.2f\tAS: %5.2f\tTAS: %5.2f\t", currentPiece.getNumber(), currentPiece.getAngle(), slipAngle.getAcceleration(), slipAngle.getChange(), slipAngle.getValue(), currentAngleSpeed, targetAngleSpeed));
        if (Math.abs(currentAngleSpeed) > 0 && currentThrottle != 1) {
            if (inCalibration && currentPiece.getAngle() != 0) {
                if (Math.abs(slipAngle.getValue()) > 40) {
                    inCalibration = false;
                    System.out.println("******************* Calibration finished, angle limit exceeded! *********************");
                    return;
                }
                if (Math.abs(slipAngle.getAcceleration()) < 0.03 && Math.abs(slipAngle.getChange()) < 0.1 && Math.abs(slipAngle.getValue()) < 25) {
                    targetAngleSpeed  += 0.005;
//                    System.out.println(String.format("P: %2s\tA: %6.4f\tC: %6.4f\tSA: %5.2f\tAS: %5.2f\tTAS: %5.2f\t", currentPiece.getNumber(), slipAngle.getAcceleration(), slipAngle.getChange(), slipAngle.getValue(), currentAngleSpeed, targetAngleSpeed));
                }
            }
        }
    }

    public double getValue() {
        return targetAngleSpeed;
    }
}
