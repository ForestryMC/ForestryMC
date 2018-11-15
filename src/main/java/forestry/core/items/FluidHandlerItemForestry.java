package forestry.core.items;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;

import forestry.core.config.Config;

public class FluidHandlerItemForestry extends FluidHandlerItemStackSimple.Consumable {
	private final EnumContainerType containerType;

	public FluidHandlerItemForestry(ItemStack container, EnumContainerType containerType) {
		super(container, Fluid.BUCKET_VOLUME);
		this.containerType = containerType;
	}

	private boolean contentsAllowed(FluidStack fluidStack) {
		Fluid fluid = fluidStack.getFluid();
		if (fluid == null) {
			return false;
		}

		switch (containerType) {
			case CAPSULE:
				return fluid.getTemperature(fluidStack) < 310.15; // melting point of wax in kelvin
			default:
				return true;
		}
	}

	@Override
	public boolean canFillFluidType(FluidStack fluid) {
		return contentsAllowed(fluid);
	}

	@Override
	public boolean canDrainFluidType(FluidStack fluid) {
		return contentsAllowed(fluid);
	}

	@Override
	protected void setFluid(FluidStack fluid) {
		super.setFluid(fluid);
		container.setItemDamage(1); // show the filled container model
	}

	@Override
	protected void setContainerToEmpty() {
		if (Config.nonConsumableCapsules) {        //default behaviour of FluidHandlerItemStack
			container.getTagCompound().removeTag(FLUID_NBT_KEY);
		} else {
			super.setContainerToEmpty();
		}
	}
}

