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
    private final double leftLaneOffset = -20.0;
    private final double hardcodedAngleSpeed = 3.75;

    @Before
    public void setup() {
        sut = new AnglePiece(radius, angle, pieceNumber, false);

        leftLane = mock(Lane.class);
        when(leftLane.getDistanceFromCenter()).thenReturn(leftLaneOffset);
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
        AnglePiece sut = new AnglePiece(radius, -45, 1, false);
        double expectedLength = calculateCornerLength(radius - leftLaneOffset);

        double actualLength = sut.getLength(leftLane);

        assertThat(actualLength, is(expectedLength));
    }

    @Test
    public void pieceLengthForRightCorner() {
        angle = 45;
        double expectedLength = calculateCornerLength(radius + 20.0);

        double actualLength = sut.getLength(leftLane);

        assertThat(actualLength, is(expectedLength));
    }

    @Test
    public void topSpeedIsLengthDividedByTheAbsoluteValueOfTheAngleDividedByTargetAngleSpeed() {
        double expectedSpeed = calculateTargetSpeed(radius);

        double actualSpeed = sut.getTargetSpeed(ignoredLane, ignoredLane);

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

    private double calculateTargetSpeed(double radius) { return calculateCornerLength(radius) / Math.abs(angle) / hardcodedAngleSpeed;}
    private double calculateCornerLength(double radius) {
        return Math.PI * 2 * radius * Math.abs(angle) / 360;
    }
}
