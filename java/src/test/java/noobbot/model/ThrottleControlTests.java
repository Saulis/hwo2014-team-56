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

    @Before
    public void setup() {
        metrics = mock(CarMetrics.class);
        throttleControl = new ThrottleControl(metrics);
        targetSpeed = mock(TargetSpeed.class);
    }

    @Test
    public void throttleAccelerates() {
        when(targetSpeed.getTargetSpeed()).thenReturn(4.0);

        assertThat(throttleControl.getThrottle(0.666, 0.0, 2, targetSpeed), is(1.0));
    }

    @Test
    public void throttleBrakes() {
        when(targetSpeed.getTargetSpeed()).thenReturn(2.0);

        assertThat(throttleControl.getThrottle(0.666, 0.0, 4, targetSpeed), is(0.0));
    }

    @Test
    public void throttleStabilizes() {
        when(metrics.getTopspeed()).thenReturn(10.0);
        when(targetSpeed.getTargetSpeed()).thenReturn(2.5);

        //2.5 / 10.0 = 0.25 (targetspeed/topspeed)
        assertThat(throttleControl.getThrottle(0.666, 0.0, 2.4, targetSpeed), is(0.25));
    }
}
