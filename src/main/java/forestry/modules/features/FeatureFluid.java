package forestry.modules.features;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.function.Supplier;

import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.BlockItem;

import forestry.core.config.Constants;
import forestry.core.fluids.BlockForestryFluid;
import forestry.core.fluids.ForestryFluid;
import forestry.core.items.DrinkProperties;

public class FeatureFluid implements IFluidFeature {
    private final FeatureBlock<BlockForestryFluid, BlockItem> block;
    private final FluidProperties properties;
    private final String moduleID;
    private final String identifier;
    @Nullable
    private FlowingFluid fluid;
    @Nullable
    private FlowingFluid flowing;

    public FeatureFluid(Builder builder) {
        this.moduleID = builder.moduleID;
        this.identifier = builder.identifier;
        this.block = builder.registry.block(() -> new BlockForestryFluid(this), "fluid_" + builder.identifier);
        this.properties = new FluidProperties(builder);
    }

    @Override
    public FeatureBlock<BlockForestryFluid, BlockItem> fluidBlock() {
        return block;
    }

    @Override
    public void setFluid(FlowingFluid fluid) {
        this.fluid = fluid;
    }

    @Override
    public void setFlowing(@Nullable FlowingFluid flowing) {
        this.flowing = flowing;
    }

    @Override
    public Supplier<FlowingFluid> getFluidConstructor(boolean flowing) {
        return () -> flowing ? new ForestryFluid.Flowing(this) : new ForestryFluid.Source(this);
    }

    @Nullable
    @Override
    public FlowingFluid getFluid() {
        return fluid;
    }

    @Nullable
    @Override
    public FlowingFluid getFlowing() {
        return flowing;
    }

    @Override
    public FluidProperties getProperties() {
        return properties;
    }

    @Override
    public boolean hasFlowing() {
        return flowing != null;
    }

    @Override
    public boolean hasFluid() {
        return fluid != null;
    }

    @Override
    public FeatureType getType() {
        return FeatureType.FLUID;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getModId() {
        return Constants.MOD_ID;
    }

    @Override
    public String getModuleId() {
        return moduleID;
    }

    public static class Builder {
        private final IFeatureRegistry registry;
        private final String moduleID;
        final String identifier;

        int density = 1000;
        int viscosity = 1000;
        int temperature = 295;
        Color particleColor = Color.WHITE;
        int flammability = 0;
        boolean flammable = false;
        @Nullable
        DrinkProperties properties = null;

        public Builder(IFeatureRegistry registry, String moduleID, String identifier) {
            this.registry = registry;
            this.moduleID = moduleID;
            this.identifier = identifier;
        }

        public Builder flammable() {
            this.flammable = true;
            return this;
        }

        public Builder flammability(int flammability) {
            this.flammability = flammability;
            return this;
        }

        public Builder density(int density) {
            this.density = density;
            return this;
        }

        public Builder viscosity(int viscosity) {
            this.viscosity = viscosity;
            return this;
        }

        public Builder temperature(int temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder setParticleColor(Color particleColor) {
            this.particleColor = particleColor;
            return this;
        }

        public Builder particleColor(Color color) {
            this.particleColor = color;
            return this;
        }

        public Builder drinkProperties(int healAmount, float saturationModifier, int maxItemUseDuration) {
            this.properties = new DrinkProperties(healAmount, saturationModifier, maxItemUseDuration);
            return this;
        }

        public FeatureFluid create() {
            return registry.register(new FeatureFluid(this));
        }
    }
}
