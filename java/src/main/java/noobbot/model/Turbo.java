package noobbot.model;

import noobbot.descriptor.TurboAvailableDescriptor;

public class Turbo {

    private double durationInTicks;

    public Turbo(TurboAvailableDescriptor descriptor) {
        this.durationInTicks = descriptor.data.turboDurationTicks;
    }

    public double getDurationInTicks() {
        return durationInTicks;
    }

}
