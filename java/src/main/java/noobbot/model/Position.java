package noobbot.model;

import noobbot.descriptor.CarPositionsDescriptor;

public interface Position {
    int getLaneNumber();
    int getPieceNumber();
    double getInPieceDistance();
    double getSlipAngle();
    CarPositionsDescriptor.Data.PiecePosition getPiecePosition();
    Lane getLane();
}
