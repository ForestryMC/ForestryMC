package forestry.modules.features;

import net.minecraftforge.eventbus.api.Event;

public class FeatureCreationEvent extends Event {
    public final String containerID;
    public final String moduleID;
    public final FeatureType type;

    public FeatureCreationEvent(String containerID, String moduleID, FeatureType type) {
        this.containerID = containerID;
        this.moduleID = moduleID;
        this.type = type;
    }
}
