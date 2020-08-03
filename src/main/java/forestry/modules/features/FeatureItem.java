package forestry.modules.features;

import forestry.core.config.Constants;
import net.minecraft.item.Item;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class FeatureItem<I extends Item> implements IItemFeature<I>, Supplier<I> {
    private final String moduleID;
    private final String identifier;
    private final Supplier<I> constructor;
    @Nullable
    private I item;

    public FeatureItem(String moduleID, String identifier, Supplier<I> constructor) {
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
    public I get() {
        return item();
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Supplier<I> getItemConstructor() {
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
