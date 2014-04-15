package noobbot;

/**
 * Created by Saulis on 15/04/14.
 */
public class GameInit {
    public class Data {
        public class Track {
            public class Piece {
                public double length;
                public double radius;
                public double angle;
                //public boolean ;
            }

            public String id;
            public String name;
            public Piece[] pieces;
        }

        public Track track;
    }

    public String msgType;
    public Data data;
}
