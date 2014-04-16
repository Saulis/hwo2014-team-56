package noobbot.model;

import noobbot.descriptor.CarPositionsDescriptor;

public interface Position {
    double getSlipAngle();
    public CarPositionsDescriptor.Data.PiecePosition getPiecePosition();
}
