package noobbot.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Saulis on 17/04/14.
 */
public class CarBrainsTests {

    private CarBrains carBrains;
    private Position firstPosition;
    private Position secondPosition;
    private Track track;
    private Lane onlyLane;

    @Before
    public void setup() {
        track = mock(Track.class);
        onlyLane = mock(Lane.class);
        when(track.getLanes()).thenReturn(Arrays.asList(onlyLane));
        carBrains = new CarBrains(track);

        firstPosition = mock(Position.class);
        secondPosition = mock(Position.class);
    }

    @Test
    public void speedIsZeroIsOnNoPreviousPositions() {
        assertThat(carBrains.getCurrentSpeed(), is(0.0));
    }

    @Test
    public void speedIsZeroOnNoPreviousPosition() {
        carBrains.setPosition(firstPosition);

        assertThat(carBrains.getCurrentSpeed(), is(0.0));
    }

    @Test
    public void speedIsCalculatedOnSamePieces() {
        when(firstPosition.getPieceNumber()).thenReturn(0);
        when(firstPosition.getInPieceDistance()).thenReturn(10.0);

        when(secondPosition.getPieceNumber()).thenReturn(0);
        when(secondPosition.getInPieceDistance()).thenReturn(25.0);

        carBrains.setPosition(firstPosition);
        carBrains.setPosition(secondPosition);

        assertThat(carBrains.getCurrentSpeed(), is(15.0));
    }

    @Test
    public void speedIsCalculatedOnDifferentPieces() {
        //This test will be refactored after track has nicer methods.
        List<Piece> pieces = new ArrayList<>();
        Piece piece = mock(Piece.class);
        pieces.add(piece);
        pieces.add(piece);

        when(piece.getLength(track.getLanes().get(0))).thenReturn(100.0);

        when(track.getPieces()).thenReturn(pieces);

        when(firstPosition.getPieceNumber()).thenReturn(0);
        when(firstPosition.getInPieceDistance()).thenReturn(95.0);

        when(secondPosition.getPieceNumber()).thenReturn(1);
        when(secondPosition.getInPieceDistance()).thenReturn(10.0);

        carBrains.setPosition(firstPosition);
        carBrains.setPosition(secondPosition);

        assertThat(carBrains.getCurrentSpeed(), is(15.0));
    }
}
