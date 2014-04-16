package noobbot.model;

public class PieceFactory {
    public Piece create(noobbot.descriptor.GameInitDescriptor.Data.Race.Track.Piece pieceDescriptor) {
        if(pieceDescriptor.angle != 0) {
            //TODO: piece number missing...
            //TODO: has switch missing...
            return new AnglePiece(pieceDescriptor.radius, pieceDescriptor.angle, 666);
        } else {
            //TODO: piece number missing...
            return new StraightPiece(pieceDescriptor.length, 667);
        }
    }
}
