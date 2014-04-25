package noobbot.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

public class AnglePieceTest extends GenericPieceTest {

    private AnglePiece sut;
    private double radius = 200.0;
    private double angle = 45;
    private Lane ignoredLane = mock(Lane.class);
    private Lane leftLane;
    private Lane rightLane;
    private final double leftLaneOffset = -20.0;
    private final double rightLaneOffset = 20.0;
    private TargetAngleSpeed targetAngleSpeed = mock(TargetAngleSpeed.class);

    @Before
    public void setup() {
        sut = new AnglePiece(radius, angle, pieceNumber, false, targetAngleSpeed);
        leftLane = mockLaneWithDistance(leftLaneOffset);
        rightLane = mockLaneWithDistance(rightLaneOffset);
    }
    
    @Test
    public void distanceFromPositionIsCornerLengthMinusInPieceDistance() throws Exception {
        double inPiecePosition = 34.5;
        Position position = mock(Position.class);
        Lane laneInCenter = mock(Lane.class);
        when(laneInCenter.getDistanceFromCenter()).thenReturn(0.0);
        when(position.getLane()).thenReturn(laneInCenter );
        when(position.getInPieceDistance()).thenReturn(inPiecePosition);
        
        double result = sut.getDistanceFrom(position);
        
        double laneLength = calculateCornerLength(radius);
        double expectedDistance = laneLength - inPiecePosition;
        assertEquals(expectedDistance, result, 0.0);
    }

    @Test
    public void pieceLengthForLeftCorner() {
        AnglePiece sut = new AnglePiece(radius, -45, 1, false, targetAngleSpeed);
        double expectedLength = calculateCornerLength(radius + leftLaneOffset);

        double actualLength = sut.getLength(leftLane);

        assertThat(actualLength, is(expectedLength));
    }

    @Test
    public void pieceLengthForRightCorner() {
        AnglePiece sut = new AnglePiece(radius, 45, 1, false, targetAngleSpeed);
        double expectedLength = calculateCornerLength(radius - rightLaneOffset);

        double actualLength = sut.getLength(rightLane);

        assertThat(actualLength, is(expectedLength));
    }

    @Test
    public void topSpeedIsLengthDividedByTheAbsoluteValueOfTheAngleDividedByTargetAngleSpeed() {
        double expectedAngleSpeed = 5.0;
        double expectedSpeed = calculateTargetSpeed(expectedAngleSpeed , radius);
        when(targetAngleSpeed.getValue()).thenReturn(expectedAngleSpeed);

        double actualSpeed = sut.getTargetSpeed(ignoredLane);

        assertThat(actualSpeed, is(expectedSpeed));
    }

    @Test
    public void pieceLengthIsCornerLength() throws Exception {
        double expectedLength = calculateCornerLength(radius);
        double result = sut.getLength(ignoredLane);
        assertEquals(expectedLength, result, 0.0);
    }
    
    @Override
    protected GenericPiece getSut() {
        return sut;
    }

    private double calculateTargetSpeed(double angleSpeed, double radius) { return calculateCornerLength(radius) / (Math.abs(angle) / angleSpeed);}
    private double calculateCornerLength(double radius) {
        return Math.PI * 2 * radius * Math.abs(angle) / 360;
    }

    private Lane mockLaneWithDistance(double distance) {
        Lane lane = mock(Lane.class);
        when(lane.getDistanceFromCenter()).thenReturn(distance);
        return lane;
    }
}
