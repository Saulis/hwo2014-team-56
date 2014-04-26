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
        Piece nextCorner = navigator.getNextCorner();

        return ! availableTurbos.isEmpty() && navigator.getCurrentPiece().getAngle() == 0 && navigator.getDistanceToTarget(nextCorner) > 300.0;
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
