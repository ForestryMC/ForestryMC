package forestry.modules.features;

import javax.annotation.Nullable;

import net.minecraft.item.Item;

import forestry.core.config.Constants;

public class FeatureItem<I extends Item> implements IItemFeature<I, I> {
	private final String moduleID;
	private final String identifier;
	private final IFeatureConstructor<I> constructor;
	@Nullable
	private I item;

	public FeatureItem(String moduleID, String identifier, IFeatureConstructor<I> constructor) {
		this.moduleID = moduleID;
		this.identifier = identifier;
		this.constructor = constructor;
	}

	@Override
	public void setItem(I item) {
		this.item = item;
	}

	@Override
	public boolean hasItem() {
		return item != null;
	}

	@Nullable
	@Override
	public I getItem() {
		return item;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public IFeatureConstructor<I> getConstructor() {
		return constructor;
	}

	@Override
	public FeatureType getType() {
		return FeatureType.ITEM;
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
