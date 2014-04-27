package noobbot.model.navigation;

import static java.util.Arrays.*;

import java.util.List;

import noobbot.model.TrackRoute;
import noobbot.model.TrackRouteSegment;

public class CustomFranceRouteStrategy extends BaseRouteStrategy {

    @Override
    public TrackRoute getRoute(List<TrackRoute> routes) {
        TrackRoute selectedRoute = stream(routes.toArray(new TrackRoute[routes.size()])).filter(r -> {
            TrackRouteSegment[] segments = r.getSegments();

            return segments[0].getDrivingLane().getIndex() == 0
                    && segments[1].getDrivingLane().getIndex() == 1
                    && segments[2].getDrivingLane().getIndex() == 0
                    && segments[3].getDrivingLane().getIndex() == 1
                    && segments[4].getDrivingLane().getIndex() == 1
                    && segments[5].getDrivingLane().getIndex() == 0
                    && segments[6].getDrivingLane().getIndex() == 0;
        }).findFirst().get();

        printSelectedRoute("custom", selectedRoute);
        return selectedRoute;
    }

}
