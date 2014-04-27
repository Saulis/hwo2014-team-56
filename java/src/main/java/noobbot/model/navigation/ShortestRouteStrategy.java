package noobbot.model.navigation;

import static java.util.Arrays.*;

import java.util.Comparator;
import java.util.List;

import noobbot.model.TrackRoute;

public class ShortestRouteStrategy extends BaseRouteStrategy {

    @Override
    public TrackRoute getRoute(List<TrackRoute> routes) {
        TrackRoute selectedRoute = stream(routes.toArray(new TrackRoute[routes.size()]))
                .filter(r -> r.isLoop())
                .sorted(new Comparator<TrackRoute>() {
            @Override
            public int compare(TrackRoute o1, TrackRoute o2) {
                return Double.compare(o1.getRouteLength(), o2.getRouteLength());
            }
        }).findFirst().get();

        printSelectedRoute("shortest", selectedRoute);
        
        return selectedRoute;
    }

}
