package noobbot.model;

import noobbot.descriptor.CarPositionsDescriptor.Data.PiecePosition;

public class TrackPosition implements Position {

    private Piece piece;
    private double distanceIntoPiece;
    private Lane lane;

    public TrackPosition(Piece piece, double distanceIntoPiece, Lane lane) {
        this.piece = piece;
        this.distanceIntoPiece = distanceIntoPiece;
        this.lane = lane;
    }

    @Override
    public int getLaneNumber() {
        return 0;
    }

    @Override
    public int getPieceNumber() {
        return piece.getNumber();
    }

    @Override
    public double getInPieceDistance() {
        return distanceIntoPiece;
    }

    @Override
    public double getSlipAngle() {
        throw new Error("N/A to track piece");
    }

    @Override
    public PiecePosition getPiecePosition() {
        throw new Error("Descriptor not to be used!");
    }

    @Override
    public Lane getLane() {
        return lane;
    }

}
