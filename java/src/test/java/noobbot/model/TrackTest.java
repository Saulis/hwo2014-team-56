package noobbot.model;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TrackTest {

	private Track sut;
    private Position ignoredPosition = mock(Position.class);
    private double ignoredDistance = 999.0;
	private List<Piece> pieces = new ArrayList<Piece>();
    private List<Lane> lanes = new ArrayList<Lane>();
    private int pieceNumber;
	
	@Before
	public void setup() {
		sut = new Track(pieces, lanes);
	}
    
    @Test
    public void distanceIsTheLengthOfOnlyPiece() {
        Double distanceBeforePoint = 1.0;
        Double distanceAfterPoint = 2.0;
        Piece onlyPiece = createPieceContaining(ignoredPosition, distanceBeforePoint, distanceAfterPoint);
        pieces.add(onlyPiece);
        
        Double result = sut.getDistanceBetween(ignoredPosition, ignoredPosition);
        
        Double expectedDistance = distanceBeforePoint + distanceAfterPoint;
        assertEquals(expectedDistance, result);
    }
    
    @Test
    public void distanceIsTheDistanceFromOfFirstPieceAndDistanceToOfSecondPiece() {
        Position firstPosition = mock(Position.class);
        Double distanceAfterFirstPoint = 3.5;
        Piece firstPiece = createPieceContaining(firstPosition, ignoredDistance, distanceAfterFirstPoint);
        
        Position secondPosition = mock(Position.class);
        Double distanceBeforeSecondPoint = 4.0;
        Piece secondPiece = createPieceContaining(secondPosition, distanceBeforeSecondPoint, ignoredDistance);

        pieces.add(firstPiece);
        pieces.add(secondPiece);
        
        Double result = sut.getDistanceBetween(firstPosition, secondPosition);
        
        Double expectedDistance = distanceBeforeSecondPoint + distanceAfterFirstPoint;
        assertEquals(expectedDistance, result);
    }
    
    @Test
    public void distanceIsTheDistanceFromOfFirstPieceAndLengthOfSecondAndDistanceToOfThirdPiece() {
        Position firstPosition = mock(Position.class);
        Double distanceAfterFirstPoint = 3.5;
        Piece firstPiece = createPieceContaining(firstPosition, ignoredDistance, distanceAfterFirstPoint);
        
        Double lengthOfMiddlePosition = 5.5;
        Piece middlePiece = createPiece(lengthOfMiddlePosition);
        
        Position lastPosition = mock(Position.class);
        Double distanceBeforeLastPoint = 4.0;
        Piece lastPiece = createPieceContaining(lastPosition, distanceBeforeLastPoint, ignoredDistance);

        pieces.add(firstPiece);
        pieces.add(middlePiece);
        pieces.add(lastPiece);
        
        Double result = sut.getDistanceBetween(firstPosition, lastPosition);
        
        Double expectedDistance = distanceBeforeLastPoint + lengthOfMiddlePosition + distanceAfterFirstPoint;
        assertEquals(expectedDistance, result);
    }
    
    private Piece createPiece(Double pieceLength) {
        Piece piece = createPiece();
        when(piece.getLength(any(Lane.class))).thenReturn(pieceLength);
        return piece;
    }

    private Piece createPieceContaining(Position positionOnPiece, Double returnedDistanceBefore, Double returnedDistanceAfter) {
        Piece piece = createPiece();
        when(piece.getDistanceTo(any(Position.class))).thenReturn(returnedDistanceBefore);
        when(piece.getDistanceFrom(any(Position.class))).thenReturn(returnedDistanceAfter);
        when(piece.contains(same(positionOnPiece))).thenReturn(true);
        return piece;
    }

    private Piece createPiece() {
        Piece piece = mock(Piece.class);
        when(piece.getNumber()).thenReturn(pieceNumber++);
        return piece;
    }
}
