package noobbot.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class SlipAngleEstimateTest {

    private SlipAngleEstimate sut;
    private double currentAngle = 20.0;
    
    @Before
    public void setup() {
        List<Double> accelerations = new ArrayList<Double>();
        sut = new SlipAngleEstimate(currentAngle, accelerations);
    }
    
    @Test
    public void accelerationsDecreasingTowardsZero() {
        
        fail("Not yet implemented");
    }

}
