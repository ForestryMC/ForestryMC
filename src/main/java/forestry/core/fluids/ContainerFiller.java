package forestry.core.fluids;

import javax.annotation.Nullable;

import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import forestry.core.utils.ItemStackUtil;

/**
 * Helper to slowly fill containers from a machine's internal tank.
 * Moves filled container from inputSlot to outputSlot.
 */
public class ContainerFiller {
	private final FluidTank fluidTank;
	private final int fillingTime;
	private final IInventory inventory;
	private final int inputSlot;
	private final int outputSlot;

	@Nullable
	private ItemStack usedInput;
	private int fillingProgress;

	public ContainerFiller(FluidTank fluidTank, int fillingTime, IInventory inventory, int inputSlot, int outputSlot) {
		this.fluidTank = fluidTank;
		this.fillingTime = fillingTime;
		this.inventory = inventory;
		this.inputSlot = inputSlot;
		this.outputSlot = outputSlot;
	}

	public void updateServerSide() {
		ItemStack input = inventory.getStackInSlot(inputSlot);
		if (usedInput == null || !ItemStackUtil.isIdenticalItem(usedInput, input)) {
			fillingProgress = 0;
			usedInput = input;
		}

		if (usedInput != null) {
			FluidStack tankContents = fluidTank.getFluid();
			if (!tankContents.isEmpty() && tankContents.getAmount() > 0) {
				if (fillingProgress == 0) {
					Fluid tankFluid = tankContents.getFluid();
					FluidHelper.FillStatus canFill = FluidHelper.fillContainers(fluidTank, inventory, inputSlot, outputSlot, tankFluid, false);
					if (canFill == FluidHelper.FillStatus.SUCCESS) {
						fillingProgress = 1;
					}
				} else {
					fillingProgress++;
					if (fillingProgress >= fillingTime) {
						Fluid tankFluid = tankContents.getFluid();
						FluidHelper.FillStatus filled = FluidHelper.fillContainers(fluidTank, inventory, inputSlot, outputSlot, tankFluid, true);
						if (filled == FluidHelper.FillStatus.SUCCESS) {
							fillingProgress = 0;
						}
					}
				}
			}
		}
	}

	public int getFillingProgress() {
		return fillingProgress;
	}
}
