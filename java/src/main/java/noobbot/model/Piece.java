package noobbot.model;

public interface Piece {

	Double getDistanceTo(Position position);
	Double getDistanceFrom(Position position);
    boolean contains(Position position);
    int getNumber();
    double getLength(Lane lane);
    double getAngle();
    double getTargetSpeed(Lane lane);
    boolean hasSwitch();

    double getDrivingTime(Lane lane);
}
