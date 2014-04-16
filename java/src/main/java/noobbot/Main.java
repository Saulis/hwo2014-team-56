package noobbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import noobbot.descriptor.CarPositionsDescriptor;
import noobbot.descriptor.GameInitDescriptor;

import com.google.gson.Gson;
import noobbot.model.PlayerPosition;
import noobbot.model.Car;
import noobbot.model.Position;

public class Main {
    public static void main(String... args) throws IOException {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String botName = args[2];
        String botKey = args[3];

        System.out.println("Connecting to " + host + ":" + port + " as " + botName + "/" + botKey);

        final Socket socket = new Socket(host, port);
        final PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));

        final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));

        new Main(reader, writer, new Join(botName, botKey));
    }

    final Gson gson = new Gson();
    private PrintWriter writer;

    public Main(final BufferedReader reader, final PrintWriter writer, final Join join) throws IOException {
        this.writer = writer;
        String line = null;

        send(join);
        double nextThrottle = 0.6;
        GameInitDescriptor gameInit = null;
        PlayerPosition previousPositions = null;
        double previousSpeed = 0;
        double previousThrottle = 0;

        double accelerationMagicNumber = 0.98; //This will be measured real time
        double topspeed = 10; //This will be calculated from acceleration magic number

        Car player = new Car();

        while((line = reader.readLine()) != null) {
            final MsgWrapper msgFromServer = gson.fromJson(line, MsgWrapper.class);
            //System.out.println(line);
            if(msgFromServer.msgType.equals("crash")) {
                System.out.println(line);
            }

            if (msgFromServer.msgType.equals("carPositions")) {
                CarPositionsDescriptor carPositions = gson.fromJson(line, CarPositionsDescriptor.class);
                PlayerPosition position = new PlayerPosition(carPositions.data[0]);
                player.setPosition(position);

                double slipAngle = position.getSlipAngle();
                double trackAngle = getTrackAngle(gameInit, player.getPosition());
                double nextTrackAngle = getNextTrackAngle(gameInit, player.getPosition());
                double speed = player.getSpeed(gameInit.data.race.track);
                double acceleration = speed - previousSpeed;

                //Trying out setting target speed roughly according to angle.. 45 degrees -> 50% of top speed
                double targetSpeed = topspeed * ((90 - Math.abs(nextTrackAngle)) / 90) * 1.2; //magic magic + 20% boost

                double speedDiff = targetSpeed - speed;

                if(speedDiff > 0.5) {
                    nextThrottle = 1;
                } else if(speedDiff < -0.5) {
                    nextThrottle = 0;
                }
                else {
                    nextThrottle = targetSpeed / topspeed;
                }


                //Acceleration estimation testing here...
                double estimatedAcceleration = (previousThrottle * topspeed - speed) * (1 - accelerationMagicNumber);

                //If we can estimate deceleration rate we can then calculate the distance required to decelerate to target speed.
                //With the braking distance we can then start braking at the last possible moment.


                //System.out.println(String.format("Piece: %s, Length: %s, Position: %s,  Angle: %s->%s, Throttle: %s->%s, Slip: %s, Speed: %s (%s), Acc: %s (%s)", player.getPosition().getPiecePosition().pieceIndex, pieceLength, player.getPosition().getPiecePosition().inPieceDistance, trackAngle, nextTrackAngle, previousThrottle, nextThrottle, slipAngle, speed, targetSpeed, acceleration, estimatedAcceleration));

                send(new Throttle(nextThrottle));

                previousSpeed = speed;
                previousThrottle = nextThrottle;

            } else if (msgFromServer.msgType.equals("join")) {
                System.out.println("Joined");
            } else if (msgFromServer.msgType.equals("gameInit")) {
                gameInit = gson.fromJson(line, GameInitDescriptor.class);
                System.out.println("Race init");
            } else if (msgFromServer.msgType.equals("gameEnd")) {
                System.out.println("Race end");
            } else if (msgFromServer.msgType.equals("gameStart")) {
                System.out.println("Race start");
            } else {
                send(new Ping());
            }
        }
    }


    private double getNextTrackAngle(GameInitDescriptor gameInit, Position carPosition) {
        int pieceIndex = (int) carPosition.getPieceNumber();
        int nextPieceIndex = 0;
        if(pieceIndex + 1 < gameInit.data.race.track.pieces.length) {
            nextPieceIndex = pieceIndex + 1;
        }

        return gameInit.data.race.track.pieces[nextPieceIndex].angle;
    }

    private double getTrackAngle(GameInitDescriptor gameInit, Position carPositions) {
        return gameInit.data.race.track.pieces[((int) carPositions.getPieceNumber())].angle;
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