package noobbot.model;

public interface Piece {

	Double getDistanceTo(Position any);
	Double getDistanceFrom(Position any);
    boolean contains(Position position);
    int getNumber();
    double getLength(int laneNumber);
}
