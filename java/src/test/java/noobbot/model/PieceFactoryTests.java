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
    private TargetAngleSpeed targetAngleSpeed;

    @Before
    public void setup() {
        targetAngleSpeed = mock(TargetAngleSpeed.class);
        pieceFactory = new PieceFactory(targetAngleSpeed);
        pieceDescriptor = mock(GameInitDescriptor.Data.Race.Track.Piece.class);
    }

    @Test
    public void anglePieceIsCreated() {
        pieceDescriptor.angle = 1;
        pieceDescriptor.hasSwitch = true;
        pieceDescriptor.length = 10;
        pieceDescriptor.radius = 20;

        Piece piece = pieceFactory.create(pieceDescriptor, 0);

        assertThat(piece, instanceOf(AnglePiece.class));
    }

    @Test
    public void straightPieceIsCreated() {
        pieceDescriptor.hasSwitch = true;
        pieceDescriptor.length = 20;

        Piece piece = pieceFactory.create(pieceDescriptor, 0);

        assertThat(piece, instanceOf(StraightPiece.class));
    }
}