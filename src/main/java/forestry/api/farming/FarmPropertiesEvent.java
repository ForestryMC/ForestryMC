package forestry.api.farming;

import net.minecraftforge.eventbus.api.Event;

public class FarmPropertiesEvent extends Event {
    private final String identifier;
    private final IFarmPropertiesBuilder propertiesBuilder;

    public FarmPropertiesEvent(String identifier, IFarmPropertiesBuilder propertiesBuilder) {
        this.identifier = identifier;
        this.propertiesBuilder = propertiesBuilder;
    }

    public String getIdentifier() {
        return identifier;
    }

    public IFarmPropertiesBuilder getBuilder() {
        return propertiesBuilder;
    }
}
