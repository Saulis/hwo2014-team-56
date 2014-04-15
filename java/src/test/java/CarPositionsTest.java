import com.google.gson.Gson;
import noobbot.CarPositions;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Saulis on 15/04/14.
 */
public class CarPositionsTest {

    @Test
    public void deSerializeData() {
        Gson gson = new Gson();
        CarPositions carPositions = gson.fromJson("{\"msgType\": \"carPositions\", \"data\":[{\"id\":{\"name\":\"Trolo Rosso\", \"color\":\"red\"}, angle=3.013576824024717E-4, piecePosition={pieceIndex=38.0, inPieceDistance=80.13766432297933, lane={startLaneIndex=0.0, endLaneIndex=0.0}, lap=2.0}}]}", CarPositions.class);

        assertThat(carPositions.msgType, is("carPositions"));

        assertThat(carPositions.data[0].id.name, is("Trolo Rosso"));
        assertThat(carPositions.data[0].angle, is(3.013576824024717E-4));

        assertThat(carPositions.data[0].piecePosition.pieceIndex, is(38.0));
        assertThat(carPositions.data[0].piecePosition.inPieceDistance, is(80.13766432297933));
        assertThat(carPositions.data[0].piecePosition.lane.startLaneIndex, is(0.0));
        assertThat(carPositions.data[0].piecePosition.lane.endLaneIndex, is(0.0));
        assertThat(carPositions.data[0].piecePosition.lap, is(2.0));

    }
}
