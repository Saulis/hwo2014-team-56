package noobbot.model;

/**
 * Created by Saulis on 22/04/14.
 */
public class TurboCharger {
    private Navigator navigator;
    private int availableTurbos = 0;

    public TurboCharger(Navigator navigator) {
        this.navigator = navigator;

    }

    public void addTurbo() {
        availableTurbos++;
    }

    public void useTurbo() {
        availableTurbos--;
    }

    public boolean shouldSendTurbo() {
        Piece nextCorner = navigator.getNextCorner();

        return availableTurbos > 0 && navigator.getCurrentPiece().getAngle() == 0 && navigator.getDistanceToTarget(nextCorner) > 400.0;
    }
}
