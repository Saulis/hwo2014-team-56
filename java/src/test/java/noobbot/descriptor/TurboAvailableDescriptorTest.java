package noobbot.descriptor;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

public class TurboAvailableDescriptorTest {

    private String message = "{\"msgType\": \"turboAvailable\", \"data\": {\r\n" + 
            "  \"turboDurationMilliseconds\": 500.0,\r\n" + 
            "  \"turboDurationTicks\": 30,\r\n" + 
            "  \"turboFactor\": 3.0\r\n" + 
            "}}";

    TurboAvailableDescriptor sut;
    Gson gson;

    @Before
    public void setUp()
    {
        gson = new Gson();
        sut = gson.fromJson(message, TurboAvailableDescriptor.class);
    }

    @Test
    public void messageTypeFromInput() {
        assertThat(sut.msgType, is("turboAvailable"));
    }

    @Test
    public void durationMillisFromInput() {
        assertThat(sut.data.turboDurationMilliseconds, is(500.0));
    }

    @Test
    public void durationTicksFromInput() {
        assertThat(sut.data.turboDurationTicks, is(30.0));
    }

    @Test
    public void turboFactorFromInput() {
        assertThat(sut.data.turboFactor, is(3.0));
    }
}
