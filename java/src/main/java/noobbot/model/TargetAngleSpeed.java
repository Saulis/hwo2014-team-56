package noobbot.model;


public class TargetAngleSpeed {

    boolean inCalibration = true;
    boolean passedFirstTile = false;
    private double targetAngleSpeed = 3; // Considered safe, add failure handling

    public TargetAngleSpeed(Track track) {
        // TODO Auto-generated constructor stub
    }

    public void calibrate(Position newPosition, Piece currentPiece, double slipAcceleration, double slipAngle, double currentAngleSpeed, double currentThrottle) {
        if (newPosition.getPieceNumber() > 0) {
            passedFirstTile = true;
        }
        if (passedFirstTile && newPosition.getPieceNumber() == 0.0) {
            //inCalibration = false;
        }

        if (Math.abs(currentAngleSpeed) > 0 && currentThrottle != 1) {
            if (inCalibration && currentPiece.getAngle() > 0) {
                if (Math.abs(slipAcceleration) < 2.0 && slipAngle < 45) {
                    targetAngleSpeed  += 0.0048;
                }
            }
        }
    }
}
