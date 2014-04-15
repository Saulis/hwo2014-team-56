import com.google.gson.Gson;
import noobbot.CarPositions;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Saulis on 15/04/14.
 */
public class CarPositionsTest {

    Gson gson;
    CarPositions carPositions;

    @Before
    public void setUp()
    {
        gson = new Gson();
        carPositions = gson.fromJson("{\"msgType\": \"carPositions\", \"data\":[{\"id\":{\"name\":\"Trolo Rosso\", \"color\":\"red\"}, angle=3.013576824024717E-4, piecePosition={pieceIndex=38.0, inPieceDistance=80.13766432297933, lane={startLaneIndex=0.0, endLaneIndex=0.0}, lap=2.0}}]}", CarPositions.class);
    }

    @Test
    public void lapIsDeSerialized()
    {
        assertThat(carPositions.data[0].piecePosition.lap, is(2.0));
    }

    @Test
    public void endLaneIndexIsDeSerialized()
    {
        assertThat(carPositions.data[0].piecePosition.lane.endLaneIndex, is(0.0));
    }

    @Test
    public void startLaneIndexIsDeSerialized()
    {
        assertThat(carPositions.data[0].piecePosition.lane.startLaneIndex, is(0.0));
    }

    @Test
    public void inPieceDistanceIsDeSerialized()
    {
        assertThat(carPositions.data[0].piecePosition.inPieceDistance, is(80.13766432297933));
    }

    @Test
    public void messageTypeIsDeSerialized()
    {
        assertThat(carPositions.msgType, is("carPositions"));
    }

    @Test
    public void idNameIsDeSerialized()
    {
        assertThat(carPositions.data[0].id.name, is("Trolo Rosso"));
    }

    @Test
    public void angleIsDeSerialized()
    {
        assertThat(carPositions.data[0].angle, is(3.013576824024717E-4));
    }

    @Test
    public void pieceIndexIsDeSerialized()
    {
        assertThat(carPositions.data[0].piecePosition.pieceIndex, is(38.0));
    }
}
