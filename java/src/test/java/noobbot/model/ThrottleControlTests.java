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

    @Before
    public void setup() {
        metrics = mock(CarMetrics.class);
        throttleControl = new ThrottleControl(metrics);
    }

    @Test
    public void throttleAccelerates() {
        assertThat(throttleControl.getThrottle(2, 4), is(1.0));
    }

    @Test
    public void throttleBrakes() {
        assertThat(throttleControl.getThrottle(4, 2), is(0.0));
    }

    @Test
    public void throttleStabilizes() {
        when(metrics.getTopspeed()).thenReturn(10.0);

        //2.5 / 10.0 = 0.25 (targetspeed/topspeed)
        assertThat(throttleControl.getThrottle(2.4, 2.5), is(0.25));
    }
}
