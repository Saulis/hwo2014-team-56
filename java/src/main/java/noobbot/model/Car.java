package noobbot.model;

/**
 * Created by jereketonen on 4/16/14.
 */
public class Car {

    private Position position;
    private Position previousPosition;
    private Track track;

    public Car(Track track) {
        this.track = track;
    }

    public void setPosition(Position newPosition) {
        previousPosition = position;
        position = newPosition;
    }

    public Position getPosition() {
        return position;
    }


    public double getSpeed() {
        if(previousPosition == null) {
            return 0;
        }

        if(previousPosition.getPieceNumber() == position.getPieceNumber()) {
            return position.getInPieceDistance() - previousPosition.getInPieceDistance();
        } else {
            double length = getPieceLength(previousPosition);
            return position.getInPieceDistance() + (length - previousPosition.getInPieceDistance());
        }
    }

    private double getPieceLength(Position piecePosition) {
        Piece piece = track.getPieces().get(piecePosition.getPieceNumber());

        if(piece.getAngle() != 0) {
            return Math.abs(piece.getAngle()) / 360 * 2 * Math.PI * getEffectiveRadius(track.getLanes().get(0), piece); // Hardcoded lane value.
        } else {
            return piece.getLength(0); //Hardcoded lane value.
        }
    }

    private double getEffectiveRadius(Lane lane, Piece piece) {
        if(isLeftTurn(piece)) {
            return piece.getRadius() +lane.getDistanceFromCenter();
        }

        return piece.getRadius() - lane.getDistanceFromCenter();
    }

    private boolean isLeftTurn(Piece piece) {
        return piece.getAngle() < 0;
    }

}
