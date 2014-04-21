package noobbot.model;

import noobbot.Keimola;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Saulis on 21/04/14.
 */
public class NavigatorTests {
    public Track keimola = Keimola.getTrack();;
    private Navigator navigator;

    @Before
    public void setup() {
        navigator = new Navigator(keimola);
    }

    @Test
    public void nice() {

    }

}
