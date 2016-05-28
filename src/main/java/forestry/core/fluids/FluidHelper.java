/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.fluids;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

import forestry.core.PluginFluids;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.ItemStackUtil;

public final class FluidHelper {

	private FluidHelper() {
	}

	public static boolean areFluidStacksEqual(FluidStack fluidStack1, FluidStack fluidStack2) {
		if (fluidStack1 == null) {
			return fluidStack2 == null;
		} else {
			return fluidStack1.isFluidStackIdentical(fluidStack2);
		}
	}

	public enum FillStatus {
		SUCCESS, INVALID_INPUT, NO_FLUID, NO_SPACE
	}

	@Deprecated
	public static FillStatus fillContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot, Fluid fluidToFill) {
		return fillContainers(fluidHandler, inv, inputSlot, outputSlot, fluidToFill, true);
	}

	@Deprecated
	public static FillStatus fillContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot, Fluid fluidToFill, boolean doFill) {
		if (fluidToFill == null) {
			return FillStatus.INVALID_INPUT;
		}

		ItemStack input = inv.getStackInSlot(inputSlot);
		if (input == null) {
			return FillStatus.INVALID_INPUT;
		}
		ItemStack output = inv.getStackInSlot(outputSlot);

		int containerCapacity = getFluidCapacity(fluidToFill, input);
		if (containerCapacity <= 0) {
			return FillStatus.INVALID_INPUT;
		}

		FluidStack canDrain = fluidHandler.drain(new FluidStack(fluidToFill, containerCapacity), false);
		if (canDrain == null || canDrain.amount == 0) {
			return FillStatus.NO_FLUID;
		}

		ItemStack filled = getFilledContainer(canDrain, input);
		if (filled == null) {
			return FillStatus.NO_FLUID; // standard containers will not fill if there isn't enough fluid
		}

		FluidStack fluidInContainer = getFluidStackInContainer(filled);
		if (fluidInContainer == null) {
			return FillStatus.INVALID_INPUT;
		}
		boolean moveToOutput = fluidInContainer.amount >= containerCapacity;
		if (moveToOutput) {
			if (output != null && (output.stackSize >= output.getMaxStackSize() || !InventoryUtil.isItemEqual(filled, output))) {
				return FillStatus.NO_SPACE;
			}
		} else {
			if (input.stackSize > 1) {
				return FillStatus.NO_SPACE;
			}
		}

		if (doFill) {
			fluidHandler.drain(canDrain, true);
			if (moveToOutput) {
				if (output == null) {
					inv.setInventorySlotContents(outputSlot, filled);
				} else {
					output.stackSize++;
				}
				inv.decrStackSize(inputSlot, 1);
			} else {
				inv.setInventorySlotContents(inputSlot, filled);
			}
		}

		return FillStatus.SUCCESS;
	}

	@Deprecated
	public static boolean drainContainers(IFluidHandler fluidHandler, IInventory inv, int slot) {
		return drainContainers(fluidHandler, inv, slot, slot);
	}

	@Deprecated
	public static boolean drainContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot) {
		ItemStack input = inv.getStackInSlot(inputSlot);
		ItemStack output = inv.getStackInSlot(outputSlot);

		if (input == null) {
			return false;
		}

		FluidStack fluidInContainer = getFluidStackInContainer(input);
		if (fluidInContainer == null) {
			return false;
		}

		int used = fluidHandler.fill(fluidInContainer, false);

		ItemStack drainedItem = getDrainedContainer(input, used);
		if (ItemStack.areItemStacksEqual(input, drainedItem)) {
			return false;
		}

		// consume forestry containers if there is only one slot
		if (outputSlot == inputSlot && drainedItem != null) {
			Item item = drainedItem.getItem();
			if (PluginFluids.items.canEmpty == item || PluginFluids.items.waxCapsuleEmpty == item || PluginFluids.items.refractoryEmpty == item) {
				drainedItem = null;
			}
		}

		if (!hasRoomForDrainedContainer(input, output, drainedItem, inputSlot, outputSlot)) {
			return false;
		}

		fluidHandler.fill(fluidInContainer, true);

		if (drainedItem != null) {
			if (outputSlot == inputSlot) {
				inv.setInventorySlotContents(outputSlot, drainedItem);
				return true;
			}

			if (output == null) {
				inv.setInventorySlotContents(outputSlot, drainedItem);
			} else {
				output.stackSize++;
			}
		}
		inv.decrStackSize(inputSlot, 1);
		return true;
	}

	private static boolean hasRoomForDrainedContainer(ItemStack input, ItemStack output, ItemStack drainedItem, int inputSlot, int outputSlot) {
		if (output == null || drainedItem == null) {
			return true;
		}

		if (outputSlot == inputSlot) {
			return input.stackSize == 1;
		}

		if (!ItemStackUtil.isIdenticalItem(output, drainedItem)) {
			return false;
		}

		return output.stackSize + drainedItem.stackSize <= output.getMaxStackSize();
	}

	public static boolean isEmptyContainer(ItemStack empty) {
		if (empty.stackSize > 1) {
			empty = empty.copy();
			empty.stackSize = 1;
		}

		IFluidHandler fluidHandler = FluidUtil.getFluidHandler(empty);
		if (fluidHandler == null) {
			return false;
		}

		FluidStack drain = fluidHandler.drain(Integer.MAX_VALUE, false);
		return drain == null || drain.amount == 0;
	}

	public static boolean isFillableContainer(ItemStack container) {
		FluidStack fluid = getFluidStackInContainer(container);
		if (fluid == null || fluid.amount == 0) {
			return isEmptyContainer(container);
		}

		int capacity = getFluidCapacity(fluid.getFluid(), container);
		return fluid.amount < capacity;
	}

	public static ItemStack getDrainedContainer(ItemStack container, int drainAmount) {
		if (container == null) {
			return null;
		}

		if (drainAmount == 0) {
			return container;
		}

		container = container.copy();
		container.stackSize = 1;

		IFluidHandler fluidHandler = FluidUtil.getFluidHandler(container);
		if (fluidHandler == null) {
			return null;
		}

		fluidHandler.drain(drainAmount, true);
		return container;
	}

	public static ItemStack getEmptyContainer(ItemStack container) {
		return getDrainedContainer(container, Integer.MAX_VALUE);
	}

	public static ItemStack getFilledContainer(Fluid fluid, ItemStack empty) {
		if (fluid == null) {
			return null;
		}

		FluidStack fluidToFill = new FluidStack(fluid, Integer.MAX_VALUE);

		return getFilledContainer(fluidToFill, empty);
	}

	public static ItemStack getFilledContainer(FluidStack liquid, ItemStack stack) {
		if (liquid == null || stack == null) {
			return null;
		}

		stack = stack.copy();
		stack.stackSize = 1;

		IFluidHandler fluidHandler = FluidUtil.getFluidHandler(stack);
		if (fluidHandler == null) {
			return null;
		}

		if (fluidHandler.fill(liquid, true) > 0) {
			return stack;
		}
		return null;
	}

	public static FluidStack getFluidStackInContainer(ItemStack stack) {
		if (stack.stackSize > 1) {
			stack = stack.copy();
			stack.stackSize = 1;
		}

		IFluidHandler fluidHandler = FluidUtil.getFluidHandler(stack);
		if (fluidHandler == null) {
			return null;
		}

		return fluidHandler.drain(Integer.MAX_VALUE, false);
	}

	public static Fluid getFluidInContainer(ItemStack stack) {
		FluidStack fluidStack = getFluidStackInContainer(stack);
		return fluidStack != null ? fluidStack.getFluid() : null;
	}

	public static boolean containsFluid(ItemStack stack, Fluid fluid) {
		FluidStack fluidStackInContainer = getFluidStackInContainer(stack);
		return fluidStackInContainer != null && fluidStackInContainer.getFluid() == fluid;
	}

	public static int getFluidCapacity(Fluid fluid, ItemStack container) {
		if (container == null) {
			return 0;
		}

		if (container.stackSize > 1) {
			container = container.copy();
			container.stackSize = 1;
		}

		IFluidHandler fluidHandler = FluidUtil.getFluidHandler(container);
		if (fluidHandler == null) {
			return 0;
		}

		FluidStack fluidStack = new FluidStack(fluid, Integer.MAX_VALUE);
		return fluidHandler.fill(fluidStack, false);
	}

}
