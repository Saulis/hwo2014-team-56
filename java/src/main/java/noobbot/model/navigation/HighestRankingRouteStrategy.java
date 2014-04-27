package noobbot.model.navigation;

import static java.util.Arrays.*;

import java.util.Comparator;
import java.util.List;

import noobbot.model.TrackRoute;

public class HighestRankingRouteStrategy extends BaseRouteStrategy {

    @Override
    public TrackRoute getRoute(List<TrackRoute> routes) {
        TrackRoute selectedRoute = stream(routes.toArray(new TrackRoute[routes.size()])).sorted(new Comparator<TrackRoute>() {
            @Override
            public int compare(TrackRoute o1, TrackRoute o2) {
                return Double.compare(o2.getRanking(), o1.getRanking());
            }
        }).findFirst().get();

        printSelectedRoute("highest ranked ", selectedRoute);
        System.out.println("Ranking: " + selectedRoute.getRanking());
        for(int i =0;i < selectedRoute.getSegments().length - 1;i++) {
            System.out.println(selectedRoute.rankSegments(selectedRoute.getSegments()[i], selectedRoute.getSegments()[i + 1]));
        }
        return selectedRoute;
    }

}
