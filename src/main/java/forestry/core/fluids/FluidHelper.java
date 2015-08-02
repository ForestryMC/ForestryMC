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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

import forestry.core.inventory.InvTools;
import forestry.core.utils.StackUtils;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class FluidHelper {

	private FluidHelper() {
	}

	public static boolean handleRightClick(IFluidHandler tank, ForgeDirection side, EntityPlayer player, boolean fill, boolean drain) {
		if (player == null) {
			return false;
		}
		ItemStack current = player.inventory.getCurrentItem();
		if (current != null) {

			FluidStack liquid = getFluidStackInContainer(current);

			if (fill && liquid != null) {
				int used = tank.fill(side, liquid, true);

				if (used > 0) {
					if (!player.capabilities.isCreativeMode) {
						ItemStack drainedContainer = getDrainedContainer(current, used);
						if (current.stackSize > 1) {
							player.inventory.decrStackSize(player.inventory.currentItem, 1);
							if (drainedContainer != null && !player.inventory.addItemStackToInventory(drainedContainer)) {
								player.dropPlayerItemWithRandomChoice(drainedContainer, false);
							}
						} else {
							player.inventory.setInventorySlotContents(player.inventory.currentItem, drainedContainer);
						}
						player.inventory.markDirty();
					}
					return true;
				}

			} else if (drain) {

				FluidStack available = tank.drain(side, Integer.MAX_VALUE, false);
				if (available != null) {
					ItemStack filled = getFilledContainer(available, current);

					liquid = getFluidStackInContainer(filled);
					if (liquid != null) {

						if (current.stackSize > 1) {
							if (!player.inventory.addItemStackToInventory(filled)) {
								return false;
							}
							player.inventory.setInventorySlotContents(player.inventory.currentItem, InvTools.depleteItem(current));
							player.inventory.markDirty();
						} else {
							player.inventory.setInventorySlotContents(player.inventory.currentItem, InvTools.depleteItem(current));
							player.inventory.setInventorySlotContents(player.inventory.currentItem, filled);
							player.inventory.markDirty();
						}

						tank.drain(side, liquid.amount, true);
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean fillContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot, Fluid fluidToFill) {
		return fillContainers(fluidHandler, inv, inputSlot, outputSlot, fluidToFill, true);
	}

	public static boolean fillContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot, Fluid fluidToFill, boolean doFill) {
		ItemStack input = inv.getStackInSlot(inputSlot);
		ItemStack output = inv.getStackInSlot(outputSlot);
		ItemStack filled = getFilledContainer(fluidToFill, input);
		if (filled != null && (output == null || (output.stackSize < output.getMaxStackSize() && InvTools.isItemEqual(filled, output)))) {
			FluidStack fluidInContainer = getFluidStackInContainer(filled);
			FluidStack drain = fluidHandler.drain(ForgeDirection.UNKNOWN, fluidInContainer, false);
			if (drain != null && drain.amount == fluidInContainer.amount) {
				if (doFill) {
					fluidHandler.drain(ForgeDirection.UNKNOWN, fluidInContainer, true);
					if (output == null) {
						inv.setInventorySlotContents(outputSlot, filled);
					} else {
						output.stackSize++;
					}
					inv.decrStackSize(inputSlot, 1);
				}
				return true;
			}
		}
		return false;
	}

	public static boolean drainContainers(IFluidHandler fluidHandler, IInventory inv, int slot) {
		return drainContainers(fluidHandler, inv, slot, slot);
	}

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

		int used = fluidHandler.fill(ForgeDirection.UNKNOWN, fluidInContainer, false);
		if (used < fluidInContainer.amount) {
			return false;
		}

		ItemStack emptyItem = getEmptyContainer(input);

		if (output != null && emptyItem != null) {
			if (outputSlot == inputSlot) {
				if (input.stackSize > 1) {
					return false;
				}
			} else if (!StackUtils.isIdenticalItem(output, emptyItem)) {
				return false;
			} else if (output.stackSize + emptyItem.stackSize > output.getMaxStackSize()) {
				return false;
			}
		}

		fluidHandler.fill(ForgeDirection.UNKNOWN, fluidInContainer, true);

		if (emptyItem != null) {
			if (outputSlot == inputSlot) {
				inv.setInventorySlotContents(outputSlot, emptyItem);
				return true;
			}

			if (output == null) {
				inv.setInventorySlotContents(outputSlot, emptyItem);
			} else {
				output.stackSize++;
			}
		}
		inv.decrStackSize(inputSlot, 1);
		return true;
	}

	public static boolean isFillableContainer(ItemStack stack, FluidStack liquid) {
		ItemStack empty = StackUtils.createSplitStack(stack, 1);
		Item item = empty.getItem();
		if (item instanceof IFluidContainerItem) {
			IFluidContainerItem containerItem = (IFluidContainerItem) item;
			return containerItem.fill(empty, liquid, false) > 0;
		}

		for (FluidContainerData cont : FluidContainerRegistry.getRegisteredFluidContainerData()) {
			if (cont.fluid.isFluidEqual(liquid) && (cont.emptyContainer != null && cont.emptyContainer.isItemEqual(empty))) {
				return true;
			}
		}

		return false;
	}

	public static boolean isEmptyContainer(ItemStack empty) {

		if (FluidContainerRegistry.isEmptyContainer(empty)) {
			return true;
		}

		Item item = empty.getItem();
		if (item instanceof IFluidContainerItem) {
			IFluidContainerItem containerItem = (IFluidContainerItem) item;
			FluidStack fluid = containerItem.getFluid(empty);
			return fluid == null || fluid.amount == 0;
		}

		return false;
	}

	public static ItemStack getEmptyContainer(ItemStack container) {
		Item item = container.getItem();
		if (item == null) {
			return null;
		}

		return item.getContainerItem(container);
	}

	public static ItemStack getDrainedContainer(ItemStack container, int drainAmount) {
		if (container == null) {
			return null;
		}

		if (drainAmount == 0) {
			return container;
		}

		Item item = container.getItem();
		if (item instanceof IFluidContainerItem) {
			ItemStack drained = container.copy();
			drained.stackSize = 1;
			IFluidContainerItem containerItem = (IFluidContainerItem) item;
			containerItem.drain(drained, drainAmount, true);
			return drained;
		}

		return getEmptyContainer(container);
	}

	public static ItemStack getFilledContainer(Fluid fluid, ItemStack empty) {
		if (fluid == null) {
			return null;
		}

		FluidStack fluidToFill = new FluidStack(fluid, Integer.MAX_VALUE);

		return getFilledContainer(fluidToFill, empty);
	}

	public static ItemStack getFilledContainer(FluidStack liquid, ItemStack empty) {
		if (liquid == null || empty == null) {
			return null;
		}

		Item item = empty.getItem();
		if (item instanceof IFluidContainerItem) {
			ItemStack full = empty.copy();
			full.stackSize = 1;
			IFluidContainerItem containerItem = (IFluidContainerItem) item;
			containerItem.fill(full, liquid, true);
			return full;
		}

		return FluidContainerRegistry.fillFluidContainer(liquid, empty);
	}

	public static FluidStack getFluidStackInContainer(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof IFluidContainerItem) {
			IFluidContainerItem containerItem = (IFluidContainerItem) item;
			return containerItem.getFluid(stack);
		}

		return FluidContainerRegistry.getFluidForFilledItem(stack);
	}

	public static Fluid getFluidInContainer(ItemStack stack) {
		FluidStack fluidStack = getFluidStackInContainer(stack);
		return fluidStack != null ? fluidStack.getFluid() : null;
	}

	public static boolean containsFluidStack(ItemStack stack, FluidStack fluidStack) {
		Item item = stack.getItem();
		if (item instanceof IFluidContainerItem) {
			IFluidContainerItem containerItem = (IFluidContainerItem) item;
			FluidStack contained = containerItem.getFluid(stack);
			return contained != null && contained.containsFluid(fluidStack);
		}

		return FluidContainerRegistry.containsFluid(stack, fluidStack);
	}

	public static boolean containsFluid(ItemStack stack, Fluid fluid) {
		return containsFluidStack(stack, new FluidStack(fluid, 1));
	}

}
