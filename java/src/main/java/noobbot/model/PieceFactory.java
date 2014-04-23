package noobbot.model;

import noobbot.descriptor.GameInitDescriptor;

public class PieceFactory {
    public Piece create(GameInitDescriptor.Data.Race.Track.Piece pieceDescriptor, int index) {
        if(pieceDescriptor.angle != 0) {
            return new AnglePiece(pieceDescriptor.radius, pieceDescriptor.angle, index, pieceDescriptor.hasSwitch);
        } else {
            return new StraightPiece(pieceDescriptor.length, index, pieceDescriptor.hasSwitch);
        }
    }
}
