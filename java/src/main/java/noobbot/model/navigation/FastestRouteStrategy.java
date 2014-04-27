package noobbot.model.navigation;

import static java.util.Arrays.*;

import java.util.Comparator;
import java.util.List;

import noobbot.model.TrackRoute;

public class FastestRouteStrategy extends BaseRouteStrategy {

    @Override
    public TrackRoute getRoute(List<TrackRoute> routes) {
        TrackRoute selectedRoute = stream(routes.toArray(new TrackRoute[routes.size()])).sorted(new Comparator<TrackRoute>() {
            @Override
            public int compare(TrackRoute o1, TrackRoute o2) {
                return Double.compare(o1.getRouteDrivingTime(), o2.getRouteDrivingTime());
            }
        }).findFirst().get();

        printSelectedRoute("fastest", selectedRoute);

        return selectedRoute;
    }

}
