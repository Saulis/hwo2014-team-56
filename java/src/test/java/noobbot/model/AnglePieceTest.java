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

    @Before
    public void setup() {
        sut = new AnglePiece(radius, angle, pieceNumber);

        leftLane = mock(Lane.class);
        when(leftLane.getDistanceFromCenter()).thenReturn(-20.0);
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
        angle = -45;
        double expectedLength = calculateCornerLength(radius - 20.0);

        double actulLength = sut.getLength(leftLane);

        assertThat(actulLength, is(expectedLength));
    }

    @Test
    public void pieceLengthForRightCorner() {
        angle = 45;
        double expectedLength = calculateCornerLength(radius + 20.0);

        double actulLength = sut.getLength(leftLane);

        assertThat(actulLength, is(expectedLength));
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
    
    private double calculateCornerLength(double radius) {
        return Math.PI * 2 * radius * Math.abs(angle) / 360;
    }
}
