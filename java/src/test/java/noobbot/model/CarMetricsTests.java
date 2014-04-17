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
public class CarMetricsTests {

    private CarMetrics carMetrics;
    private Position firstPosition;
    private Position secondPosition;
    private Track track;
    private Lane onlyLane;

    @Before
    public void setup() {
        track = mock(Track.class);
        onlyLane = mock(Lane.class);
        when(track.getLanes()).thenReturn(Arrays.asList(onlyLane));
        carMetrics = new CarMetrics(track);

        firstPosition = mock(Position.class);
        secondPosition = mock(Position.class);
    }

    @Test
    public void speedIsZeroIsOnNoPreviousPositions() {
        assertThat(carMetrics.getCurrentSpeed(), is(0.0));
    }

    @Test
    public void speedIsZeroOnNoPreviousPosition() {
        carMetrics.setPosition(firstPosition);

        assertThat(carMetrics.getCurrentSpeed(), is(0.0));
    }

    @Test
    public void speedIsCalculatedOnSamePieces() {
        when(firstPosition.getPieceNumber()).thenReturn(0);
        when(firstPosition.getInPieceDistance()).thenReturn(10.0);

        when(secondPosition.getPieceNumber()).thenReturn(0);
        when(secondPosition.getInPieceDistance()).thenReturn(25.0);

        carMetrics.setPosition(firstPosition);
        carMetrics.setPosition(secondPosition);

        assertThat(carMetrics.getCurrentSpeed(), is(15.0));
    }

    @Test
    public void speedIsCalculatedOnDifferentPieces() {
        Piece piece = mock(Piece.class);
        when(piece.getLength(track.getLanes().get(0))).thenReturn(100.0);
        when(track.getPiece(firstPosition)).thenReturn(piece);

        when(firstPosition.getPieceNumber()).thenReturn(0);
        when(firstPosition.getInPieceDistance()).thenReturn(95.0);

        when(secondPosition.getPieceNumber()).thenReturn(1);
        when(secondPosition.getInPieceDistance()).thenReturn(10.0);

        carMetrics.setPosition(firstPosition);
        carMetrics.setPosition(secondPosition);

        assertThat(carMetrics.getCurrentSpeed(), is(15.0));
    }
}
