package noobbot.model;

import java.util.ArrayList;
import java.util.List;

public class Track {
    private final Piece longestStraight;
    private List<Piece> pieces;
    private List<Lane> lanes;
    private List<Turn> turns;

    public Track(List<Piece> pieces, List<Lane> lanes) {
        this.pieces = pieces;
        this.lanes = lanes;

        longestStraight = calculateLongestStraight();

        turns = getTurns(pieces);

        for (Turn turn : turns) {
            System.out.println(String.format("Turn %s: %s", turns.indexOf(turn), turn.toString()));
        }

    }

    private List<Turn> getTurns(List<Piece> pieces) {
        List<Turn> turns = new ArrayList<>();
        List<Piece> tmp = new ArrayList<>();

        for (Piece piece : pieces) {
            if(piece.getAngle() == 0) {
                if(tmp.size() > 0) {
                    turns.add(getTurn(tmp));

                    tmp.clear();
                } else {
                    continue;
                }
            } else if(tmp.size() > 0 && !haveSameSign(piece.getAngle(), tmp.get(0).getAngle())) {
                turns.add(getTurn(tmp));

                tmp.clear();
            }
            else {
                tmp.add(piece);
            }
        }

        if(tmp.size() > 0) {
            turns.add(getTurn(tmp));
        }

        return turns;
    }

    private Turn getTurn(List<Piece> pieces) {
        return new Turn(pieces.toArray(new Piece[pieces.size()]));
    }

    private boolean haveSameSign(double x, double y) {
        return ((x<0) == (y<0));
    }

    private Piece calculateLongestStraight() {
        double longestLength = 0;
        Piece longest = null;

        for (Piece piece : getPieces()) {
            double length = getStraightLength(piece);
            if(longestLength < length) {
                longest = piece;
                longestLength = length;
            }
        }

        return longest;
    }

    public Piece getBeginningPieceOfLongestStraight() {
        return longestStraight;
    }

    public double getStraightLength(Piece piece) {
        if(piece.getAngle() != 0) {
            return 0;
        }

        double length = piece.getLength(lanes.get(0)); //lanes all have the same length on a straight piece
        Piece nextPiece = getPieceAfter(piece);
        while(nextPiece.getAngle() == 0) {
            length += nextPiece.getLength(lanes.get(0));
            nextPiece = getPieceAfter(nextPiece);
        }

        return length;
    }

    public Double getDistanceBetween(Position position1, Position position2) {
        Piece startingPiece = getPiece(position1);
        double distance = startingPiece.getDistanceFrom(position1);
                
        Piece nextPiece;
        for (nextPiece = getPieceAfter(startingPiece); ! nextPiece.contains(position2); nextPiece = getPieceAfter(nextPiece)) {
            distance += nextPiece.getLength(position1.getLane());
        };
        
        distance += nextPiece.getDistanceTo(position2);
        
        return distance;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public List<Lane> getLanes() {
        return lanes;
    }

    public Piece getPieceAfter(Piece precedingPiece) {
        int nextPieceIndex = precedingPiece.getNumber() + 1;
        return pieces.get(nextPieceIndex % pieces.size());
    }

    public Piece getPieceBefore(Piece piece) {
        int index = piece.getNumber() - 1;
        return pieces.get(index % pieces.size());
    }


    public Piece getPiece(Position position) {
        return pieces.get(position.getPieceNumber());
    }

    public Turn getPreviousTurn(Piece piece) {
        int number = piece.getNumber();

        for(int i = turns.size() -1;i >= 0;i--) {
            Turn turn = turns.get(i);

            if(turn.getStartingPieceNumber() < number) {
                return turn;
            }
//            if(!turn.containsPiece(piece) && turn.getStartingPieceNumber() > number) {
//                return getPreviousTurn(turn);
//            }
        }

//        for (Turn turn : turns) {
//            if(!turn.containsPiece(piece) && turn.getStartingPieceNumber() > number) {
//                return getPreviousTurn(turn);
//            }
//        }

        return null;
    }

    private Turn getPreviousTurn(Turn turn) {
        int index = turns.indexOf(turn) - 1;

        return turns.get(index % turns.size());
    }
}
