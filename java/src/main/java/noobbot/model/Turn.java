package noobbot.model;

/**
 * Created by Saulis on 10/05/14.
 */
public class Turn {
    private final Piece[] pieces;

    public Turn(Piece[] pieces) {

        this.pieces = pieces;
    }

    public boolean containsPiece(Piece piece) {
        for (Piece p : pieces) {
            if(p == piece) {
                return true;
            }
        }

        return false;
    }

    public int getStartingPieceNumber() {
        return pieces[0].getNumber();
    }

    public void modifySpeed(double maxSlipAngle, int ticksInCorner) {
        for (Piece piece : pieces) {
            piece.modifySpeed(maxSlipAngle, ticksInCorner);
        }

    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i=0;i<pieces.length;i++) {
            s.append(String.format("%s: %s ", i, pieces[i].getAngle()));
        }

        return s.toString().trim();
    }
}
