package forestry.modules.features;

import javax.annotation.Nullable;
import java.util.function.UnaryOperator;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;

import forestry.core.config.Constants;

public class FeatureEntityType<T extends Entity> implements IEntityTypeFeature<T> {
	protected final String moduleID;
	protected final String identifier;
	protected final UnaryOperator<EntityType.Builder<T>> consumer;
	protected final EntityType.IFactory<T> factory;
	protected final EntityClassification classification;
	@Nullable
	private EntityType<T> entityType;

	public FeatureEntityType(String moduleID, String identifier, UnaryOperator<EntityType.Builder<T>> consumer, EntityType.IFactory<T> factory, EntityClassification classification) {
		this.moduleID = moduleID;
		this.identifier = identifier;
		this.consumer = consumer;
		this.factory = factory;
		this.classification = classification;
	}

	@Override
	public void setEntityType(EntityType<T> entityType) {
		this.entityType = entityType;
	}

	@Override
	public EntityType.Builder<T> getEntityTypeConstructor() {
		return consumer.apply(EntityType.Builder.create(factory, classification));
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
