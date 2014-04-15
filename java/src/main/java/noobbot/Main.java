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
        double currentThrottle = 0.6;
        GameInitDescriptor gameInit = null;
        CarPositionsDescriptor previousPositions = null;

        while((line = reader.readLine()) != null) {
            final MsgWrapper msgFromServer = gson.fromJson(line, MsgWrapper.class);
            System.out.println(line);
            if (msgFromServer.msgType.equals("carPositions")) {
                CarPositionsDescriptor carPositions = gson.fromJson(line, CarPositionsDescriptor.class);

                double slipAngle = carPositions.getSlipAngle();
                double nextAngle = getNextTrackAngle(gameInit, carPositions);
                double speed = getSpeed(gameInit, previousPositions, carPositions);

                if(Math.abs(nextAngle) <= 10) {
                    currentThrottle = 0.925;
                } else {
                    currentThrottle = 0.5;
                }
/*
                if(Math.abs(slipAngle) > 10) {
                    if(currentThrottle >= 0.65) {
                        currentThrottle -= 0.1;
                    } else {
                        currentThrottle = 0.6;
                    }
                } else {
                    if(currentThrottle <= 0.65) {
                        currentThrottle += 0.05;
                    } else
                        currentThrottle = 1;
                }*/

                previousPositions = carPositions;

                System.out.println(String.format("Throttle: %s, Next angle: %s", currentThrottle, nextAngle));
                send(new Throttle(currentThrottle));
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

    private double getSpeed(GameInitDescriptor gameInit, CarPositionsDescriptor previousPositions, CarPositionsDescriptor carPositions) {
        if(previousPositions == null) {
            return 0;
        }

            CarPositionsDescriptor.Data.PiecePosition previousPiece = previousPositions.data[0].piecePosition;
            CarPositionsDescriptor.Data.PiecePosition currentPiece = carPositions.data[0].piecePosition;

            if(previousPiece.pieceIndex == currentPiece.pieceIndex) {
                return currentPiece.inPieceDistance - previousPiece.inPieceDistance;
            } else {
                //TODO: won't work with angle pieces
                double length = getPieceLength(gameInit, previousPiece);
                return currentPiece.inPieceDistance + (length - previousPiece.inPieceDistance);
            }
    }

    private double getPieceLength(GameInitDescriptor gameInit, CarPositionsDescriptor.Data.PiecePosition previousPiece) {
        return gameInit.data.race.track.pieces[((int) previousPiece.pieceIndex)].length;
    }

    private double getNextTrackAngle(GameInitDescriptor gameInit, CarPositionsDescriptor carPositions) {
        int pieceIndex = (int) carPositions.data[0].piecePosition.pieceIndex;
        int nextPieceIndex = 0;
        if(pieceIndex + 1 < gameInit.data.race.track.pieces.length) {
            nextPieceIndex = pieceIndex + 1;
        }

        return gameInit.data.race.track.pieces[nextPieceIndex].angle;
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