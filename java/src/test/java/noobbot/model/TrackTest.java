package noobbot.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import noobbot.descriptor.GameInitDescriptor;

import org.junit.Before;
import org.junit.Test;

public class TrackTest {

	private Track sut;
	private GameInitDescriptor.Data.Race.Track trackDescriptor;
	private Position ignoredPosition = mock(Position.class);
	private PieceFactory pieceFactory = mock(PieceFactory.class);
	
	@Before
	public void setup() {
		sut = new Track(trackDescriptor);
	}
	
	@Test
	public void distanceIsTheLengthOfOnlyPiece() {
		Piece onlyPiece = mock(Piece.class);
		Double distanceTo = 1.0;
		Double distanceFrom = 2.0;
		when(onlyPiece.getDistanceTo(any(Position.class))).thenReturn(distanceTo);
		when(onlyPiece.getDistanceFrom(any(Position.class))).thenReturn(distanceFrom);
		when(pieceFactory.create(any(noobbot.descriptor.GameInitDescriptor.Data.Race.Track.Piece.class))).thenReturn(onlyPiece);
		
		Double result = sut.getDistance(ignoredPosition, ignoredPosition);
		
		Double expectedDistance = distanceTo + distanceFrom;
		assertEquals(expectedDistance, result);
	}

}
