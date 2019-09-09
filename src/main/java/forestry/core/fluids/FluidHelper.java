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

import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import forestry.core.utils.ItemStackUtil;

//TODO: Fix isFillable's
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

	public static boolean canAcceptFluid(World world, BlockPos pos, Direction facing, FluidStack fluid, boolean checkSpace) {
		LazyOptional<IFluidHandler> capability = FluidUtil.getFluidHandler(world, pos, facing);
		return capability.filter((handler) -> {
			for (int tank = 0; tank < handler.getTanks(); tank++) {
				int amountFilled = handler.fill(fluid, IFluidHandler.FluidAction.SIMULATE);
				if (amountFilled > 0 && (!checkSpace || amountFilled >= fluid.getAmount())) {
					return true;
				}
			}
			return false;
		})
			.isPresent();
	}

	public static boolean canAcceptFluid(World world, BlockPos pos, Direction facing, FluidStack fluid) {
		return canAcceptFluid(world, pos, facing, fluid, false);
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

		if (emptyStack.isEmpty()) {
			emptyStack = filled;
		}

		LazyOptional<IFluidHandlerItem> fluidFilledHandlerCap = FluidUtil.getFluidHandler(filled);
		LazyOptional<IFluidHandlerItem> fluidEmptyHandlerCap = FluidUtil.getFluidHandler(emptyStack);
		if (!fluidFilledHandlerCap.isPresent() || !fluidEmptyHandlerCap.isPresent()) {
			return FillStatus.INVALID_INPUT;
		}

		IFluidHandlerItem fluidFilledHandler = fluidFilledHandlerCap.orElse(null);
		IFluidHandlerItem fluidEmptyHandler = fluidEmptyHandlerCap.orElse(null);

		int containerEmptyCapacity = fluidEmptyHandler.fill(new FluidStack(fluidToFill, Integer.MAX_VALUE), IFluidHandler.FluidAction.SIMULATE);
		int containerCapacity = fluidFilledHandler.fill(new FluidStack(fluidToFill, Integer.MAX_VALUE), IFluidHandler.FluidAction.SIMULATE);
		if (containerCapacity <= 0 && containerEmptyCapacity <= 0) {
			return FillStatus.INVALID_INPUT;
		}

		FluidStack canDrain = fluidHandler.drain(new FluidStack(fluidToFill, containerCapacity), IFluidHandler.FluidAction.SIMULATE);
		if (canDrain.isEmpty()) {
			return FillStatus.NO_FLUID;
		}

		if (fluidFilledHandler.fill(canDrain, IFluidHandler.FluidAction.EXECUTE) <= 0) {
			return FillStatus.NO_FLUID; // standard containers will not fill if there isn't enough fluid
		}

		FluidStack fluidInContainer = fluidFilledHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
		if (fluidInContainer.isEmpty()) {
			return FillStatus.INVALID_INPUT;
		}

		filled = fluidFilledHandler.getContainer();

		boolean moveToOutput = fluidInContainer.getAmount() >= containerCapacity;
		if (moveToOutput) {
			if (!output.isEmpty() && (output.getCount() >= output.getMaxStackSize() || !ItemStackUtil.areItemStacksEqualIgnoreCount(filled, output))) {
				return FillStatus.NO_SPACE;
			}
		} else {
			if (input.getCount() > 1) {
				return FillStatus.NO_SPACE;
			}
		}

		if (doFill) {
			fluidHandler.drain(canDrain, IFluidHandler.FluidAction.EXECUTE);
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

		FluidActionResult fluidActionSimulated = FluidUtil.tryEmptyContainer(input, fluidHandler, FluidAttributes.BUCKET_VOLUME, null, false);
		if (!fluidActionSimulated.isSuccess()) {
			return false;
		}

		ItemStack drainedItemSimulated = fluidActionSimulated.getResult();
		if (input.getCount() == 1 || drainedItemSimulated.isEmpty()) {
			FluidActionResult fluidActionResult = FluidUtil.tryEmptyContainer(input, fluidHandler, FluidAttributes.BUCKET_VOLUME, null, true);
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

		//Only needed so we can test if the container can be filled
		FluidStack content = FluidUtil.getFluidContained(input).orElse(FluidStack.EMPTY);
		FluidActionResult drainedResultSimulated = FluidUtil.tryEmptyContainer(input, fluidHandler, FluidAttributes.BUCKET_VOLUME, null, false);
		if (!drainedResultSimulated.isSuccess()) {
			return FillStatus.INVALID_INPUT;
		}

		ItemStack drainedItemSimulated = drainedResultSimulated.getResult();

		if (outputStack.isEmpty() || drainedItemSimulated.isEmpty() || ItemStackUtil.isIdenticalItem(outputStack, drainedItemSimulated) && outputStack.getCount() + drainedItemSimulated.getCount() < outputStack.getMaxStackSize()) {
			if (doDrain) {
				FluidActionResult drainedResult = FluidUtil.tryEmptyContainer(input, fluidHandler, FluidAttributes.BUCKET_VOLUME, null, true);
				if (drainedResult.isSuccess()) {
					ItemStack drainedItem = drainedResult.getResult();
					if (!drainedItem.isEmpty()) {
						ItemStack newStack = drainedItem.copy();
						if (!outputStack.isEmpty()) {
							newStack.grow(outputStack.getCount());
						}
						if (!isFillableContainer(newStack, content) || isFillableContainerAndEmpty(newStack, content)) {
							inv.setInventorySlotContents(outputSlot, newStack);
							inv.decrStackSize(inputSlot, 1);
						}
						if (isDrainableContainer(newStack) && !isEmpty(newStack)) {
							inv.setInventorySlotContents(inputSlot, newStack);
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

	public static boolean isFillableContainer(ItemStack container, FluidStack content) {
		LazyOptional<IFluidHandlerItem> fluidHandlerCap = FluidUtil.getFluidHandler(container);
		if (!fluidHandlerCap.isPresent()) {
			return false;
		}

		return fluidHandlerCap.filter(handler -> handler.fill(new FluidStack(content, 1), IFluidHandler.FluidAction.SIMULATE) > 0).isPresent();
	}

	public static boolean isFillableContainerAndEmpty(ItemStack container, FluidStack content) {
		LazyOptional<IFluidHandlerItem> fluidHandlerCap = FluidUtil.getFluidHandler(container);
		if (!fluidHandlerCap.isPresent()) {
			return false;
		}

		return fluidHandlerCap.filter(handler -> handler.fill(new FluidStack(content, 1), IFluidHandler.FluidAction.SIMULATE) > 0 && handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE).isEmpty()).isPresent();
	}

	public static ItemStack getEmptyContainer(ItemStack container) {
		ItemStack empty = container.copy();
		empty.setCount(1);
		LazyOptional<IFluidHandlerItem> fluidHandlerCap = FluidUtil.getFluidHandler(empty);
		if (!fluidHandlerCap.isPresent()) {
			return ItemStack.EMPTY;
		}

		return fluidHandlerCap.filter(fluidHandler -> fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE) != null).isPresent() ? empty : ItemStack.EMPTY;
	}

	public static boolean isFillableContainerWithRoom(ItemStack container) {
		LazyOptional<IFluidHandlerItem> fluidHandlerCap = FluidUtil.getFluidHandler(container);
		return fluidHandlerCap.isPresent();
		/*if (!fluidHandlerCap.isPresent()) {
			return false;
		}

		IFluidHandlerItem fluidHandler = fluidHandlerCap.orElse(null);

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

		return false;*/
	}

	public static boolean isFillableEmptyContainer(ItemStack empty) {
		LazyOptional<IFluidHandlerItem> fluidHandlerCap = FluidUtil.getFluidHandler(empty);
		return fluidHandlerCap.isPresent();
		/*if (!fluidHandlerCap.isPresent()) {
			return false;
		}

		IFluidHandlerItem fluidHandler = fluidHandlerCap.orElse(null);

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

		return true;*/
	}

	public static boolean isDrainableFilledContainer(ItemStack container) {
		LazyOptional<IFluidHandlerItem> fluidHandlerCap = FluidUtil.getFluidHandler(container);
		return fluidHandlerCap.isPresent();
		/*if (!fluidHandlerCap.isPresent()) {
			return false;
		}

		IFluidHandlerItem fluidHandler = fluidHandlerCap.orElse(null);

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

		return true;*/
	}

	public static boolean isDrainableContainer(ItemStack container) {
		LazyOptional<IFluidHandlerItem> fluidHandlerCap = FluidUtil.getFluidHandler(container);
		return fluidHandlerCap.isPresent();
		/*if (!fluidHandlerCap.isPresent()) {
			return false;
		}

		IFluidHandlerItem fluidHandler = fluidHandlerCap.orElse(null);

		IFluidTankProperties[] tankProperties = fluidHandler.getTankProperties();
		for (IFluidTankProperties properties : tankProperties) {
			if (properties.canDrain()) {
				return true;
			}
		}

		return false;*/
	}

	public static boolean isEmpty(ItemStack container) {
		LazyOptional<IFluidHandlerItem> fluidHandlerCap = FluidUtil.getFluidHandler(container);
		if (!fluidHandlerCap.isPresent()) {
			return false;
		}

		return fluidHandlerCap.filter(fluidHandler -> {
			for (int i = 0; i < fluidHandler.getTanks(); i++) {
				if (!fluidHandler.getFluidInTank(i).isEmpty()) {
					return false;
				}
			}
			return true;
		}).isPresent();
	}

}
