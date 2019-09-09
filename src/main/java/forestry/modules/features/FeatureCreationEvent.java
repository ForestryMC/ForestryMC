package forestry.modules.features;


import net.minecraftforge.eventbus.api.Event;

public class FeatureCreationEvent extends Event {
	public final String containerID;
	public final FeatureType type;

	public FeatureCreationEvent(String containerID, FeatureType type) {
		this.containerID = containerID;
		this.type = type;
	}
}
