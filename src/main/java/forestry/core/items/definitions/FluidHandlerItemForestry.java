package forestry.core.items.definitions;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;

import forestry.core.config.Config;

/**
 * Fluid handler that consumes the container item after it was used.
 * <p>
 * This function can be disabled in the config with {@link Config#nonConsumableCapsules}.
 */
public class FluidHandlerItemForestry extends FluidHandlerItemStackSimple.Consumable {
	private final EnumContainerType containerType;

	public FluidHandlerItemForestry(ItemStack container, EnumContainerType containerType) {
		super(container, FluidAttributes.BUCKET_VOLUME);
		this.containerType = containerType;
	}

	/**
	 * Checks if the given fluid can be contained in this handler. This property gets defined by the container type of
	 * this handler.
	 * <p>
	 * Capsules can't contain fluid that are hotter or equal to the melting point of wax
	 */
	private boolean contentsAllowed(FluidStack fluidStack) {
		Fluid fluid = fluidStack.getFluid();
		if (fluid == null) {
			return false;
		}

		if (containerType == EnumContainerType.CAPSULE) {
			return fluid.getAttributes().getTemperature(fluidStack) < 310.15; // melting point of wax in kelvin
		}
		return true;
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
		container.setDamageValue(1); // show the filled container model
	}

	@Override
	protected void setContainerToEmpty() {
		if (Config.nonConsumableCapsules) {        //default behaviour of FluidHandlerItemStack
			container.getTag().remove(FLUID_NBT_KEY);
		} else {
			super.setContainerToEmpty();
		}
	}
}

