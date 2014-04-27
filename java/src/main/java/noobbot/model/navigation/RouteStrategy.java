package noobbot.model.navigation;

import java.util.List;

import noobbot.model.TrackRoute;

public interface RouteStrategy {

    public abstract TrackRoute getRoute(List<TrackRoute> routes);

}
