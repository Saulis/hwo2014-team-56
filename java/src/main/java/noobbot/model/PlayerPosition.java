package noobbot.model;

import noobbot.descriptor.CarPositionsDescriptor;

/**
 * Created by Jere on 15.4.2014.
 */
public class PlayerPosition implements Position {
    private CarPositionsDescriptor.Data data;
    private Lane lane;

    public PlayerPosition(Track track, CarPositionsDescriptor.Data data) {
        this.data = data;
        this.lane = track.getLanes().get(getLaneNumber());
    }

    public double getSlipAngle() {
        return data.angle;
    }

    public CarPositionsDescriptor.Data.PiecePosition getPiecePosition() {
        return data.piecePosition;
    }

    @Override
    public int getLaneNumber() {
        return (int) data.piecePosition.lane.startLaneIndex;
    }

    @Override
    public int getPieceNumber() {
        return (int) data.piecePosition.pieceIndex;
    }

    @Override
    public double getInPieceDistance() {
        return data.piecePosition.inPieceDistance;
    }

    @Override
    public Lane getLane() {
        return lane;
    }
}
