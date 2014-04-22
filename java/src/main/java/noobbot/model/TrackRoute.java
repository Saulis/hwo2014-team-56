package noobbot.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

/**
 * Created by Saulis on 21/04/14.
 */
public class TrackRoute {
    private Lane startLane;
    private Lane endLane;
    private TrackRouteSegment[] segments;

    public TrackRoute(Lane startLane, Lane endLane) {
        this.startLane = startLane;
        this.endLane = endLane;
        this.segments = new TrackRouteSegment[0];
    }

    public TrackRoute(Lane startLane, Lane endLane, TrackRouteSegment[] segments) {
        this.startLane = startLane;
        this.endLane = endLane;
        this.segments = segments;
    }

    public TrackRoute[] addSegments(TrackRouteSegment[] segments) {
        List<TrackRoute> newRoutes = stream(segments)
                .filter(s -> drivingLanesAreNextToEachOther(s))
                .map(s -> createNewRoute(s)).collect(Collectors.toList());

        return newRoutes.toArray(new TrackRoute[newRoutes.size()]);
    }

    private boolean drivingLanesAreNextToEachOther(TrackRouteSegment s) {
        if(!hasPreviousSegment()) {
            return true;
        }

        TrackRouteSegment previousSegment = getPreviousTrackSegment();
        int laneIndex = previousSegment.getDrivingLane().getIndex();

        return Math.abs(s.getDrivingLane().getIndex() - laneIndex) <= 1;
    }

    private boolean hasPreviousSegment() {
        return this.segments.length > 0;
    }

    private TrackRouteSegment getPreviousTrackSegment() {
        return this.segments[this.segments.length - 1];
    }

    private TrackRoute createNewRoute(TrackRouteSegment s) {
        List<TrackRouteSegment> segmentRoutes = new ArrayList<>();
        segmentRoutes.addAll(Arrays.asList(this.segments));
        segmentRoutes.add(s);

        if(hasPreviousSegment()) {
            TrackRouteSegment previousTrackSegment = getPreviousTrackSegment();
            s.setPreviousDrivingLane(previousTrackSegment.getDrivingLane());
        }

        return new TrackRoute(startLane, endLane, segmentRoutes.toArray(new TrackRouteSegment[segmentRoutes.size()]));
    }

    public TrackRouteSegment[] getSegments() {
        return this.segments;
    }

    public TrackRouteSegment getSegmentForPiece(int pieceIndex) {
        for(TrackRouteSegment segment : segments) {
            if(segment.containsPiece(pieceIndex)) {
                return segment;
            }
        }

        System.out.println(String.format("No segment found for piece %s, wtf?", pieceIndex));
        return null;
    }

    public TrackRouteSegment getNextSegment(TrackRouteSegment segment) {
        int index = Arrays.asList(segments).indexOf(segment);

        return segments[++index % segments.length];
    }

    public TrackRouteSegment getPreviousSegment(TrackRouteSegment segment) {
        int index = Arrays.asList(segments).indexOf(segment);

        return segments[Math.abs(index - segments.length) % segments.length];
    }

    public boolean isValid() {
        return this.endLane == segments[segments.length - 1].getDrivingLane();
    }

    public double getRouteLength() {
        return stream(segments).mapToDouble(s -> s.getSegmentLength()).sum();
    }

    public double getRouteDrivingTime() {
        return stream(segments).mapToDouble(s -> s.getSegmentDrivingTime()).sum();
    }
}
