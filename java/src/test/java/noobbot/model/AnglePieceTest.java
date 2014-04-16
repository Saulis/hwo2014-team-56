package noobbot.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

public class AnglePieceTest extends GenericPieceTest {

    private AnglePiece sut;
    private double radius = 200.0;
    private double angle = -45;
    private int ignoredLaneNumber = 3;

    @Before
    public void setup() {
        sut = new AnglePiece(radius, angle, pieceNumber);
    }
    
    @Test
    public void distanceFromPositionIsCornerLengthMinusInPieceDistance() throws Exception {
        double inPiecePosition = 34.5;
        Position position = mock(Position.class);
        when(position.getInPieceDistance()).thenReturn(inPiecePosition);
        
        double result = sut.getDistanceFrom(position);
        
        double laneLength = calculateCornerLength();
        double expectedDistance = laneLength - inPiecePosition;
        assertEquals(expectedDistance, result, 0.0);
    }
    
    @Test
    public void pieceLengthIsCornerLength() throws Exception {
        double expectedLength = calculateCornerLength();
        double result = sut.getLength(ignoredLaneNumber );
        assertEquals(expectedLength, result, 0.0);
    }
    
    @Override
    protected GenericPiece getSut() {
        return sut;
    }
    
    private double calculateCornerLength() {
        return Math.PI * 2 * radius * angle / 360;
    }
}
