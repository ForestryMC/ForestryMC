package forestry.modules.features;

import javax.annotation.Nullable;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.registries.RegisterEvent;

public interface IEntityTypeFeature<E extends Entity> extends IModFeature {

	@Override
	default void create() {
        setEntityType(getEntityTypeConstructor().build(getIdentifier()));
	}

	@Override
	@SuppressWarnings("unchecked")
	default void register(RegisterEvent event) {
		if (hasEntityType()) {
            event.register(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(getModId(), getIdentifier()), this::entityType);
			// DefaultAttributes.put((EntityType<? extends LivingEntity>) entityType(), createAttributes().build());
        }
	}

	default EntityType<E> entityType() {
		EntityType<E> tileType = getEntityType();
		if (tileType == null) {
			throw new IllegalStateException("Called feature getter method before content creation.");
		}
		return tileType;
	}

	AttributeSupplier.Builder createAttributes();

	void setEntityType(EntityType<E> entityType);

	EntityType.Builder<E> getEntityTypeConstructor();

	boolean hasEntityType();

	@Nullable
	EntityType<E> getEntityType();
}
