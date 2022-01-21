package forestry.modules.features;

import javax.annotation.Nullable;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import net.minecraftforge.network.IContainerFactory;

import forestry.core.config.Constants;

public class FeatureContainerType<C extends AbstractContainerMenu> implements IContainerTypeFeature<C> {
	protected final String moduleID;
	protected final String identifier;
	protected final IContainerFactory<C> containerFactory;
	@Nullable
	private MenuType<C> containerType;

	public FeatureContainerType(String moduleID, String identifier, IContainerFactory<C> containerFactory) {
		this.moduleID = moduleID;
		this.identifier = identifier;
		this.containerFactory = containerFactory;
	}


	@Override
	public void setContainerType(MenuType<C> containerType) {
		this.containerType = containerType;
	}

	@Override
	public IContainerFactory<C> getContainerFactory() {
		return containerFactory;
	}

	@Override
	public boolean hasContainerType() {
		return containerType != null;
	}

	@Nullable
	@Override
	public MenuType<C> getContainerType() {
		return containerType;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public FeatureType getType() {
		return FeatureType.CONTAINER;
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
