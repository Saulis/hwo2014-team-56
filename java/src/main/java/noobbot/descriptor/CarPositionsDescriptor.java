package noobbot.descriptor;

/**
 * Created by Saulis on 15/04/14.
 */
public class CarPositionsDescriptor {

    public class Data {
        public class Id {
            public String name;
            public String color;
        }

        public class PiecePosition {
            public class Lane {
                public double startLaneIndex;
                public double endLaneIndex;
            }

            public double pieceIndex;
            public double inPieceDistance;
            public Lane lane;
            public double lap;
        }


        public Id id;
        public double angle;
        public PiecePosition piecePosition;
    }

    public String msgType;
    public Data[] data;

    public double getSlipAngle() {
        //TODO: Fix to find our car.
        return data[0].angle;
    }
}
