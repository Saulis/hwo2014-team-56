package noobbot.model;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import noobbot.descriptor.CarPositionsDescriptor;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by Jere on 15.4.2014.
 */
public class PlayerPositionTests {

    private CarPositionsDescriptor.Data data;
    private PlayerPosition sut;
    private Track track = mock(Track.class); 

    @Before
    public void setUp() {
        CarPositionsDescriptor descriptor = new CarPositionsDescriptor();
        data = descriptor.new Data();
        when(track.getLanes()).thenReturn(new ArrayList<Lane>());
        sut = new PlayerPosition(track, data);
    }

    @Test
    public void slipAngleComesStraightFromData() {
        final double expectedAngle = 55.3;
        data.angle = expectedAngle;

        double result = sut.getSlipAngle();

        assertThat(result, is(expectedAngle));
    }
    
    @Test
    public void laneNumberIsTheStartLaneIndexInData() throws Exception {
        final int expectedLane = 3;
        data.piecePosition = data.new PiecePosition();
        data.piecePosition.lane = data.piecePosition.new Lane();
        data.piecePosition.lane.startLaneIndex = expectedLane;

        int result = sut.getLaneNumber();

        assertEquals(expectedLane, result);
    }
    
    @Test
    public void pieceNumberIsPiecePositionNumberInData() throws Exception {
        final int expectedPieceNumber = 4;
        data.piecePosition = data.new PiecePosition();
        data.piecePosition.lane = data.piecePosition.new Lane();
        data.piecePosition.pieceIndex = expectedPieceNumber;

        int result = sut.getPieceNumber();

        assertEquals(expectedPieceNumber, result);
    }
    
    @Test
    public void inPieceDistanceIsInPieceDistanceInData() throws Exception {
        final double expectedInPieceDistance = 12.3;
        data.piecePosition = data.new PiecePosition();
        data.piecePosition.lane = data.piecePosition.new Lane();
        data.piecePosition.inPieceDistance = expectedInPieceDistance;

        double result = sut.getInPieceDistance();

        assertEquals(expectedInPieceDistance, result, 0.0);
    }
}
