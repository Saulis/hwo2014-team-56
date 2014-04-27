package noobbot.model.navigation;

import static java.util.Arrays.*;

import java.util.List;

import noobbot.model.TrackRoute;
import noobbot.model.TrackRouteSegment;

public class CustomKeimolaRoute extends BaseRouteStrategy {

    @Override
    public TrackRoute getRoute(List<TrackRoute> routes) {
        TrackRoute selectedRoute = stream(routes.toArray(new TrackRoute[routes.size()])).filter(r -> {
            TrackRouteSegment[] segments = r.getSegments();

            return segments[0].getDrivingLane().getIndex() == 0
                    && segments[1].getDrivingLane().getIndex() == 1
                    && segments[2].getDrivingLane().getIndex() == 0
                    && segments[3].getDrivingLane().getIndex() == 0
                    && segments[4].getDrivingLane().getIndex() == 1
                    && segments[5].getDrivingLane().getIndex() == 1
                    && segments[6].getDrivingLane().getIndex() == 0
                    && segments[7].getDrivingLane().getIndex() == 0;
        }).findFirst().get();

        printSelectedRoute("custom", selectedRoute);
        System.out.println("Ranking: " + selectedRoute.getRanking());
        for(int i =0;i < selectedRoute.getSegments().length - 1;i++) {
            System.out.println(selectedRoute.rankSegments(selectedRoute.getSegments()[i], selectedRoute.getSegments()[i + 1]));
        }        
        return selectedRoute;
    }

}
