package noobbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import noobbot.descriptor.CarPositionsDescriptor;
import noobbot.descriptor.GameInitDescriptor;

import com.google.gson.Gson;
import noobbot.model.*;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class Main {
    private TurboCharger turboCharger;
    private Navigator navigator;
    private Track track;

    public static void main(String... args) throws IOException {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String botName = args[2];
        String botKey = args[3];

        System.out.println("Connecting to " + host + ":" + port + " as " + botName + "/" + botKey);

        final Socket socket = new Socket(host, port);
        final PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));

        final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));

        if(args.length == 7) {
            String command = args[4];
            String trackName = args[5];
            String carCount = args[6];
            int carCount1 = Integer.getInteger(carCount, 3).intValue();
            if(command.equals("create")) {
                new Main(reader, writer, new CreateRace(botName, botKey, trackName, carCount1));//, new JoinRace(botName, botKey, trackName, carCount1));
            } else if(command.equals("join")) {
                new Main(reader, writer, new JoinRace(botName, botKey, trackName, carCount1));
            }
        } else {
            new Main(reader, writer, new Join(botName, botKey));
        }
    }

    final Gson gson = new Gson();
    private PrintWriter writer;

    public Main(final BufferedReader reader, final PrintWriter writer, SendMsg... msgs) throws IOException {
        this.writer = writer;
        String line = null;

        for(SendMsg msg : msgs) {
            send(msg);
        }

        Car player = null;

        while((line = reader.readLine()) != null) {
            final MsgWrapper msgFromServer = gson.fromJson(line, MsgWrapper.class);
            System.out.println(line);
            if(msgFromServer.msgType.equals("crash")) {
                System.out.println(line);
            }

            if (msgFromServer.msgType.equals("turboAvailable")) {
                turboCharger.addTurbo();
            }

            if (msgFromServer.msgType.equals("carPositions")) {
                CarPositionsDescriptor carPositions = gson.fromJson(line, CarPositionsDescriptor.class);
                PlayerPosition position = new PlayerPosition(track, carPositions.data[0]);

                navigator.setPosition(position);
                double nextThrottle = player.setPosition(position);
                if(navigator.shouldSendSwitchLanes()) {
                    send(navigator.setTargetLane());
                } else if(turboCharger.shouldSendTurbo()) {
                    turboCharger.useTurbo();
                    send(new Turbo());
                } else {


                send(new Throttle(nextThrottle));

                //System.out.println("");
            }

            } else if (msgFromServer.msgType.equals("join")) {
                System.out.println("Joined");
            } else if (msgFromServer.msgType.equals("gameInit")) {
                GameInitDescriptor gameInit = gson.fromJson(line, GameInitDescriptor.class);
                List<Piece> pieces = getPieces(gameInit);
                List<Lane> lanes = getLanes(gameInit);
                track = new Track(pieces, lanes);
                navigator = new Navigator(track);
                CarMetrics carMetrics = new CarMetrics(track);
                navigator.useHighestRankingRoute();
                turboCharger = new TurboCharger(navigator);
                player = new Car(carMetrics, navigator);

                System.out.println("Race init");
                System.out.println(line);
            } else if (msgFromServer.msgType.equals("gameEnd")) {
                System.out.println("Race end");
            } else if (msgFromServer.msgType.equals("gameStart")) {
                System.out.println("Race start");
            } else {
                send(new Ping());
            }
        }
    }

    public static List<Lane> getLanes(GameInitDescriptor gameInit) {
        GameInitDescriptor.Data.Race.Track.Lane[] lanes = gameInit.data.race.track.lanes;

        double laneWidth = getLaneWidth(lanes);

        return stream(lanes).map(l -> new LaneImpl(l.index, l.distanceFromCenter, laneWidth)).collect(toList());
    }

    private static double getLaneWidth(GameInitDescriptor.Data.Race.Track.Lane[] lanes) {
        if(lanes.length >= 2) {
           return Math.abs(lanes[0].distanceFromCenter - lanes[1].distanceFromCenter);
        }
        return 0.0;
    }

    public static List<Piece> getPieces(GameInitDescriptor gameInit) {
        PieceFactory pieceFactory = new PieceFactory();

        AtomicInteger index = new AtomicInteger();
        return stream(gameInit.data.race.track.pieces).map(p -> pieceFactory.create(p, index.getAndIncrement())).collect(toList());
    }

    private void send(final SendMsg msg) {
        writer.println(msg.toJson());
        writer.flush();
    }
}

abstract class SendMsg {
    public String toJson() {
        return new Gson().toJson(new MsgWrapper(this));
    }

    protected Object msgData() {
        return this;
    }

    protected abstract String msgType();
}

class MsgWrapper {
    public final String msgType;
    public final Object data;

    MsgWrapper(final String msgType, final Object data) {
        this.msgType = msgType;
        this.data = data;
    }

    public MsgWrapper(final SendMsg sendMsg) {
        this(sendMsg.msgType(), sendMsg.msgData());
    }
}

class CreateRace extends SendMsg {

    class BotId {
        public String name;
        public String key;

        public BotId(String name, String key) {

            this.name = name;
            this.key = key;
        }
    }

    public BotId botId;
    public String trackName;
    public String password = "nicenice";
    public int carCount;

    CreateRace(String name, String key, String trackName, int carCount) {
        this.trackName = trackName;
        this.carCount = carCount;
        botId = new BotId(name, key);
    }

    @Override
    protected String msgType() {
        return "createRace";
    }
}

class JoinRace extends SendMsg {

    class BotId {
        public String name;
        public String key;

        public BotId(String name, String key) {

            this.name = name;
            this.key = key;
        }
    }

    public BotId botId;
    public String trackName;
    public String password = "nicenice";
    public int carCount;

    JoinRace(String name, String key, String trackName, int carCount) {
        this.trackName = trackName;
        this.carCount = carCount;
        Random random = new Random();
        botId = new BotId(name + random.nextInt(10), key);
    }

    @Override
    protected String msgType() {
        return "joinRace";
    }
}

class Join extends SendMsg {
    public final String name;
    public final String key;

    Join(final String name, final String key) {
        this.name = name;
        this.key = key;
    }

    @Override
    protected String msgType() {
        return "join";
    }
}

class Ping extends SendMsg {
    @Override
    protected String msgType() {
        return "ping";
    }
}

class Throttle extends SendMsg {
    private double value;

    public Throttle(double value) {
        this.value = value;
    }

    @Override
    protected Object msgData() {
        return value;
    }

    @Override
    protected String msgType() {
        return "throttle";
    }
}

class Turbo extends SendMsg {
    @Override
    protected String msgType() { return "turbo";}

    @Override
    protected Object msgData() { return "Sierra jättää.";}
}

