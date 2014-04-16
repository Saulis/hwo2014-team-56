package noobbot.model;

import noobbot.descriptor.GameInitDescriptor;

/**
 * Created by jereketonen on 4/16/14.
 */
public class Car {

    private Position position;
    private Position previousPosition;

    public void setPosition(Position newPosition) {
        previousPosition = position;
        position = newPosition;
    }

    public Position getPosition() {
        return position;
    }


    public double getSpeed(GameInitDescriptor.Data.Race.Track track) {
        if(previousPosition == null) {
            return 0;
        }

        if(previousPosition.getPieceNumber() == position.getPieceNumber()) {
            return position.getInPieceDistance() - previousPosition.getInPieceDistance();
        } else {
            double length = getPieceLength(track, previousPosition);
            return position.getInPieceDistance() + (length - previousPosition.getInPieceDistance());
        }
    }

    private double getPieceLength(GameInitDescriptor.Data.Race.Track track, Position piecePosition) {
        GameInitDescriptor.Data.Race.Track.Piece piece = track.pieces[((int) piecePosition.getPieceNumber())];

        if(piece.angle != 0) {
            return Math.abs(piece.angle) / 360 * 2 * Math.PI * getEffectiveRadius(track.lanes[0], piece);
        } else {
            return piece.length;
        }
    }

    private double getEffectiveRadius(GameInitDescriptor.Data.Race.Track.Lane lane, GameInitDescriptor.Data.Race.Track.Piece piece) {
        if(isLeftTurn(piece)) {
            return piece.radius +lane.distanceFromCenter;
        }

        return piece.radius - lane.distanceFromCenter;
    }

    private boolean isLeftTurn(GameInitDescriptor.Data.Race.Track.Piece piece) {
        return piece.angle < 0;
    }

}
