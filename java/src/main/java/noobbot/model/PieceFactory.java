package noobbot.model;

public class PieceFactory {
    public Piece create(noobbot.descriptor.GameInitDescriptor.Data.Race.Track.Piece pieceDescriptor, int index) {
        if(pieceDescriptor.angle != 0) {
            // TODO: switch missing
            return new AnglePiece(pieceDescriptor.radius, pieceDescriptor.angle, index);
        } else {
            //TODO: piece number missing...
            return new StraightPiece(pieceDescriptor.length, index);
        }
    }
}