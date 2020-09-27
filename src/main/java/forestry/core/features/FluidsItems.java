package forestry.core.features;

import forestry.core.ModuleFluids;
import forestry.core.fluids.ForestryFluids;
import forestry.core.items.EnumContainerType;
import forestry.core.items.ItemFluidContainerForestry;
import forestry.modules.features.FeatureItemGroup;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

@FeatureProvider
public class FluidsItems {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleFluids.class);
    public static final FeatureItemGroup<ItemFluidContainerForestry, EnumContainerType> CONTAINERS = REGISTRY.itemGroup(
            ItemFluidContainerForestry::new,
            EnumContainerType.values()
    ).create();

    public static ItemStack getContainer(EnumContainerType type, ForestryFluids fluid) {
        return getContainer(type, fluid.getFluid());
    }

    public static ItemStack getContainer(EnumContainerType type, Fluid fluid) {
        ItemStack container = CONTAINERS.stack(type);
        LazyOptional<IFluidHandlerItem> fluidHandlerCap = FluidUtil.getFluidHandler(container);
        return fluidHandlerCap.map(handler -> {
            handler.fill(new FluidStack(fluid, Integer.MAX_VALUE), IFluidHandler.FluidAction.EXECUTE);
            return container;
        }).orElse(ItemStack.EMPTY);
    }

    private FluidsItems() {
    }
}
