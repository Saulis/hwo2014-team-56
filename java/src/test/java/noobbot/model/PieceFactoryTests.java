package noobbot.model;

import noobbot.descriptor.GameInitDescriptor;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.mockito.Mockito.mock;

/**
 * Created by Saulis on 16/04/14.
 */
public class PieceFactoryTests {

    private PieceFactory pieceFactory;
    private GameInitDescriptor.Data.Race.Track.Piece pieceDescriptor;

    @Before
    public void setup() {
        pieceFactory = new PieceFactory();
        pieceDescriptor = mock(GameInitDescriptor.Data.Race.Track.Piece.class);
    }

    @Test
    public void anglePieceIsCreated() {
        pieceDescriptor.angle = 1;
        pieceDescriptor.hasSwitch = true;
        pieceDescriptor.length = 10;
        pieceDescriptor.radius = 20;

        Piece piece = pieceFactory.create(pieceDescriptor);

        assertThat(piece, instanceOf(AnglePiece.class));
    }

    @Test
    public void straightPieceIsCreated() {
        pieceDescriptor.hasSwitch = true;
        pieceDescriptor.length = 20;

        Piece piece = pieceFactory.create(pieceDescriptor);

        assertThat(piece, instanceOf(StraightPiece.class));
    }
}