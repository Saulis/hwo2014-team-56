package noobbot.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

/**
 * Created by Saulis on 21/04/14.
 */
public class TrackRoute {
    private Lane startLane;
    private Lane endLane;
    private TrackRouteSegment[] segments;
    private int ranking;

    public TrackRoute(Lane startLane, Lane endLane) {
        this.startLane = startLane;
        this.endLane = endLane;
        this.segments = new TrackRouteSegment[0];
    }

    public TrackRoute(Lane startLane, Lane endLane, TrackRouteSegment[] segments) {
        this.startLane = startLane;
        this.endLane = endLane;
        this.segments = segments;

        ranking = getRouteRank();
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
            s.setSwitchIsUsed(previousTrackSegment.getDrivingLane() != s.getDrivingLane());
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

    public double getRouteLength() {
        return stream(segments).mapToDouble(s -> s.getSegmentLength()).sum();
    }

    public double getRouteDrivingTime() {
        return stream(segments).mapToDouble(s -> s.getSegmentDrivingTime()).sum();
    }

    public Piece getPieceForNextTargetSpeed(Piece piece) {
        double targetSpeed = getTargetSpeed(piece);

        Piece nextPiece = getNextPiece(piece);
        while(targetSpeed != getTargetSpeed(nextPiece)) {
            nextPiece = getNextPiece(nextPiece);
        }

        return nextPiece;
    }

    public double getDistanceBetween(Piece piece1, Piece piece2) {
        double distance = getDrivingLength(piece1);
        Piece nextPiece = getNextPiece(piece1);

        while(nextPiece != piece2) {
            distance += getDrivingLength(nextPiece);
            nextPiece = getNextPiece(nextPiece);
        }

        return distance;
    }

    public double getDrivingLength(Piece piece) {
        TrackRouteSegment segment = getSegmentForPiece(piece.getNumber());

        return piece.getLength(segment.getDrivingLane());
    }

    private double getTargetSpeed(Piece piece) {
        TrackRouteSegment segment = getSegmentForPiece(piece.getNumber());

        return piece.getTargetSpeed(segment.getDrivingLane());
    }

    private Piece getNextPiece(Piece piece) {
        TrackRouteSegment segment = getSegmentForPiece(piece.getNumber());

        Optional<Piece> nextPiece = segment.getPiece(piece.getNumber() + 1);
        if(nextPiece.isPresent()) {
            return nextPiece.get();
        } else {
            segment = getNextSegment(segment);

            return segment.getFirstPiece();
        }
    }

    private int getRouteRank() {
        int ranking = 0;
        for(int i=0;i < segments.length - 1;i++) {
            TrackRouteSegment firstSegment = segments[i];
            TrackRouteSegment laterSegment = segments[i + 1];
            ranking += rankSegments(firstSegment, laterSegment);
        }

        if(segments[0].getDrivingLane() != segments[segments.length-1].getDrivingLane()) {
            ranking -= 100;
        }

        return ranking;
    }

    public int rankSegments(TrackRouteSegment firstSegment, TrackRouteSegment laterSegment) {
        int ranking = 0;

        //Lane is switched to inside lane when turning into a corner
        if(laterSegment.hasSwitchJustBeforeRightCorner() && hasLaneIsSwitchedRight(firstSegment, laterSegment)) {
            ranking++;
        }

        if(laterSegment.hasSwitchJustBeforeRightCorner() && hasLaneIsSwitchedLeft(firstSegment, laterSegment)) {
            ranking--;
        }

        if(laterSegment.hasSwitchWhenTurningIntoARightCorner() && hasLaneIsSwitchedRight(firstSegment, laterSegment)) {
            ranking++;
        }

        if(laterSegment.hasSwitchWhenTurningIntoARightCorner() && hasLaneIsSwitchedLeft(firstSegment, laterSegment)) {
            ranking--;
        }

        if(laterSegment.hasSwitchJustBeforeLeftCorner() && hasLaneIsSwitchedLeft(firstSegment, laterSegment)) {
            ranking++;
        }

        if(laterSegment.hasSwitchJustBeforeLeftCorner() && hasLaneIsSwitchedRight(firstSegment, laterSegment)) {
            ranking--;
        }

        if(laterSegment.hasSwitchWhenTurningIntoALeftCorner() && hasLaneIsSwitchedLeft(firstSegment, laterSegment)) {
            ranking++;
        }

        if(laterSegment.hasSwitchWhenTurningIntoALeftCorner() && hasLaneIsSwitchedRight(firstSegment, laterSegment)) {
            ranking--;
        }

        //Lane is switched to outer lane when accelerating out from a corner.
        if(firstSegment.endsInRightCorner() && hasLaneIsSwitchedLeft(firstSegment, laterSegment)) {
            ranking++;
            ranking++;
        }

        if(firstSegment.endsInRightCorner() && hasLaneIsSwitchedRight(firstSegment, laterSegment)) {
            ranking--;
            ranking--;
        }

        if(firstSegment.endsInLeftCorner() && hasLaneIsSwitchedRight(firstSegment, laterSegment)) {
            ranking++;
            ranking++;
        }

        if(firstSegment.endsInLeftCorner() && hasLaneIsSwitchedLeft(firstSegment, laterSegment)) {
            ranking--;
            ranking--;
        }
        return ranking;
    }

    private boolean hasLaneIsSwitchedLeft(TrackRouteSegment firstSegment, TrackRouteSegment laterSegment) {
        return firstSegment.getDrivingLane().getDistanceFromCenter() > laterSegment.getDrivingLane().getDistanceFromCenter();
    }

    private boolean hasLaneIsSwitchedRight(TrackRouteSegment firstSegment, TrackRouteSegment laterSegment) {
        return firstSegment.getDrivingLane().getDistanceFromCenter() < laterSegment.getDrivingLane().getDistanceFromCenter();
    }

    public int getRanking() {
        return ranking;
    }
}
