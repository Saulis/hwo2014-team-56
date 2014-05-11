package noobbot.model;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Saulis on 18/04/14.
 */
public class ThrottleControlTests {

    private ThrottleControl throttleControl;
    private CarMetrics metrics;
    private TargetSpeed targetSpeed;
    private SlipAngle slipAngle;

    @Before
    public void setup() {
        metrics = mock(CarMetrics.class);
        throttleControl = new ThrottleControl(metrics);
        targetSpeed = mock(TargetSpeed.class);
        slipAngle = mock(SlipAngle.class);
        when(metrics.getSlipAngle()).thenReturn(slipAngle);
    }

    @Test
    public void throttleAccelerates() {
        when(targetSpeed.getDistanceToTarget()).thenReturn(4.0);
        currentPieceIsStraight();

        assertThat(throttleControl.getThrottle(0.666, targetSpeed, targetSpeed), is(1.0));
    }

    @Test
    public void throttleBrakes() {
        when(targetSpeed.getTargetSpeed()).thenReturn(2.0);

        assertThat(throttleControl.getThrottle(0.666, targetSpeed, targetSpeed), is(0.0));
    }

    @Test
    public void throttleStabilizes() {
        when(metrics.getTopspeed()).thenReturn(10.0);
        when(targetSpeed.getDistanceToTarget()).thenReturn(2.5);
        currentPieceIsAngled();
        
        //2.5 / 10.0 = 0.25 (targetspeed/topspeed)
        assertThat(throttleControl.getThrottle(0.666, targetSpeed, targetSpeed), is(0.0666));
    }

    private void currentPieceIsStraight() {
        Piece straightPiece = mockStraightPiece();
        currentPieceIs(straightPiece);
    }

    private void currentPieceIsAngled() {
        Piece angledPiece = mockAngledPiece();
        currentPieceIs(angledPiece);
    }
    
    private void currentPieceIs(Piece straightPiece) {
        when(metrics.getCurrentPiece()).thenReturn(straightPiece);
    }

    private Piece mockStraightPiece() {
        Piece straightPiece = mock(Piece.class);
        when(straightPiece.getAngle()).thenReturn(0.0);
        return straightPiece;
    }

    private Piece mockAngledPiece() {
        Piece angledPiece = mock(Piece.class);
        when(angledPiece.getAngle()).thenReturn(45.0);
        return angledPiece;
    }
}
