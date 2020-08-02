package forestry.modules.features;

import javax.annotation.Nullable;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

import net.minecraftforge.fml.network.IContainerFactory;

import forestry.core.config.Constants;

public class FeatureContainerType<C extends Container> implements IContainerTypeFeature<C> {
    protected final String moduleID;
    protected final String identifier;
    protected final IContainerFactory<C> containerFactory;
    @Nullable
    private ContainerType<C> containerType;

    public FeatureContainerType(String moduleID, String identifier, IContainerFactory<C> containerFactory) {
        this.moduleID = moduleID;
        this.identifier = identifier;
        this.containerFactory = containerFactory;
    }


    @Override
    public void setContainerType(ContainerType<C> containerType) {
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
    public ContainerType<C> getContainerType() {
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
