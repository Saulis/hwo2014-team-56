package noobbot.model;

import noobbot.descriptor.CarPositionsDescriptor;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by Jere on 15.4.2014.
 */
public class PlayerPositionTests {

    CarPositionsDescriptor.Data data;
    PlayerPosition sut;

    @Before
    public void setUp() {
        CarPositionsDescriptor descriptor = new CarPositionsDescriptor();
        data = descriptor.new Data();
        sut = new PlayerPosition(data);
    }

    @Test
    public void slipAngleComesStraightFromData() {
        final double expectedAngle = 55.3;
        data.angle = expectedAngle;

        double result = sut.getSlipAngle();

        assertThat(result, is(expectedAngle));
    }
}
