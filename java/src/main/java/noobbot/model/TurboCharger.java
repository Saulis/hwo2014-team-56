package noobbot.model;

/**
 * Created by Saulis on 22/04/14.
 */
public class TurboCharger {
    private Navigator navigator;
    private boolean turboAvailable;

    public TurboCharger(Navigator navigator) {
        this.navigator = navigator;

    }

    public void setTurboAvailable(boolean turboAvailable) {
        this.turboAvailable = turboAvailable;
    }

    public boolean shouldSendTurbo() {
        Piece nextCorner = navigator.getNextCorner();

        return turboAvailable && navigator.getCurrentPiece().getAngle() == 0 && navigator.getDistanceToTarget(nextCorner) > 300.0;
    }
}
