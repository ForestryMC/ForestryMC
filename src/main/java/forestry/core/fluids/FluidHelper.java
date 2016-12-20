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

import javax.annotation.Nonnull;

import forestry.core.utils.InventoryUtil;
import forestry.core.utils.ItemStackUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

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

	public static boolean canAcceptFluid(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, @Nonnull FluidStack fluid) {
		IFluidHandler capability = FluidUtil.getFluidHandler(world, pos, facing);
		if (capability != null) {
			if(capability.getTankProperties() == null){
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
		if (fluidToFill == null) {
			return FillStatus.INVALID_INPUT;
		}

		ItemStack input = inv.getStackInSlot(inputSlot);
		if (input == null) {
			return FillStatus.INVALID_INPUT;
		}
		ItemStack output = inv.getStackInSlot(outputSlot);

		ItemStack filled = input.copy();
		filled.stackSize = 1;

		if(emptyStack == null){
			emptyStack = filled;
		}
		
		IFluidHandler fluidFilledHandler = FluidUtil.getFluidHandler(filled);
		IFluidHandler fluidEmptyHandler = FluidUtil.getFluidHandler(emptyStack);
		if (fluidFilledHandler == null || emptyStack != null && fluidEmptyHandler == null) {
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

		boolean moveToOutput = fluidInContainer.amount >= containerEmptyCapacity;
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
			}else{
				inv.setInventorySlotContents(inputSlot, filled);
			}
		}

		return FillStatus.SUCCESS;
	}
	
	public static boolean drainContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot) {
		ItemStack input = inv.getStackInSlot(inputSlot);
		if (input == null) {
			return false;
		}

		ItemStack drainedItemSimulated = FluidUtil.tryEmptyContainer(input, fluidHandler, Fluid.BUCKET_VOLUME, null, false);
		if (drainedItemSimulated == null) {
			return false;
		}

		if (input.stackSize == 1 || drainedItemSimulated.stackSize == 0) {
			ItemStack drainedItem = FluidUtil.tryEmptyContainer(input, fluidHandler, Fluid.BUCKET_VOLUME, null, true);
			
			if (drainedItem != null) {
				if(drainedItem.stackSize > 0){
					inv.setInventorySlotContents(inputSlot, drainedItem);
				}else {
					inv.decrStackSize(inputSlot, 1);
				}
				return true;
			}
		}

		return false;
	}

	public static FillStatus drainContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot, boolean doDrain) {
		ItemStack input = inv.getStackInSlot(inputSlot);
		if (input == null) {
			return FillStatus.INVALID_INPUT;
		}
		ItemStack outputStack = inv.getStackInSlot(outputSlot);
		
		ItemStack drainedItemSimulated = FluidUtil.tryEmptyContainer(input, fluidHandler, Fluid.BUCKET_VOLUME, null, false);
		if (drainedItemSimulated == null) {
			return FillStatus.INVALID_INPUT;
		}

		if (outputStack == null || drainedItemSimulated.stackSize == 0 || ItemStackUtil.isIdenticalItem(outputStack, drainedItemSimulated) && outputStack.stackSize + drainedItemSimulated.stackSize < outputStack.getMaxStackSize()) {
			if(doDrain){
				ItemStack drainedItem = FluidUtil.tryEmptyContainer(input, fluidHandler, Fluid.BUCKET_VOLUME, null, true);
				
				if (drainedItem != null) {
					if(drainedItem.stackSize > 0){
						ItemStack newStack = drainedItem.copy();
						if(outputStack != null){
							newStack.stackSize+=outputStack.stackSize;
						}
						if (isFillableContainerAndEmpty(newStack)) {
							inv.setInventorySlotContents(outputSlot, newStack);
							inv.decrStackSize(inputSlot, 1);
						}
					}else {
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
	
	public static ItemStack getEmptyContainer(ItemStack container){
		if(container == null){
			return null;
		}
		ItemStack empty = container.copy();
		empty.stackSize = 1;
		IFluidHandler fluidHandler = FluidUtil.getFluidHandler(empty);
		if (fluidHandler == null) {
			return null;
		}
		if(fluidHandler.drain(Integer.MAX_VALUE, true) != null){
			return empty;
		}
		return null;
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
		if (container.stackSize <= 0) {
			return null;
		}

		return container;
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
}
