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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

import forestry.core.config.Constants;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.plugins.PluginFluids;

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
				int canUseAmount = tank.fill(side, liquid, false);
				if (canUseAmount == 0) {
					return false;
				}

				ItemStack drainedContainer = getDrainedContainer(current, canUseAmount);
				if (ItemStackUtil.isIdenticalItem(current, drainedContainer)) {
					return false;
				}

				int usedAmount = tank.fill(side, liquid, true);
				if (usedAmount > 0) {
					if (!player.capabilities.isCreativeMode) {
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
							player.inventory.setInventorySlotContents(player.inventory.currentItem, InventoryUtil.depleteItem(current));
							player.inventory.markDirty();
						} else {
							player.inventory.setInventorySlotContents(player.inventory.currentItem, InventoryUtil.depleteItem(current));
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

	public static FillStatus fillContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot, Fluid fluidToFill) {
		return fillContainers(fluidHandler, inv, inputSlot, outputSlot, fluidToFill, true);
	}

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

		int fillAmount = containerCapacity;
		if (input.getItem() instanceof IFluidContainerItem) {
			fillAmount = Math.min(Constants.BUCKET_VOLUME, containerCapacity);
		}

		FluidStack canDrain = fluidHandler.drain(ForgeDirection.UNKNOWN, new FluidStack(fluidToFill, fillAmount), false);
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
		boolean moveToOutput = (fluidInContainer.amount >= containerCapacity);
		if (moveToOutput) {
			if ((output != null) && (output.stackSize >= output.getMaxStackSize() || !InventoryUtil.isItemEqual(filled, output))) {
				return FillStatus.NO_SPACE;
			}
		} else {
			if (input.stackSize > 1) {
				return FillStatus.NO_SPACE;
			}
		}

		if (doFill) {
			fluidHandler.drain(ForgeDirection.UNKNOWN, canDrain, true);
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

		if (input.getItem() instanceof IFluidContainerItem) {
			if (fluidInContainer.amount > Constants.BUCKET_VOLUME) {
				fluidInContainer.amount = Constants.BUCKET_VOLUME;
			}
		}

		int used = fluidHandler.fill(ForgeDirection.UNKNOWN, fluidInContainer, false);

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

		fluidHandler.fill(ForgeDirection.UNKNOWN, fluidInContainer, true);

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
		Item item = empty.getItem();
		if (item instanceof IFluidContainerItem) {
			IFluidContainerItem containerItem = (IFluidContainerItem) item;
			FluidStack fluid = containerItem.getFluid(empty);
			return fluid == null || fluid.amount == 0;
		}

		return FluidContainerRegistry.isEmptyContainer(empty);
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

		Item item = container.getItem();
		if (item == null) {
			return null;
		}

		if (item instanceof IFluidContainerItem) {
			ItemStack drained = container.copy();
			drained.stackSize = 1;
			IFluidContainerItem containerItem = (IFluidContainerItem) item;
			containerItem.drain(drained, drainAmount, true);
			return drained;
		} else {
			int capacity = FluidContainerRegistry.getContainerCapacity(container);
			if (capacity > 0) {
				if (drainAmount < capacity) {
					return container;
				}
				return FluidContainerRegistry.drainFluidContainer(container);
			}
		}

		return null;
	}

	public static ItemStack getEmptyContainer(ItemStack container) {
		if (container == null) {
			return null;
		}
		FluidStack fluidStack = FluidHelper.getFluidStackInContainer(container);
		if (fluidStack == null) {
			return null;
		}

		return FluidHelper.getDrainedContainer(container, fluidStack.amount);
	}

	public static List<ItemStack> getAllFilledContainers(ItemStack empty) {
		List<ItemStack> filledContainers = new ArrayList<>();
		for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
			ItemStack filledContainer = getFilledContainer(fluid, empty);
			if (filledContainer != null) {
				filledContainers.add(filledContainer);
			}
		}
		return filledContainers;
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
			ItemStack full = ItemStackUtil.createSplitStack(empty, 1);
			IFluidContainerItem containerItem = (IFluidContainerItem) item;
			containerItem.fill(full, liquid, true);
			return full;
		}

		return FluidContainerRegistry.fillFluidContainer(liquid, empty);
	}

	public static FluidStack getFluidStackInContainer(ItemStack stack) {
		if (stack == null) {
			return null;
		}
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

	public static int getFluidCapacity(Fluid fluid, ItemStack container) {
		if (container == null) {
			return 0;
		}
		Item item = container.getItem();
		if (item instanceof IFluidContainerItem) {
			IFluidContainerItem containerItem = (IFluidContainerItem) item;
			return containerItem.getCapacity(container);
		}

		if (fluid == null) {
			return FluidContainerRegistry.getContainerCapacity(container);
		} else {
			return FluidContainerRegistry.getContainerCapacity(new FluidStack(fluid, Constants.BUCKET_VOLUME), container);
		}
	}

}
