package forestry.modules.features;

import net.minecraftforge.eventbus.api.Event;

public class RegisterFeatureEvent extends Event {
    public void register(Class<? extends IModFeature> featureClass) {
        for (IModFeature feature : featureClass.getEnumConstants()) {
            ModFeatureRegistry.get(feature.getModId()).register(feature);
        }
    }

}
