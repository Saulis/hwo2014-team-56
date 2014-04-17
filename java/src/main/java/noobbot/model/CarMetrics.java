package noobbot.model;

/**
 * Created by jereketonen on 4/16/14.
 */
public class CarMetrics {
    private Track track;
    private Position currentPosition;
    private Position previousPosition;

    public CarMetrics(Track track) {
        this.track = track;
    }

    public void setPosition(Position newPosition) {
        previousPosition = currentPosition;
        currentPosition = newPosition;
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


    //TODO: this method would go to AnglePiece
    private boolean isLeftTurn(Piece piece) {
        return piece.getAngle() < 0;
    }

    //TODO: this method would probably go to Piece?
    private double getEffectiveRadius(Lane lane, Piece piece) {
        if(isLeftTurn(piece)) {
            return piece.getRadius() +lane.getDistanceFromCenter();
        }

        return piece.getRadius() - lane.getDistanceFromCenter();
    }

}
