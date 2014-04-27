package noobbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import noobbot.descriptor.CarPositionsDescriptor;
import noobbot.descriptor.GameInitDescriptor;
import noobbot.descriptor.TurboAvailableDescriptor;

import com.google.gson.Gson;

import noobbot.descriptor.YourCarDescriptor;
import noobbot.model.*;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class Main {
    private String carColor;
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
            int carCount1 = Integer.parseInt(carCount);
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
    private ThrottleControl throttleControl;

    public Main(final BufferedReader reader, final PrintWriter writer, SendMsg... msgs) throws IOException {
        this.writer = writer;
        String line = null;

        for(SendMsg msg : msgs) {
            send(msg);
        }

        Car player = null;

        while ((line = reader.readLine()) != null) {
            final MsgWrapper msgFromServer = gson.fromJson(line, MsgWrapper.class);
            //System.out.println(line);
            if(msgFromServer.msgType.equals("crash")) {
                System.out.println(line);
            }

            if (msgFromServer.msgType.equals("turboAvailable")) {
                TurboAvailableDescriptor descriptor = gson.fromJson(line, TurboAvailableDescriptor.class);
                turboCharger.setTurboAvailable(new Turbo(descriptor));
            }

            if (msgFromServer.msgType.equals("carPositions")) {
                CarPositionsDescriptor carPositions = gson.fromJson(line, CarPositionsDescriptor.class);
                PlayerPosition position = getPlayerPosition(carPositions);

                navigator.setPosition(position);
                double nextThrottle = player.setPosition(position);
                if(navigator.shouldSendSwitchLanes()) {
                    send(navigator.setTargetLane());
                 } else if (turboCharger.shouldSendTurbo()) {
                     navigator.useTurbo(turboCharger.useTurbo());
                     send(new TurboMsg());
                } else {


                send(new ThrottleMsg(nextThrottle));

                //System.out.println("");
            }

            } else {
                if (msgFromServer.msgType.equals("join")) {
                    System.out.println("Joined");
                } else if (msgFromServer.msgType.equals("gameInit")) {
                    GameInitDescriptor gameInit = gson.fromJson(line, GameInitDescriptor.class);
                    TargetAngleSpeed tas = new TargetAngleSpeed();

                    List<Piece> pieces = getPieces(gameInit, tas);
                    List<Lane> lanes = getLanes(gameInit);
                    track = new Track(pieces, lanes);
                    navigator = new Navigator(track);
                    navigator.useHighestRankingRoute();
                    turboCharger = new TurboCharger(navigator);
                    CarMetrics carMetrics = new CarMetrics(track, navigator, tas);
                    throttleControl = new ThrottleControl(carMetrics);

                    player = new Car(carMetrics, navigator, throttleControl);

                    System.out.println("Race init");
                    System.out.println(line);
                } else if (msgFromServer.msgType.equals("gameEnd")) {
                    System.out.println("Race end");
                } else if (msgFromServer.msgType.equals("gameStart")) {
                    System.out.println("Race start");
                } else if(msgFromServer.msgType.equals("yourCar")) {
                    YourCarDescriptor yourCarDescriptor = gson.fromJson(line, YourCarDescriptor.class);

                    carColor = yourCarDescriptor.data.color;
                }

                send(new Ping());
            }
        }
    }

    private PlayerPosition getPlayerPosition(CarPositionsDescriptor carPositions) {
        Optional<CarPositionsDescriptor.Data> data = stream(carPositions.data).filter(d -> d.id.color.equals(carColor)).findFirst();
        return new PlayerPosition(track, data.get());
    }

    public static List<Lane> getLanes(GameInitDescriptor gameInit) {
        GameInitDescriptor.Data.Race.Track.Lane[] lanes = gameInit.data.race.track.lanes;

        double laneWidth = getLaneWidth(lanes);

        return stream(lanes).map(l -> new LaneImpl(l.index, l.distanceFromCenter, laneWidth)).collect(toList());
    }

    private static double getLaneWidth(GameInitDescriptor.Data.Race.Track.Lane[] lanes) {
        if (lanes.length >= 2) {
            return Math.abs(lanes[0].distanceFromCenter - lanes[1].distanceFromCenter);
        }
        return 0.0;
    }

    public static List<Piece> getPieces(GameInitDescriptor gameInit, TargetAngleSpeed tas) {
        PieceFactory pieceFactory = new PieceFactory(tas);

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

class ThrottleMsg extends SendMsg {
    private double value;

    public ThrottleMsg(double value) {
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

class TurboMsg extends SendMsg {
    @Override
    protected String msgType() {
        return "turbo";
    }

    @Override
    protected Object msgData() {
        return "Sierra jättää.";
    }
}
