package noobbot;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Saulis on 15/04/14.
 */
public class GameInit {
    public class Data {
        public class Race {
        public class Track {
            public class Piece {
                public double length;
                public double radius;
                public double angle;

                @SerializedName("switch")
                public boolean hasSwitch;
            }

            public class Lane {
                public double distanceFromCenter;
                public int index;
            }

            public class StartingPoint {
                public class Position {
                    public double x;
                    public double y;
                }

                public Position position;
                public double angle;
            }

            public String id;
            public String name;
            public Piece[] pieces;
            public Lane[] lanes;
            public StartingPoint startingPoint;
        }

        public class Car {
            public class Id {
                public String name;
                public String color;
            }

            public class Dimensions {
                public double length;
                public double width;
                public double guideFlagPosition;
            }

            public Id id;
            public Dimensions dimensions;
        }

        public Track track;
        public Car[] cars;
    }
        
        public Race race;
    }

    public String msgType;
    public Data data;
}
