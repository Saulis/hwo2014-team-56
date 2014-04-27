package noobbot.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saulis on 22/04/14.
 */
public class TurboCharger {
    private Navigator navigator;
    private List<Turbo> availableTurbos = new ArrayList<Turbo>();

    public TurboCharger(Navigator navigator) {
        this.navigator = navigator;

    }

    public void setTurboAvailable(Turbo turboAvailable) {
        this.availableTurbos.add(turboAvailable);
    }

    public boolean shouldSendTurbo() {
        return noTurboActive() && isTurboAvailable() && isEnoughStraightRoadAhead();
    }

    private boolean noTurboActive(){
        return !navigator.isTurboActive();
    }

    private boolean isEnoughStraightRoadAhead() {
        Piece nextCorner = navigator.getNextCorner();
        return navigator.getCurrentPiece().getAngle() == 0 && navigator.getDistanceToTarget(nextCorner) > 400.0;
    }

    private boolean isTurboAvailable() {
        return ! availableTurbos.isEmpty();
    }

    public Turbo useTurbo() {
        if (availableTurbos.isEmpty()) {
            return null;
        }
            
        Turbo turbo = availableTurbos.get(0);
        availableTurbos.remove(turbo);
        return turbo;
    }
}
