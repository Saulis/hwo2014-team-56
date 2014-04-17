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

    private double currentThrottle = 1.0;
    private CarMetrics carMetrics;
    private Position firstPosition;
    private Position secondPosition;
    private Track track;
    private Lane onlyLane;
    private Position thirdPosition;
    private Piece piece;
    private Position startingPosition;

    @Before
    public void setup() {
        track = mock(Track.class);
        onlyLane = mock(Lane.class);
        when(track.getLanes()).thenReturn(Arrays.asList(onlyLane));
        carMetrics = new CarMetrics(track);

        piece = mock(Piece.class);
        when(piece.getLength(onlyLane)).thenReturn(100.0);

        startingPosition = createPosition(0, 0);
        firstPosition = createPosition(0, 10.0);
        secondPosition = createPosition(0, 25.0);
        thirdPosition = createPosition(1, 5.0);
    }

    private Position createPosition(int pieceNumber, double inDistance) {
        Position position = mock(Position.class);

        when(position.getLane()).thenReturn(onlyLane);
        when(position.getPieceNumber()).thenReturn(pieceNumber);
        when(position.getInPieceDistance()).thenReturn(inDistance);

        when(track.getPiece(position)).thenReturn(piece);

        return position;
    }

    @Test
    public void speedIsZeroIsOnNoPreviousPositions() {
        assertThat(carMetrics.getCurrentSpeed(), is(0.0));
    }

    @Test
    public void speedIsZeroOnNoPreviousPosition() {
        carMetrics.update(new Metric(firstPosition, currentThrottle));

        assertThat(carMetrics.getCurrentSpeed(), is(0.0));
    }

    @Test
    public void speedIsCalculatedOnSamePieces() {
        carMetrics.update(new Metric(firstPosition, currentThrottle));
        carMetrics.update(new Metric(secondPosition, currentThrottle));

        assertThat(carMetrics.getCurrentSpeed(), is(15.0));
    }

    @Test
    public void speedIsCalculatedOnDifferentPieces() {
        carMetrics.update(new Metric(secondPosition, currentThrottle));
        carMetrics.update(new Metric(thirdPosition, currentThrottle));

        assertThat(carMetrics.getCurrentSpeed(), is(80.0));
    }

    @Test
    public void accelerationIsCalculated() {
        carMetrics.update(new Metric(firstPosition, currentThrottle));
        carMetrics.update(new Metric(secondPosition, currentThrottle));
        carMetrics.update(new Metric(thirdPosition, currentThrottle));

        assertThat(carMetrics.getCurrentAcceleration(), is(80.0 - 15.0));
    }

    @Test
    public void topSpeedIsCalculated() {
        update(startingPosition, 1.0);
        update(firstPosition, 1.0);
        update(secondPosition, 1.0);

        //Speed: 0, 10-0=10, 25-10=15, Acceleration: 10-0=10, 15-10=5, Ratio: 5/10=0.5
        //Topspeed: First acc with full throttle / ratio -> 10.0/0.5 = 20
        assertThat(carMetrics.getTopspeed(), is(20.0));
    }

    private void update(Position position, double throttle) {
        carMetrics.update(new Metric(position, throttle));
    }
}
