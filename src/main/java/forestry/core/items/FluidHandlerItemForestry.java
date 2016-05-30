package forestry.core.items;

import forestry.core.fluids.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;

public class FluidHandlerItemForestry extends FluidHandlerItemStackSimple.Consumable {
	private final EnumContainerType containerType;

	public FluidHandlerItemForestry(ItemStack container, EnumContainerType containerType) {
		super(container, Fluid.BUCKET_VOLUME);
		this.containerType = containerType;
	}

	@Override
	public boolean canFillFluidType(FluidStack fluid) {
		Fluids fluidDefinition = Fluids.getFluidDefinition(fluid);
		return fluidDefinition != null && fluidDefinition.getContainerTypes().contains(containerType);
	}

	@Override
	protected void setFluid(FluidStack fluid) {
		super.setFluid(fluid);
		container.setItemDamage(1); // show the filled container model
	}
}

