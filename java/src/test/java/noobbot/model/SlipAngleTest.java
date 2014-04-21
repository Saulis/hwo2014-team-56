package noobbot.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class SlipAngleTest {

    private SlipAngle sut;
    private Track track;
    
    @Before
    public void setup() {
        track = mockTrack();
        sut = new SlipAngle(track);
    }

    @Test
    public void accelerationIsDifferenceBetweenTwoLastAngleChangesWithEqualWeights() {
        sut.setAccelerationWeights(Arrays.asList(100));
        
        double firstAngle = 1.0;
        Position firstPosition = createPositionWithAngle(firstAngle);
        double secondAngle = 2.0;
        Position secondPosition = createPositionWithAngle(secondAngle);
        double thirdAngle = 5.0;
        Position thirdPosition = createPositionWithAngle(thirdAngle);
        
        sut.update(firstPosition);
        sut.update(secondPosition);
        sut.update(thirdPosition);
        double result = sut.getAcceleration();
        
        double firstChange = secondAngle - firstAngle;
        double secondChange = thirdAngle - secondAngle;
        double expectedAcceleration = secondChange - firstChange;
        assertEquals(expectedAcceleration, result, 0);
    }

    @Test
    public void accelerationIsCalculatedFromTwoLatestAccelerationWithProvidedWeights() {
        int latestWeight = 60;
        int previousWeight = 40;
        sut.setAccelerationWeights(Arrays.asList(latestWeight, previousWeight));
        
        double firstAngle = 1.0;
        Position firstPosition = createPositionWithAngle(firstAngle);
        double secondAngle = 2.0;
        Position secondPosition = createPositionWithAngle(secondAngle);
        double thirdAngle = 5.0;
        Position thirdPosition = createPositionWithAngle(thirdAngle);
        double fourthAngle = 20.0;
        Position fourthPosition = createPositionWithAngle(fourthAngle);
        
        sut.update(firstPosition);
        sut.update(secondPosition);
        sut.update(thirdPosition);
        sut.update(fourthPosition);
        double result = sut.getAcceleration();
        
        double firstChange = secondAngle - firstAngle;
        double secondChange = thirdAngle - secondAngle;
        double thirdChange = fourthAngle - thirdAngle;
        double weighterLatestAcceleration = (thirdChange - secondChange) * latestWeight;
        double weightedPreviousAcceleration = (secondChange - firstChange) * previousWeight;
        
        double expectedAcceleration = (weighterLatestAcceleration + weightedPreviousAcceleration) / 100;
        assertEquals(expectedAcceleration, result, 0);
    }

    private Position createPositionWithAngle(double firstAngle) {
        Position firstPosition = mock(Position.class);
        when(firstPosition.getSlipAngle()).thenReturn(firstAngle);
        return firstPosition;
    }

    private Track mockTrack() {
        Track track = mock(Track.class);
        when(track.getPiece(any(Position.class))).thenReturn(mock(Piece.class));
        return track;
    }
}
