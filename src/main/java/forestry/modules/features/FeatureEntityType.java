package forestry.modules.features;

import javax.annotation.Nullable;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import forestry.core.config.Constants;

public class FeatureEntityType<T extends Entity> implements IEntityTypeFeature<T> {
	protected final String moduleID;
	protected final String identifier;
	protected final UnaryOperator<EntityType.Builder<T>> consumer;
	protected final Supplier<AttributeSupplier.Builder> attributes;
	protected final EntityType.EntityFactory<T> factory;
	protected final MobCategory classification;
	@Nullable
	private EntityType<T> entityType;

	public FeatureEntityType(String moduleID, String identifier, UnaryOperator<EntityType.Builder<T>> consumer, EntityType.EntityFactory<T> factory, MobCategory classification,
		Supplier<AttributeSupplier.Builder> attributes) {
		this.moduleID = moduleID;
		this.identifier = identifier;
		this.consumer = consumer;
		this.factory = factory;
		this.attributes = attributes;
		this.classification = classification;
	}

	@Override
	public void setEntityType(EntityType<T> entityType) {
		this.entityType = entityType;
	}

	@Override
	public AttributeSupplier.Builder createAttributes() {
		return attributes.get();
	}

	@Override
	public EntityType.Builder<T> getEntityTypeConstructor() {
		return consumer.apply(EntityType.Builder.of(factory, classification));
	}

	@Override
	public boolean hasEntityType() {
		return entityType != null;
	}

	@Nullable
	@Override
	public EntityType<T> getEntityType() {
		return entityType;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public FeatureType getType() {
		return FeatureType.ENTITY;
	}

	@Override
	public String getModId() {
		return Constants.MOD_ID;
	}

	@Override
	public String getModuleId() {
		return moduleID;
	}
}
