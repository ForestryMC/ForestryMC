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

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

import forestry.core.fluids.tanks.StandardTank;
import forestry.core.inventory.InvTools;
import forestry.core.utils.StackUtils;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class FluidHelper {

	public static final int BUCKET_FILL_TIME = 8;
	public static final int NETWORK_UPDATE_INTERVAL = 128;
	public static final int BUCKET_VOLUME = 1000;

	private FluidHelper() {
	}

	public static boolean handleRightClick(IFluidHandler tank, ForgeDirection side, EntityPlayer player, boolean fill, boolean drain) {
		if (player == null)
			return false;
		ItemStack current = player.inventory.getCurrentItem();
		if (current != null) {

			FluidStack liquid = getFluidStackInContainer(current);

			if (fill && liquid != null) {
				int used = tank.fill(side, liquid, true);

				if (used > 0) {
					if (!player.capabilities.isCreativeMode) {
						player.inventory.setInventorySlotContents(player.inventory.currentItem, InvTools.depleteItem(current));
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
							if (!player.inventory.addItemStackToInventory(filled))
								return false;
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

	public static void processContainers(StandardTank tank, IInventory inv, int inputSlot, int outputSlot) {
		processContainers(tank, inv, inputSlot, outputSlot, tank.getFluidType(), true, true);
	}

	public static void processContainers(StandardTank tank, IInventory inv, int inputSlot, int outputSlot, Fluid fluidToFill, boolean processFilled, boolean processEmpty) {
		TankManager tankManger = new TankManager();
		tankManger.add(tank);
		processContainers(tankManger, inv, inputSlot, outputSlot, fluidToFill, processFilled, processEmpty);
	}

	public static void processContainers(TankManager tank, IInventory inv, int inputSlot, int outputSlot, Fluid fluidToFill) {
		processContainers(tank, inv, inputSlot, outputSlot, fluidToFill, true, true);
	}

	public static void processContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot, Fluid fluidToFill, boolean processFilled, boolean processEmpty) {
		ItemStack input = inv.getStackInSlot(inputSlot);

		if (input == null)
			return;

		if (processFilled && drainContainers(fluidHandler, inv, inputSlot, outputSlot))
			return;

		if (processEmpty && fluidToFill != null)
			fillContainers(fluidHandler, inv, inputSlot, outputSlot, fluidToFill);
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
					if (output == null)
						inv.setInventorySlotContents(outputSlot, filled);
					else
						output.stackSize++;
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

		if (input == null)
			return false;

		FluidStack fluidInContainer = getFluidStackInContainer(input);
		if (fluidInContainer == null)
			return false;

		int used = fluidHandler.fill(ForgeDirection.UNKNOWN, fluidInContainer, false);
		if (used < fluidInContainer.amount)
			return false;

		ItemStack emptyItem = getEmptyContainer(input);

		if (output != null && emptyItem != null) {
			if (outputSlot == inputSlot) {
				if (input.stackSize > 1)
					return false;
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

	public static boolean isContainer(ItemStack stack) {
		if (stack != null && stack.getItem() instanceof IFluidContainerItem)
			return true;
		return FluidContainerRegistry.isContainer(stack);
	}

	public static boolean isFilledContainer(ItemStack stack) {
		if (FluidContainerRegistry.isFilledContainer(stack))
			return true;

		Item item = stack.getItem();
		if (item instanceof IFluidContainerItem) {
			IFluidContainerItem containerItem = (IFluidContainerItem)item;
			FluidStack fluidStack = containerItem.getFluid(stack);
			return fluidStack != null && fluidStack.amount > 0;
		}

		return false;
	}

	public static boolean isFillableContainer(ItemStack empty, FluidStack liquid) {
		Item item = empty.getItem();
		if (item instanceof IFluidContainerItem) {
			IFluidContainerItem containerItem = (IFluidContainerItem)item;
			return containerItem.fill(empty, liquid, false) > 0;
		}

		for (FluidContainerData cont : FluidContainerRegistry.getRegisteredFluidContainerData()) {
			if (cont.fluid.isFluidEqual(liquid) && (cont.emptyContainer != null && cont.emptyContainer.isItemEqual(empty)))
				return true;
		}

		return false;
	}

	public static boolean isEmptyContainer(ItemStack empty) {

		if (FluidContainerRegistry.isEmptyContainer(empty))
			return true;

		Item item = empty.getItem();
		if (item instanceof IFluidContainerItem) {
			IFluidContainerItem containerItem = (IFluidContainerItem)item;
			FluidStack fluid = containerItem.getFluid(empty);
			return fluid == null || fluid.amount == 0;
		}

		return false;
	}

	public static ItemStack getEmptyContainer(ItemStack container) {
		Item item = container.getItem();
		if (item == null)
			return null;

		return item.getContainerItem(container);
	}

	public static ItemStack getFilledContainer(Fluid fluid, ItemStack empty) {
		if (fluid == null)
			return null;

		FluidStack fluidToFill = new FluidStack(fluid, Integer.MAX_VALUE);

		return getFilledContainer(fluidToFill, empty);
	}

	public static ItemStack getFilledContainer(FluidStack liquid, ItemStack empty) {
		if (liquid == null || empty == null)
			return null;

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

	public static boolean isFluidEqual(FluidStack L1, FluidStack L2) {
		if (L1 == null || L2 == null)
			return false;
		return L1.isFluidEqual(L2);
	}

	public static FluidStack drainBlock(World world, int x, int y, int z, boolean doDrain) {
		return drainBlock(world.getBlock(x, y, z), world, x, y, z, doDrain);
	}

	public static FluidStack drainBlock(Block block, World world, int x, int y, int z, boolean doDrain) {
		if (block instanceof IFluidBlock) {
			IFluidBlock fluidBlock = (IFluidBlock) block;
			if (fluidBlock.canDrain(world, x, y, z))
				return fluidBlock.drain(world, x, y, z, doDrain);
		} else if (block == Blocks.water || block == Blocks.flowing_water) {
			int meta = world.getBlockMetadata(x, y, z);
			if (meta != 0)
				return null;
			if (doDrain)
				world.setBlockToAir(x, y, z);
			return new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME);
		} else if (block == Blocks.lava || block == Blocks.flowing_lava) {
			int meta = world.getBlockMetadata(x, y, z);
			if (meta != 0)
				return null;
			if (doDrain)
				world.setBlockToAir(x, y, z);
			return new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME);
		}
		return null;
	}

	public static boolean isFullFluidBlock(World world, int x, int y, int z) {
		return isFullFluidBlock(world.getBlock(x, y, z), world, x, y, z);
	}

	public static boolean isFullFluidBlock(Block block, World world, int x, int y, int z) {
		if (block instanceof BlockLiquid || block instanceof IFluidBlock)
			return world.getBlockMetadata(x, y, z) == 0;
		return false;
	}

	public static Fluid getFluid(Block block) {
		if (block instanceof IFluidBlock)
			return ((IFluidBlock) block).getFluid();
		else if (block == Blocks.water || block == Blocks.flowing_water)
			return FluidRegistry.WATER;
		else if (block == Blocks.lava || block == Blocks.flowing_lava)
			return FluidRegistry.LAVA;
		return null;
	}

}
