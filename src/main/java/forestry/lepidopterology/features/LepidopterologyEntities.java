package forestry.lepidopterology.features;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.MobEntity;

import forestry.lepidopterology.ModuleLepidopterology;
import forestry.lepidopterology.entities.EntityButterfly;
import forestry.modules.features.FeatureEntityType;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class LepidopterologyEntities {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleLepidopterology.class);

	public static final FeatureEntityType<EntityButterfly> BUTTERFLY = REGISTRY.entity(EntityButterfly::new, EntityClassification.CREATURE, "butterfly", (builder) -> builder.size(1.0f, 0.4f), MobEntity::func_233666_p_);
}
