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
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

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
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class FluidHelper {

	public static final int BUCKET_FILL_TIME = 8;
	public static final int NETWORK_UPDATE_INTERVAL = 128;
	public static final int BUCKET_VOLUME = 1000;

	private FluidHelper() {
	}

	public static boolean handleRightClick(IFluidHandler tank, EnumFacing side, EntityPlayer player, boolean fill, boolean drain) {
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

		if (input == null) {
			return;
		}

		if (processFilled && drainContainers(fluidHandler, inv, inputSlot, outputSlot)) {
			return;
		}

		if (processEmpty && fluidToFill != null) {
			fillContainers(fluidHandler, inv, inputSlot, outputSlot, fluidToFill);
		}
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
			FluidStack drain = fluidHandler.drain(null, fluidInContainer, false);
			if (drain != null && drain.amount == fluidInContainer.amount) {
				if (doFill) {
					fluidHandler.drain(null, fluidInContainer, true);
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

		int used = fluidHandler.fill(null, fluidInContainer, false);
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

		fluidHandler.fill(null, fluidInContainer, true);

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
		if (stack != null && stack.getItem() instanceof IFluidContainerItem) {
			return true;
		}
		return FluidContainerRegistry.isContainer(stack);
	}

	public static boolean isFilledContainer(ItemStack stack) {
		if (FluidContainerRegistry.isFilledContainer(stack)) {
			return true;
		}

		Item item = stack.getItem();
		if (item instanceof IFluidContainerItem) {
			IFluidContainerItem containerItem = (IFluidContainerItem) item;
			FluidStack fluidStack = containerItem.getFluid(stack);
			return fluidStack != null && fluidStack.amount > 0;
		}

		return false;
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

	public static FluidStack drainBlock(World world, BlockPos pos, boolean doDrain) {
		return drainBlock(world.getBlockState(pos), world, pos, doDrain);
	}

	public static FluidStack drainBlock(IBlockState state, World world, BlockPos pos, boolean doDrain) {
		Block block = state.getBlock();
		int meta = block.getMetaFromState(state);
		if (block instanceof IFluidBlock) {
			IFluidBlock fluidBlock = (IFluidBlock) block;
			if (fluidBlock.canDrain(world, pos)) {
				return fluidBlock.drain(world, pos, doDrain);
			}
		} else if (block == Blocks.water || block == Blocks.flowing_water) {
			if (meta != 0) {
				return null;
			}
			if (doDrain) {
				world.setBlockToAir(pos);
			}
			return new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME);
		} else if (block == Blocks.lava || block == Blocks.flowing_lava) {
			if (meta != 0) {
				return null;
			}
			if (doDrain) {
				world.setBlockToAir(pos);
			}
			return new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME);
		}
		return null;
	}

	public static boolean isFullFluidBlock(World world, BlockPos pos) {
		return isFullFluidBlock(world.getBlockState(pos), world, pos);
	}

	public static boolean isFullFluidBlock(IBlockState state, World world, BlockPos pos) {
		Block block = state.getBlock();
		if (block instanceof BlockLiquid || block instanceof IFluidBlock) {
			return block.getMetaFromState(state) == 0;
		}
		return false;
	}

	public static Fluid getFluid(Block block) {
		if (block instanceof IFluidBlock) {
			return ((IFluidBlock) block).getFluid();
		} else if (block == Blocks.water || block == Blocks.flowing_water) {
			return FluidRegistry.WATER;
		} else if (block == Blocks.lava || block == Blocks.flowing_lava) {
			return FluidRegistry.LAVA;
		}
		return null;
	}
}
