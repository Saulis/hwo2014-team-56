package noobbot.model;

/**
 * Created by jereketonen on 4/16/14.
 */
public class CarTests {

}

/**
 * private double getSpeed(GameInitDescriptor gameInit, PlayerPosition previousPosition, PlayerPosition carPosition) {
 if(previousPosition == null) {
 return 0;
 }

 CarPositionsDescriptor.Data.PiecePosition previousPiece = previousPosition.getPiecePosition();
 CarPositionsDescriptor.Data.PiecePosition currentPiece = carPosition.getPiecePosition();


 if(previousPiece.pieceIndex == currentPiece.pieceIndex) {
 return currentPiece.inPieceDistance - previousPiece.inPieceDistance;
 } else {
 double length = getPieceLength(gameInit, previousPiece);
 return currentPiece.inPieceDistance + (length - previousPiece.inPieceDistance);
 }
 }

 private double getPieceLength(GameInitDescriptor gameInit, CarPositionsDescriptor.Data.PiecePosition piecePosition) {
 GameInitDescriptor.Data.Race.Track.Piece piece = gameInit.data.race.track.pieces[((int) piecePosition.pieceIndex)];

 if(piece.angle != 0) {
 return Math.abs(piece.angle) / 360 * 2 * Math.PI * getEffectiveRadius(gameInit.data.race.track.lanes[0], piece);
 } else {
 return piece.length;
 }
 }

 private double getEffectiveRadius(GameInitDescriptor.Data.Race.Track.Lane lane, GameInitDescriptor.Data.Race.Track.Piece piece) {
 if(isLeftTurn(piece)) {
 return piece.radius +lane.distanceFromCenter;
 }

 return piece.radius - lane.distanceFromCenter;
 }

 private boolean isLeftTurn(GameInitDescriptor.Data.Race.Track.Piece piece) {
 return piece.angle < 0;
 }

 private double getNextTrackAngle(GameInitDescriptor gameInit, PlayerPosition carPositions) {
 int pieceIndex = (int) carPositions.getPiecePosition().pieceIndex;
 int nextPieceIndex = 0;
 if(pieceIndex + 1 < gameInit.data.race.track.pieces.length) {
 nextPieceIndex = pieceIndex + 1;
 }

 return gameInit.data.race.track.pieces[nextPieceIndex].angle;
 }
 */
