package noobbot.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

public class StraightPieceTest extends GenericPieceTest {

    private StraightPiece sut;
    private double pieceLength = 100.0;
    private int ignoredLaneNumber = 2;
    
    @Before
    public void setup() {
        sut = new StraightPiece(pieceLength, pieceNumber);
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
    public void pieceLengthIsLengthProvidedInConstructor() throws Exception {
        assertEquals(pieceLength, sut.getLength(ignoredLaneNumber), 0.0);
    }

    @Override
    protected GenericPiece getSut() {
        return sut;
    }
}
