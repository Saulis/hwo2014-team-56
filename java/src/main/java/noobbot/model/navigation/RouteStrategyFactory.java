package noobbot.model.navigation;

public class RouteStrategyFactory {
    public static RouteStrategy getCustomFranceRoute() {
        return new CustomFranceRouteStrategy();
    }
    public static RouteStrategy getCustomUsaRoute() {
        return new CustomUsaRouteStrategy();
    }
    public static RouteStrategy getCustomKeimolaRoute() {
        return new CustomKeimolaRouteStrategy();
    }
    public static RouteStrategy getFastestRoute() {
        return new FastestRouteStrategy();
    }
    public static RouteStrategy getShortestRoute() {
        return new ShortestRouteStrategy();
    }
    public static RouteStrategy getHighestRankingRoute() {
        return new HighestRankingRouteStrategy();
    }
}
