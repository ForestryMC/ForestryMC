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

import javax.annotation.Nullable;

import forestry.core.utils.InventoryUtil;
import forestry.core.utils.ItemStackUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public final class FluidHelper {

	private FluidHelper() {
	}

	public static boolean areFluidStacksEqual(@Nullable FluidStack fluidStack1, @Nullable FluidStack fluidStack2) {
		if (fluidStack1 == null) {
			return fluidStack2 == null;
		} else {
			return fluidStack1.isFluidStackIdentical(fluidStack2);
		}
	}

	public static boolean canAcceptFluid(World world, BlockPos pos, EnumFacing facing, FluidStack fluid) {
		IFluidHandler capability = FluidUtil.getFluidHandler(world, pos, facing);
		if (capability != null) {
			if (capability.getTankProperties() == null) {
				throw new NullPointerException("The fluid handler " + capability.toString() + " returns null in the method getTankProperties, this is not allowed, please report that to the author of the mod from that the handler is.");
			}
			for (IFluidTankProperties tankProperties : capability.getTankProperties()) {
				if (tankProperties.canFillFluidType(fluid)) {
					return true;
				}
			}
		}
		return false;
	}

	public enum FillStatus {
		SUCCESS, INVALID_INPUT, NO_FLUID, NO_SPACE, NO_SPACE_FLUID
	}

	public static FillStatus fillContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot, Fluid fluidToFill, boolean doFill) {
		return fillContainers(fluidHandler, inv, inputSlot, outputSlot, fluidToFill, getEmptyContainer(inv.getStackInSlot(inputSlot)), doFill);
	}

	public static FillStatus fillContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot, Fluid fluidToFill, ItemStack emptyStack, boolean doFill) {
		ItemStack input = inv.getStackInSlot(inputSlot);
		if (input.isEmpty()) {
			return FillStatus.INVALID_INPUT;
		}
		ItemStack output = inv.getStackInSlot(outputSlot);

		ItemStack filled = input.copy();
		filled.setCount(1);

		if(emptyStack.isEmpty()){
			emptyStack = filled;
		}
		
		IFluidHandlerItem fluidFilledHandler = FluidUtil.getFluidHandler(filled);
		IFluidHandlerItem fluidEmptyHandler = FluidUtil.getFluidHandler(emptyStack);
		if (fluidFilledHandler == null || fluidEmptyHandler == null) {
			return FillStatus.INVALID_INPUT;
		}

		int containerEmptyCapacity = fluidEmptyHandler.fill(new FluidStack(fluidToFill, Integer.MAX_VALUE), false);
		int containerCapacity = fluidFilledHandler.fill(new FluidStack(fluidToFill, Integer.MAX_VALUE), false);
		if (containerCapacity <= 0 && containerEmptyCapacity <= 0) {
			return FillStatus.INVALID_INPUT;
		}

		FluidStack canDrain = fluidHandler.drain(new FluidStack(fluidToFill, containerCapacity), false);
		if (canDrain == null || canDrain.amount == 0) {
			return FillStatus.NO_FLUID;
		}

		if (fluidFilledHandler.fill(canDrain, true) <= 0) {
			return FillStatus.NO_FLUID; // standard containers will not fill if there isn't enough fluid
		}

		FluidStack fluidInContainer = fluidFilledHandler.drain(Integer.MAX_VALUE, false);
		if (fluidInContainer == null) {
			return FillStatus.INVALID_INPUT;
		}

		filled = fluidFilledHandler.getContainer();

		boolean moveToOutput = fluidInContainer.amount >= containerCapacity;
		if (moveToOutput) {
			if (!output.isEmpty() && (output.getCount() >= output.getMaxStackSize() || !InventoryUtil.isItemEqual(filled, output))) {
				return FillStatus.NO_SPACE;
			}
		} else {
			if (input.getCount() > 1) {
				return FillStatus.NO_SPACE;
			}
		}

		if (doFill) {
			fluidHandler.drain(canDrain, true);
			if (moveToOutput) {
				if (output.isEmpty()) {
					inv.setInventorySlotContents(outputSlot, filled);
				} else {
					output.grow(1);
				}
				inv.decrStackSize(inputSlot, 1);
			} else {
				inv.setInventorySlotContents(inputSlot, filled);
			}
		}

		return FillStatus.SUCCESS;
	}

	public static boolean drainContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot) {
		ItemStack input = inv.getStackInSlot(inputSlot);
		if (input.isEmpty()) {
			return false;
		}

		FluidActionResult fluidActionSimulated = FluidUtil.tryEmptyContainer(input, fluidHandler, Fluid.BUCKET_VOLUME, null, false);
		if (!fluidActionSimulated.isSuccess()) {
			return false;
		}

		ItemStack drainedItemSimulated = fluidActionSimulated.getResult();
		if (input.getCount() == 1 || drainedItemSimulated.isEmpty()) {
			FluidActionResult fluidActionResult = FluidUtil.tryEmptyContainer(input, fluidHandler, Fluid.BUCKET_VOLUME, null, true);
			if (fluidActionResult.isSuccess()) {
				ItemStack drainedItem = fluidActionResult.getResult();
				if (!drainedItem.isEmpty()) {
					inv.setInventorySlotContents(inputSlot, drainedItem);
				} else {
					inv.decrStackSize(inputSlot, 1);
				}
				return true;
			}
		}

		return false;
	}

	public static FillStatus drainContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot, boolean doDrain) {
		ItemStack input = inv.getStackInSlot(inputSlot);
		if (input.isEmpty()) {
			return FillStatus.INVALID_INPUT;
		}
		ItemStack outputStack = inv.getStackInSlot(outputSlot);

		FluidActionResult drainedResultSimulated = FluidUtil.tryEmptyContainer(input, fluidHandler, Fluid.BUCKET_VOLUME, null, false);
		if (!drainedResultSimulated.isSuccess()) {
			return FillStatus.INVALID_INPUT;
		}

		ItemStack drainedItemSimulated = drainedResultSimulated.getResult();

		if (outputStack.isEmpty() || drainedItemSimulated.isEmpty() || ItemStackUtil.isIdenticalItem(outputStack, drainedItemSimulated) && outputStack.getCount() + drainedItemSimulated.getCount() < outputStack.getMaxStackSize()) {
			if (doDrain) {
				FluidActionResult drainedResult = FluidUtil.tryEmptyContainer(input, fluidHandler, Fluid.BUCKET_VOLUME, null, true);
				if (drainedResult.isSuccess()) {
					ItemStack drainedItem = drainedResult.getResult();
					if (!drainedItem.isEmpty()) {
						ItemStack newStack = drainedItem.copy();
						if (!outputStack.isEmpty()) {
							newStack.grow(outputStack.getCount());
						}
						if (isFillableContainerAndEmpty(newStack)) {
							inv.setInventorySlotContents(outputSlot, newStack);
							inv.decrStackSize(inputSlot, 1);
						}
					} else {
						inv.decrStackSize(inputSlot, 1);
					}
					return FillStatus.SUCCESS;
				}
			}
			return FillStatus.SUCCESS;
		}

		return FillStatus.NO_SPACE;
	}

	public static boolean isFillableContainerAndEmpty(ItemStack container) {
		IFluidHandler fluidHandler = FluidUtil.getFluidHandler(container);
		if (fluidHandler == null) {
			return false;
		}

		IFluidTankProperties[] tankProperties = fluidHandler.getTankProperties();
		for (IFluidTankProperties properties : tankProperties) {
			if (properties.canFill() && properties.getCapacity() > 0) {
				FluidStack contents = properties.getContents();
				if (contents == null) {
					return true;
				} else if (contents.amount > 0) {
					return false;
				}
			}
		}

		return false;
	}

	public static ItemStack getEmptyContainer(ItemStack container) {
		ItemStack empty = container.copy();
		empty.setCount(1);
		IFluidHandler fluidHandler = FluidUtil.getFluidHandler(empty);
		if (fluidHandler == null) {
			return ItemStack.EMPTY;
		}
		if (fluidHandler.drain(Integer.MAX_VALUE, true) != null) {
			return empty;
		}
		return ItemStack.EMPTY;
	}

	public static boolean isFillableContainerWithRoom(ItemStack container) {
		IFluidHandler fluidHandler = FluidUtil.getFluidHandler(container);
		if (fluidHandler == null) {
			return false;
		}

		IFluidTankProperties[] tankProperties = fluidHandler.getTankProperties();
		for (IFluidTankProperties properties : tankProperties) {
			if (properties.canFill() && properties.getCapacity() > 0) {
				FluidStack contents = properties.getContents();
				if (contents == null) {
					return true;
				} else if (contents.amount < properties.getCapacity()) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isFillableEmptyContainer(ItemStack empty) {
		IFluidHandler fluidHandler = FluidUtil.getFluidHandler(empty);
		if (fluidHandler == null) {
			return false;
		}

		IFluidTankProperties[] tankProperties = fluidHandler.getTankProperties();
		for (IFluidTankProperties properties : tankProperties) {
			if (!properties.canFill()) {
				return false;
			}

			FluidStack contents = properties.getContents();
			if (contents != null && contents.amount > 0) {
				return false;
			}
		}

		return true;
	}

	public static boolean isDrainableFilledContainer(ItemStack container) {
		IFluidHandler fluidHandler = FluidUtil.getFluidHandler(container);
		if (fluidHandler == null) {
			return false;
		}

		IFluidTankProperties[] tankProperties = fluidHandler.getTankProperties();
		for (IFluidTankProperties properties : tankProperties) {
			if (!properties.canDrain()) {
				return false;
			}

			FluidStack contents = properties.getContents();
			if (contents == null || contents.amount < properties.getCapacity()) {
				return false;
			}
		}

		return true;
	}
}
