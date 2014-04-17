package noobbot.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

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
    private Position thirdPosition;
    private Piece piece;

    @Before
    public void setup() {
        track = mock(Track.class);
        onlyLane = mock(Lane.class);
        when(track.getLanes()).thenReturn(Arrays.asList(onlyLane));
        carMetrics = new CarMetrics(track);

        piece = mock(Piece.class);
        when(piece.getLength(onlyLane)).thenReturn(100.0);

        firstPosition = createPosition(0, 10.0);
        secondPosition = createPosition(0, 25.0);
        thirdPosition = createPosition(1, 5.0);
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
        carMetrics.setPosition(firstPosition);
        carMetrics.setPosition(secondPosition);

        assertThat(carMetrics.getCurrentSpeed(), is(15.0));
    }

    @Test
    public void speedIsCalculatedOnDifferentPieces() {
        carMetrics.setPosition(secondPosition);
        carMetrics.setPosition(thirdPosition);

        assertThat(carMetrics.getCurrentSpeed(), is(80.0));
    }

    private Position createPosition(int pieceNumber, double inDistance) {
        Position position = mock(Position.class);

        when(position.getLane()).thenReturn(onlyLane);
        when(position.getPieceNumber()).thenReturn(pieceNumber);
        when(position.getInPieceDistance()).thenReturn(inDistance);

        when(track.getPiece(position)).thenReturn(piece);

        return position;
    }
}
