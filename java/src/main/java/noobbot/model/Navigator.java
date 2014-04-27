package noobbot.model;

import static java.util.Arrays.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import noobbot.LeftSwitchLane;
import noobbot.RightSwitchLane;
import noobbot.SwitchLane;
import noobbot.model.navigation.RouteStrategy;

/**
 * Created by Saulis on 21/04/14.
 */
public class Navigator {
    private Track track;
    private List<TrackRoute> routes;
    private TrackRoute selectedRoute;
    private PlayerPosition currentPosition;
    private TrackRouteSegment currentSegment;
    private boolean switchIsPending = false;
    private double turboTimeLeft = 0;
    private List<TrackRoute> staticRoutes;
    private boolean followingSelectedRoute = true;

    public Navigator(Track track, RouteStrategy routeStrategy) {

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

        staticRoutes = stream(routes.toArray(new TrackRoute[routes.size()])).filter(r -> r.getNumberOfSwitchesUsed() == 0).collect(Collectors.toList());

        System.out.println(String.format("Navigator: %s possible routes plotted.", routes.size()));
        
        selectedRoute = routeStrategy.getRoute(routes);
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

    private TrackRouteSegment getNextSegment() {
        return getSelectedRoute().getNextSegment(getCurrentSegment());
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

    public Lane getCurrentLane() {
        return currentPosition.getLane();
    }

    public void setPosition(PlayerPosition position) {

        if (currentPosition != null && currentPosition.getPieceNumber() != position.getPieceNumber()) {
            getCurrentPiece().complete();
        }
        
        currentPosition = position;
        turboTimeLeft -= 1;
        
        if(currentSegment != getCurrentSegment()) {
            switchIsPending = false;
            currentSegment = getCurrentSegment();

            followingSelectedRoute = currentSegment.getDrivingLane() == getCurrentLane();
        }
        
        getCurrentPiece().calibrate(currentPosition.getSlipAngle());
    }

    public TrackRouteSegment getCurrentSegment() {
        return getSelectedRoute().getSegmentForPiece(currentPosition.getPieceNumber());
    }

    public TrackRoute getSelectedRoute() {
        if(followingSelectedRoute) {
            return selectedRoute;
        }

        return getStaticRoute(getCurrentLane());
    }

    private TrackRoute getStaticRoute(Lane currentLane) {
        return stream(staticRoutes.toArray(new TrackRoute[staticRoutes.size()])).filter(r -> r.getSegments()[0].getDrivingLane() == currentLane).findFirst().get();
    }

    public Piece getNextCorner() {
        TrackRoute selectedRoute = getSelectedRoute();
        Piece currentPiece = getCurrentPiece();
        double currentTargetSpeed = currentPiece.getTargetSpeed(getCurrentLane());
        boolean currentCornerHasEnded = false;

        Piece nextPiece = track.getPieceAfter(currentPiece);
        TrackRouteSegment segment = selectedRoute.getSegmentForPiece(nextPiece.getNumber());
        double targetSpeed = nextPiece.getTargetSpeed(segment.getDrivingLane());

        while((targetSpeed == currentTargetSpeed && !currentCornerHasEnded) || nextPiece.getAngle() == 0) {
            if(nextPiece.getAngle() == 0) {
                currentCornerHasEnded = true;
            }

            nextPiece = track.getPieceAfter(nextPiece);
            segment = selectedRoute.getSegmentForPiece(nextPiece.getNumber());
            targetSpeed = nextPiece.getTargetSpeed(segment.getDrivingLane());
        }

        return nextPiece;
    }

    public Piece getCurrentPiece() {
        return track.getPieces().get(currentPosition.getPieceNumber());
    }

    public double getDistanceToTarget(Piece targetPiece) {
        return getSelectedRoute().getDistanceBetween(getCurrentPiece(), targetPiece) - currentPosition.getInPieceDistance();
    }

    public void useTurbo(Turbo turbo) {
        turboTimeLeft = turbo.getDurationInTicks();
    }

    public boolean isTurboActive() {
        return turboTimeLeft > 0;
    }
}
