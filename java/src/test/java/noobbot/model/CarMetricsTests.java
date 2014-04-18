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

    private void update(Position position, double throttle) {
        carMetrics.update(new Metric(position, throttle));
    }

    @Test
    public void speedIsZeroIsOnNoPreviousPositions() {
        assertThat(carMetrics.getCurrentSpeed(), is(0.0));
    }

    @Test
    public void speedIsZeroOnNoPreviousPosition() {
        update(firstPosition, currentThrottle);

        assertThat(carMetrics.getCurrentSpeed(), is(0.0));
    }

    @Test
    public void speedIsCalculatedOnSamePieces() {
        update(firstPosition, currentThrottle);
        update(secondPosition, currentThrottle);

        assertThat(carMetrics.getCurrentSpeed(), is(15.0));
    }

    @Test
    public void speedIsCalculatedOnDifferentPieces() {
        update(secondPosition, currentThrottle);
        update(thirdPosition, currentThrottle);

        assertThat(carMetrics.getCurrentSpeed(), is(80.0));
    }

    @Test
    public void accelerationIsCalculated() {
        update(firstPosition, currentThrottle);
        update(secondPosition, currentThrottle);
        update(thirdPosition, currentThrottle);

        assertThat(carMetrics.getCurrentAcceleration(), is(80.0 - 15.0));
    }

    @Test
    public void accelerationIsMeasuredCalculated() {
        updateForAccelerationMeasuring();

        //Speed: 0, 10-0=10, 25-10=15, Acceleration: 10-0=10, 15-10=5, Ratio: 5/10=0.5
        //Topspeed: First acc with full throttle / ratio -> 10.0/0.5 = 20
        assertThat(carMetrics.getAccelerationRatio(), is(0.5));
        assertThat(carMetrics.getMaxAcceleration(), is(10.0));
        assertThat(carMetrics.getTopspeed(), is(20.0));
    }

    private void updateForAccelerationMeasuring() {
        update(startingPosition, 1.0);
        update(firstPosition, 1.0);
        update(secondPosition, 1.0);
    }

    @Test
    public void topSpeedHasFallbackValue() {
        assertThat(carMetrics.getTopspeed(), is(10.0)); //test track top speed
    }

    @Test
    public void accelerationRatioHasFallbackValue() {
        assertThat(carMetrics.getAccelerationRatio(), is(0.02)); //test track acceleration ratio
    }

    @Test
    public void maxAccelerationHasFallbackValue() {
        assertThat(carMetrics.getMaxAcceleration(), is(0.2)); //test track max acceleration
    }

    @Test
    public void nextAccelerationIsEstimated() {
        updateForAccelerationMeasuring();

        //Max accel 10, accel ratio 0.5, current speed 2.0, top speed 20.0, hence 20.0 - 2.0 = 18.0, 18.0 * 0.5 = 9.0
        assertThat(carMetrics.getNextAcceleration(2.0, 1.0), is(9.0));
    }

    @Test
    public void nextSpeedIsEstimated() {
        assertThat(carMetrics.getSpeed(2.0, 9.0), is(11.0));
    }
}
