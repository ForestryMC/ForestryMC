package forestry.modules.features;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IEntityTypeFeature<E extends Entity> extends IModFeature {

	@Override
	default void create() {
		EntityType<E> entityType = getEntityTypeConstructor().build(getIdentifier());
		entityType.setRegistryName(new ResourceLocation(getModId(), getIdentifier()));
		setEntityType(entityType);
	}

	@Override
	@SuppressWarnings("unchecked")
	default <R extends IForgeRegistryEntry<R>> void register(RegistryEvent.Register<R> event) {
		IForgeRegistry<R> registry = event.getRegistry();
		Class<R> superType = registry.getRegistrySuperType();
		if (EntityType.class.isAssignableFrom(superType) && hasEntityType()) {
			registry.register((R) entityType());
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
