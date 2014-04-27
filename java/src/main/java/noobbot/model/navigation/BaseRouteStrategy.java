package noobbot.model.navigation;

import noobbot.model.TrackRoute;
import noobbot.model.TrackRouteSegment;

public abstract class BaseRouteStrategy implements RouteStrategy {

    protected void printSelectedRoute(String description, TrackRoute selectedRoute) {
        System.out.println(String.format("Navigator: using %s route: %s", description, selectedRoute.getRouteLength()));
        for(int i=0;i < selectedRoute.getSegments().length;i++) {
            TrackRouteSegment trackRouteSegment = selectedRoute.getSegments()[i];
            System.out.println(i + ": " + trackRouteSegment.getDrivingLane().getDistanceFromCenter());
        }
    }
}
