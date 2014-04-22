package noobbot.model;

import noobbot.LeftSwitchLane;
import noobbot.RightSwitchLane;
import noobbot.SwitchLane;

import java.util.*;

import static java.util.Arrays.stream;

/**
 * Created by Saulis on 21/04/14.
 */
public class Navigator {
    private Track track;
    private List<TrackRoute> routes;
    private TrackRoute selectedRoute;
    private PlayerPosition currentPosition;
    private TrackRouteSegment currentSegment;

    public Navigator(Track track) {

        this.track = track;

        List<TrackSegment> trackSegments = createTrackSegments(track);
        routes = new ArrayList<TrackRoute>();

        for(Lane l : track.getLanes()) {
            routes.add(new TrackRoute(l, l));
        }

        for(TrackSegment t : trackSegments) {
            TrackRouteSegment[] newSegments = getNewSegments(track, t);

            List<TrackRoute> newRoutes = new ArrayList<TrackRoute>();

            for(TrackRoute r : routes) {
                newRoutes.addAll(Arrays.asList(r.addSegments(newSegments)));
            }

            routes = newRoutes;
        }

/*        List<TrackRoute> newRoutes = new ArrayList<TrackRoute>();

        for(TrackRoute route : routes) {
            if(route.isValid()) {
                newRoutes.add(route);
            }
        }

        routes = newRoutes;
*/
        System.out.println(String.format("Navigator: %s possible routes plotted.", routes.size()));
    }

    public void useShortestRoute() {
        selectedRoute = stream(routes.toArray(new TrackRoute[routes.size()])).sorted(new Comparator<TrackRoute>() {
            @Override
            public int compare(TrackRoute o1, TrackRoute o2) {
                return Double.compare(o1.getRouteLength(), o2.getRouteLength());
            }
        }).findFirst().get();

        printSelectedRoute("shortest");
    }

    private void printSelectedRoute(String description) {
        System.out.println(String.format("Navigator: using %s route: %s", description, selectedRoute.getRouteLength()));
        for(int i=0;i < selectedRoute.getSegments().length;i++) {
            TrackRouteSegment trackRouteSegment = selectedRoute.getSegments()[i];
            System.out.println(i + ": " + trackRouteSegment.getDrivingLane().getDistanceFromCenter());
        }
    }

    public void useFastestRoute() {
        selectedRoute = stream(routes.toArray(new TrackRoute[routes.size()])).sorted(new Comparator<TrackRoute>() {
            @Override
            public int compare(TrackRoute o1, TrackRoute o2) {
                return Double.compare(o1.getRouteDrivingTime(), o2.getRouteDrivingTime());
            }
        }).findFirst().get();

        printSelectedRoute("fastest");
    }

    public void useCustomKeimolaRoute() {
        selectedRoute = stream(routes.toArray(new TrackRoute[routes.size()])).filter(r -> {
            TrackRouteSegment[] segments = r.getSegments();

            return segments[0].getDrivingLane().getIndex() == 0
                    && segments[1].getDrivingLane().getIndex() == 0
                    && segments[2].getDrivingLane().getIndex() == 0
                    && segments[3].getDrivingLane().getIndex() == 0
                    && segments[4].getDrivingLane().getIndex() == 0
                    && segments[5].getDrivingLane().getIndex() == 0
                    && segments[6].getDrivingLane().getIndex() == 0
                    && segments[7].getDrivingLane().getIndex() == 0;
        }).findFirst().get();

        printSelectedRoute("custom");
    }

    public void useCustomUsaRoute() {
        selectedRoute = stream(routes.toArray(new TrackRoute[routes.size()])).filter(r -> {
            TrackRouteSegment[] segments = r.getSegments();

            return segments[0].getDrivingLane().getIndex() == 1
                    && segments[1].getDrivingLane().getIndex() == 1
                    && segments[2].getDrivingLane().getIndex() == 1
                    && segments[3].getDrivingLane().getIndex() == 1
                    && segments[4].getDrivingLane().getIndex() == 1;
        }).findFirst().get();

        printSelectedRoute("custom");
    }

    private TrackRouteSegment[] getNewSegments(Track track, TrackSegment t) {
        List<TrackRouteSegment> segmentRoutes = new ArrayList<>();

        for(Lane l : track.getLanes()) {
            segmentRoutes.add(new TrackRouteSegment(l, t));
        }

        return segmentRoutes.toArray(new TrackRouteSegment[segmentRoutes.size()]);
    }

    private List<TrackSegment> createTrackSegments(Track track) {
        List<TrackSegment> segments = new ArrayList<TrackSegment>();
        List<Piece> segmentPieces = new ArrayList<Piece>();
        List<Piece> pieces = track.getPieces();

        for(int i=0;i<track.getPieces().size();i++) {
            Piece p = pieces.get(i);

            if(p.hasSwitch()) {
                segments.add(new TrackSegment(segmentPieces.toArray(new Piece[segmentPieces.size()])));
                segmentPieces = new ArrayList<Piece>();
            }

            segmentPieces.add(p);
        }

        if(segmentPieces.size() > 0) {
            segments.add(new TrackSegment(segmentPieces.toArray(new Piece[segmentPieces.size()])));
        }

        return segments;
    }

    public boolean shouldSendSwitchLanes() {
        TrackRouteSegment nextSegment = getNextSegment();
        Lane currentLane = getCurrentLane();

        //System.out.println(String.format("Lanes: %s -> %s, %s->%s", currentLane.getDistanceFromCenter(), nextSegment.getDrivingLane().getDistanceFromCenter(), getCurrentSegment().getDrivingLane().getDistanceFromCenter(), nextSegment.getDrivingLane().getDistanceFromCenter()));

        return currentLane != nextSegment.getDrivingLane() && !switchIsPending;
    }

    private boolean switchIsPending = false;

    private TrackRouteSegment getNextSegment() {
        return selectedRoute.getNextSegment(getCurrentSegment());
    }

    public SwitchLane setTargetLane() {
        TrackRouteSegment nextSegment = getNextSegment();

        Lane nextLane = nextSegment.getDrivingLane();
        SwitchLane switchLane;

        if(nextLane.getDistanceFromCenter() < getCurrentLane().getDistanceFromCenter()) {
            switchLane = new LeftSwitchLane();
        } else {
            switchLane = new RightSwitchLane();
        }

        switchIsPending = true;

        return switchLane;
    }

    public Lane getLane(Piece piece) {
        TrackRouteSegment segment = selectedRoute.getSegmentForPiece(piece.getNumber());

        return segment.getDrivingLane();
    }

    public Lane getCurrentLane() {
        return currentPosition.getLane();
    }

    public void setPosition(PlayerPosition position) {

        currentPosition = position;

        if(currentSegment != getCurrentSegment()) {
            switchIsPending = false;
            currentSegment = getCurrentSegment();
        }
    }

    public TrackRouteSegment getCurrentSegment() {
        return selectedRoute.getSegmentForPiece(currentPosition.getPieceNumber());
    }
}
