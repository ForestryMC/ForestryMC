package forestry.modules.features;

import forestry.core.fluids.BlockForestryFluid;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public interface IFluidFeature extends IModFeature {

    FeatureBlock<BlockForestryFluid, BlockItem> fluidBlock();

    default Fluid apply(Fluid fluid) {
        return fluid;
    }

    void setFluid(FlowingFluid fluid);

    void setFlowing(FlowingFluid flowing);

    Supplier<FlowingFluid> getFluidConstructor(boolean flowing);

    @Nullable
    FlowingFluid getFluid();

    @Nullable
    FlowingFluid getFlowing();

    FluidProperties getProperties();

    boolean hasFluid();

    boolean hasFlowing();

    default FlowingFluid fluid() {
        FlowingFluid fluid = getFluid();
        if (fluid == null) {
            throw new IllegalStateException("Called feature getter method before content creation.");
        }
        return fluid;
    }

    default FlowingFluid flowing() {
        FlowingFluid flowing = getFlowing();
        if (flowing == null) {
            throw new IllegalStateException("Called feature getter method before content creation.");
        }
        return flowing;
    }

    default FluidStack fluidStack(int amount) {
        if (hasFluid()) {
            return new FluidStack(fluid(), amount);
        }
        return FluidStack.EMPTY;
    }

    default FluidStack fluidStack() {
        return fluidStack(FluidAttributes.BUCKET_VOLUME);
    }

    @Override
    default void create() {
        FlowingFluid fluid = getFluidConstructor(false).get();
        FlowingFluid flowing = getFluidConstructor(true).get();
        fluid.setRegistryName(getModId(), getIdentifier());
        flowing.setRegistryName(getModId(), getIdentifier() + "_flowing");
        setFluid(fluid);
        setFlowing(flowing);
    }

    @Override
    @SuppressWarnings("unchecked")
    default <T extends IForgeRegistryEntry<T>> void register(RegistryEvent.Register<T> event) {
        IForgeRegistry<T> registry = event.getRegistry();
        Class<T> superType = registry.getRegistrySuperType();
        if (Fluid.class.isAssignableFrom(superType)) {
            registry.register((T) fluid());
        }
    }
}
