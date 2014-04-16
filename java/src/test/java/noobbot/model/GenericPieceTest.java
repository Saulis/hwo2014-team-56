package noobbot.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

public abstract class GenericPieceTest {

    protected int pieceNumber = 5;

    @Test
    public void positionWithSamePieceNumberIsOnPiece() {
        Position position = mock(Position.class);
        when(position.getPieceNumber()).thenReturn(pieceNumber);
        assertTrue(getSut().contains(position));
    }

    @Test
    public void positionWithDifferentPieceNumberIsNotOnPiece() {
        int differentPieceNumber = pieceNumber + 1;
        Position position = mock(Position.class);
        when(position.getPieceNumber()).thenReturn(differentPieceNumber);
        assertFalse(getSut().contains(position));
    }
    
    @Test
    public void distanceToPositionIsPositionsInPieceDistance() throws Exception {
        double expectedDistance = 123.4;
        Position position = mock(Position.class);
        when(position.getInPieceDistance()).thenReturn(expectedDistance);
        
        Double result = getSut().getDistanceTo(position);
        
        assertEquals(expectedDistance, result, 0.0);
    }

    @Test
    public void pieceNumberIsNumberProvidedInConstructor() throws Exception {
        assertEquals(pieceNumber, getSut().getNumber());
    }
    
    protected abstract GenericPiece getSut();
}
