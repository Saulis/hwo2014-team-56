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
    private final TrackRoute shortest;
    private Lane currentLane;

    public Navigator(Track track) {

        this.track = track;
        currentLane = track.getLanes().get(0); //TODO: check this from game init.

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

        List<TrackRoute> newRoutes = new ArrayList<TrackRoute>();

        for(TrackRoute route : routes) {
            if(route.isValid()) {
                newRoutes.add(route);
            }
        }

        routes = newRoutes;

        shortest = stream(routes.toArray(new TrackRoute[routes.size()])).sorted(new Comparator<TrackRoute>() {
            @Override
            public int compare(TrackRoute o1, TrackRoute o2) {
                return Double.compare(o1.getRouteLength(), o2.getRouteLength());
            }
        }).findFirst().get();

        System.out.println(String.format("Navigator: %s possible routes plotted. Shortest is: %s", routes.size(), shortest.getRouteLength()));

        for(int i=0;i < shortest.getSegments().length;i++) {
            TrackRouteSegment trackRouteSegment = shortest.getSegments()[i];
            System.out.println(i + ": " + trackRouteSegment.getDrivingLane().getDistanceFromCenter());
        }
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

    public boolean switchLanesMaybe(PlayerPosition position) {
        TrackRouteSegment segmentForNextPiece = getTrackSegmentForNextPiece(position);

        System.out.println(String.format("Lanes: %s -> %s", currentLane.getDistanceFromCenter(), segmentForNextPiece.getDrivingLane().getDistanceFromCenter()));

        return currentLane != segmentForNextPiece.getDrivingLane();
    }

    private TrackRouteSegment getTrackSegmentForNextPiece(PlayerPosition position) {
        Piece currentPiece = track.getPiece(position);
        Piece nextPiece = track.getPieceAfter(currentPiece);

        return shortest.getSegmentForPiece(nextPiece.getNumber());
    }

    public SwitchLane setNextLane(PlayerPosition position) {
        TrackRouteSegment segmentForNextPiece = getTrackSegmentForNextPiece(position);

        Lane nextLane = segmentForNextPiece.getDrivingLane();
        SwitchLane switchLane;

        if(nextLane.getDistanceFromCenter() < currentLane.getDistanceFromCenter()) {
            switchLane = new LeftSwitchLane();
        } else {
            switchLane = new RightSwitchLane();
        }

        currentLane = nextLane;

        return switchLane;
    }

    public Lane getLane(Piece piece) {
        TrackRouteSegment segment = shortest.getSegmentForPiece(piece.getNumber());

        return segment.getDrivingLane();
    }
}
