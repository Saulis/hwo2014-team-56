package noobbot;

import com.google.gson.Gson;

import noobbot.descriptor.GameInitDescriptor;
import noobbot.model.Lane;
import noobbot.model.Piece;
import noobbot.model.TargetAngleSpeed;
import noobbot.model.Track;

import java.util.List;

/**
 * Created by Saulis on 21/04/14.
 */
public class Keimola {
    private static String data = "{\"msgType\":\"gameInit\",\"data\":{\"race\":{\"track\":{\"id\":\"keimola\",\"name\":\"Keimola\",\"pieces\":[{\"length\":100.0},{\"length\":100.0},{\"length\":100.0},{\"length\":100.0,\"switch\":true},{\"radius\":100,\"angle\":45.0},{\"radius\":100,\"angle\":45.0},{\"radius\":100,\"angle\":45.0},{\"radius\":100,\"angle\":45.0},{\"radius\":200,\"angle\":22.5,\"switch\":true},{\"length\":100.0},{\"length\":100.0},{\"radius\":200,\"angle\":-22.5},{\"length\":100.0},{\"length\":100.0,\"switch\":true},{\"radius\":100,\"angle\":-45.0},{\"radius\":100,\"angle\":-45.0},{\"radius\":100,\"angle\":-45.0},{\"radius\":100,\"angle\":-45.0},{\"length\":100.0,\"switch\":true},{\"radius\":100,\"angle\":45.0},{\"radius\":100,\"angle\":45.0},{\"radius\":100,\"angle\":45.0},{\"radius\":100,\"angle\":45.0},{\"radius\":200,\"angle\":22.5},{\"radius\":200,\"angle\":-22.5},{\"length\":100.0,\"switch\":true},{\"radius\":100,\"angle\":45.0},{\"radius\":100,\"angle\":45.0},{\"length\":62.0},{\"radius\":100,\"angle\":-45.0,\"switch\":true},{\"radius\":100,\"angle\":-45.0},{\"radius\":100,\"angle\":45.0},{\"radius\":100,\"angle\":45.0},{\"radius\":100,\"angle\":45.0},{\"radius\":100,\"angle\":45.0},{\"length\":100.0,\"switch\":true},{\"length\":100.0},{\"length\":100.0},{\"length\":100.0},{\"length\":90.0}],\"lanes\":[{\"distanceFromCenter\":-10,\"index\":0},{\"distanceFromCenter\":10,\"index\":1}],\"startingPoint\":{\"position\":{\"x\":-300.0,\"y\":-44.0},\"angle\":90.0}},\"cars\":[{\"id\":{\"name\":\"Trolo Rosso\",\"color\":\"red\"},\"dimensions\":{\"length\":40.0,\"width\":20.0,\"guideFlagPosition\":10.0}}],\"raceSession\":{\"laps\":3,\"maxLapTimeMs\":60000,\"quickRace\":true}}},\"gameId\":\"e0b0ae81-fe2b-4450-9fb6-a09b73797ab0\"}";

    public static Track getTrack(TargetAngleSpeed targetAngleSpeed) {
        Gson gson = new Gson();
        GameInitDescriptor gameInit = gson.fromJson(data, GameInitDescriptor.class);
        List<Piece> pieces = Main.getPieces(gameInit, targetAngleSpeed);
        List<Lane> lanes = Main.getLanes(gameInit);

        return new Track(pieces, lanes);
    }
}
