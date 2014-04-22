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
        TrackRouteSegment currentSegment = navigator.getCurrentSegment();

        //System.out.println(String.format("Turbo: %s, corners: %s", turboAvailable, currentSegment.hasCorners()));

        return turboAvailable && !currentSegment.hasCorners();
    }
}
