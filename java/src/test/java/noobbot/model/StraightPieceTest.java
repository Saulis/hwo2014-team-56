package noobbot.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

public class StraightPieceTest {

    private StraightPiece sut;
    private double pieceLength = 100.0;
    private int pieceNumber = 5;
    private int ignoredLaneNumber = 2;
    
    @Before
    public void setup() {
        sut = new StraightPiece(pieceLength, pieceNumber);
    }
    
    @Test
    public void positionWithSamePieceNumberIsOnPiece() {
        Position position = mock(Position.class);
        when(position.getPieceNumber()).thenReturn(pieceNumber);
        assertTrue(sut.contains(position));
    }
    
    @Test
    public void positionWithDifferentPieceNumberIsNotOnPiece() {
        int differentPieceNumber = pieceNumber + 1;
        Position position = mock(Position.class);
        when(position.getPieceNumber()).thenReturn(differentPieceNumber);
        assertFalse(sut.contains(position));
    }

    @Test
    public void distanceToPositionIsPositionsInPieceDistance() throws Exception {
        double expectedDistance = 123.4;
        Position position = mock(Position.class);
        when(position.getInPieceDistance()).thenReturn(expectedDistance);
        
        Double result = sut.getDistanceTo(position);
        
        assertEquals(expectedDistance, result, 0.0);
    }

    @Test
    public void distanceFromPositionIsPieceLengthMinusInPiecePosition() throws Exception {
        Double inPiecePosition = 45.6;
        Position position = mock(Position.class);
        when(position.getInPieceDistance()).thenReturn(inPiecePosition);
        
        Double result = sut.getDistanceFrom(position);
        
        double expectedDistance = pieceLength - inPiecePosition;
        assertEquals(expectedDistance, result, 0.0);
    }

    @Test
    public void pieceNumberIsNumberProvidedInConstructor() throws Exception {
        assertEquals(pieceNumber, sut.getNumber());
    }

    @Test
    public void pieceLengthIsLengthProvidedInConstructor() throws Exception {
        assertEquals(pieceLength, sut.getLength(ignoredLaneNumber), 0.0);
    }
}
