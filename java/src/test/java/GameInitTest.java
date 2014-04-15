import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import noobbot.GameInit;

import org.junit.Test;

import com.google.gson.Gson;

/**
 * Created by Saulis on 15/04/14.
 */
public class GameInitTest {

    private GameInit gameInit;

	@Test
    public void deSerializeData() {
        Gson gson = new Gson();
        gameInit = gson.fromJson("{\"msgType\": \"gameInit\", \"data\": {  \"race\": {    \"track\": {      \"id\": \"indianapolis\",      \"name\": \"Indianapolis\",      \"pieces\": [        {          \"length\": 100.0        },        {          \"length\": 100.0,          \"switch\": true        },        {          \"radius\": 200,          \"angle\": 22.5        }      ],      \"lanes\": [        {          \"distanceFromCenter\": -20,          \"index\": 0        },        {          \"distanceFromCenter\": 0,          \"index\": 1        },        {          \"distanceFromCenter\": 20,          \"index\": 2        }      ],      \"startingPoint\": {        \"position\": {          \"x\": -340.0,          \"y\": -96.0        },        \"angle\": 90.0      }    },    \"cars\": [      {        \"id\": {          \"name\": \"Schumacher\",          \"color\": \"red\"        },        \"dimensions\": {          \"length\": 40.0,          \"width\": 20.0,          \"guideFlagPosition\": 10.0        }      },      {        \"id\": {          \"name\": \"Rosberg\",          \"color\": \"blue\"        },        \"dimensions\": {          \"length\": 40.0,          \"width\": 20.0,          \"guideFlagPosition\": 10.0        }      }    ],    \"raceSession\": {      \"laps\": 3,      \"maxLapTimeMs\": 30000,      \"quickRace\": true    }  }}}", GameInit.class);

        assertThat(gameInit.msgType, is("gameInit"));

        assertThat(gameInit.data.race.track.id, is("indianapolis"));
        assertThat(gameInit.data.race.track.lanes.length, is(3));
        assertLaneDistance(0, -20.0);
        assertLaneDistance(1, 0.0);
        assertLaneDistance(2, 20.0);

        assertThat(gameInit.data.race.track.startingPoint.position.x, is(-340.0));
        assertThat(gameInit.data.race.track.startingPoint.position.y, is(-96.0));
        assertThat(gameInit.data.race.track.startingPoint.angle, is(90.0));

        assertCarDimensions(0, 20.0, 40.0, 10.0);
        assertCarDimensions(1, 20.0, 40.0, 10.0);
        
		assertCarId(0, "red", "Schumacher");
		assertCarId(1, "blue", "Rosberg");

        assertThat(gameInit.data.race.raceSession.laps, is(3));
        assertThat(gameInit.data.race.raceSession.maxLapTimeMs, is(30000));
        assertThat(gameInit.data.race.raceSession.quickRace, is(true));
	}

	private void assertCarId(int carNumber, String color, String name) {
		assertThat(gameInit.data.race.cars[carNumber].id.color, is(color));
		assertThat(gameInit.data.race.cars[carNumber].id.name, is(name));
	}

	private void assertCarDimensions(int carNumber, double width, double length, double flagPosition) {
		assertThat(gameInit.data.race.cars[carNumber].dimensions.width, is(width));
        assertThat(gameInit.data.race.cars[carNumber].dimensions.length, is(length));
        assertThat(gameInit.data.race.cars[carNumber].dimensions.guideFlagPosition, is(flagPosition));
	}
	
	private void assertLaneDistance(int lane, double distance) {
		assertThat(gameInit.data.race.track.lanes[lane].distanceFromCenter, is(distance));
	}
}
