package noobbot.model;

import java.util.ArrayList;

import static java.util.Arrays.stream;

/**
 * Created by jereketonen on 4/16/14.
 */
public class CarMetrics {
    private Track track;
    private Position currentPosition;
    private Position previousPosition;
    private double previousSpeed = 0;
    private double topspeed = 0;
    private double currentThrottle;
    private double previousThrottle;
    private boolean measuring = false;
    private double previousAcceleration;

    public CarMetrics(Track track) {
        this.track = track;
    }

    public void update(Metric metric) {
        previousAcceleration = getCurrentAcceleration();
        previousSpeed = getCurrentSpeed();
        previousPosition = this.currentPosition;
        previousThrottle = this.currentThrottle;

        this.currentThrottle = metric.getThrottle();
        this.currentPosition = metric.getPosition();

        measureTopspeed();
    }

    //Ugly but works
    private void measureTopspeed() {

        //start measuring if we start hitting full throttle from zero
        if(getCurrentSpeed() == 0 && this.currentThrottle == 1.0) {
            measuring = true;
        }

        if(currentThrottle != 1.0) {
            measuring = false;
        }

        if(measuring && previousAcceleration > 0) {
            measuring = false;

            double accelerationRatio = getCurrentAcceleration() / previousAcceleration;
            topspeed = previousAcceleration / (1 - accelerationRatio);

            System.out.println("Measured top speed at " + topspeed);
        }
    }

    public double getCurrentSpeed() {
        if(!hasPreviousPosition()) {
            return 0;
        }

        if(previousPositionWasOnCurrentPiece()) {
            return currentPosition.getInPieceDistance() - previousPosition.getInPieceDistance();
        } else {
            return currentPosition.getInPieceDistance() + (getPreviousPieceLength() - previousPosition.getInPieceDistance());
        }
    }

    private double getPreviousPieceLength() {
        return getPieceLength(previousPosition);
    }

    private boolean previousPositionWasOnCurrentPiece() {
        return previousPosition.getPieceNumber() == currentPosition.getPieceNumber();
    }

    private boolean hasPreviousPosition() {
        return previousPosition != null;
    }

    private double getPieceLength(Position piecePosition) {
        Piece piece = track.getPiece(piecePosition);

        return piece.getLength(piecePosition.getLane());
    }

    public double getCurrentAcceleration() {
        return getCurrentSpeed() - previousSpeed;
    }

    public double getTopspeed() {
        return topspeed;
    }
}
