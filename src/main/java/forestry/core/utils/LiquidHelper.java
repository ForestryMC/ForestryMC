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
package forestry.core.utils;

import java.util.HashMap;
import java.util.LinkedList;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.common.registry.LanguageRegistry;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.render.TextureManager;

public class LiquidHelper {

	private static final LinkedList<String> myLiquids = new LinkedList<String>();
	private static final HashMap<String, Fluid> liquidMap = new HashMap<String, Fluid>();

	public static boolean isEmptyLiquidData() {
		return FluidContainerRegistry.getRegisteredFluidContainerData().length <= 0;
	}

	public static boolean isEmptyContainer(ItemStack empty) {
		return FluidContainerRegistry.isEmptyContainer(empty);
	}

	public static FluidContainerData getEmptyContainer(ItemStack empty, FluidStack liquid) {
		for (FluidContainerData cont : FluidContainerRegistry.getRegisteredFluidContainerData()) {
			if (cont.fluid.isFluidEqual(liquid) && (cont.emptyContainer != null && cont.emptyContainer.isItemEqual(empty)))
				return cont;
		}

		return null;
	}

	public static FluidContainerData getLiquidContainer(ItemStack container) {
		for (FluidContainerData cont : FluidContainerRegistry.getRegisteredFluidContainerData()) {
			if (cont.filledContainer.isItemEqual(container))
				return cont;
		}
		return null;
	}

	public static FluidContainerData createLiquidData(String ident, FluidStack stillLiquid, ItemStack filled, ItemStack container) {
		return new FluidContainerData(new FluidStack(getOrCreateLiquid(ident), Defaults.BUCKET_VOLUME), filled, container);
	}

	public static boolean isLiquid(String ident, FluidStack stack) {
		return FluidRegistry.getFluidID(ident) == stack.fluidID;
	}

	public static boolean exists(String ident) {
		return FluidRegistry.isFluidRegistered(ident);
	}

	public static Fluid getFluid(String ident) {
		Fluid fluid = FluidRegistry.getFluid(ident);
		return fluid;
	}

	public static Fluid getOrCreateLiquid(String ident) {
		if (!FluidRegistry.isFluidRegistered(ident)) {
			Fluid fluid = new Fluid(ident);
			FluidRegistry.registerFluid(fluid);
			myLiquids.add(ident);
		}
		liquidMap.put(ident, FluidRegistry.getFluid(ident));
		return FluidRegistry.getFluid(ident);
	}

	public static FluidStack getLiquid(String name, int amount) {
		return FluidRegistry.getFluidStack(name, amount);
	}

	public static void injectLiquidContainer(String name, int volume, ItemStack filled, ItemStack empty) {
		injectLiquidContainer(name, volume, filled, empty, null, 0);
	}

	public static void injectWaxContainer(String name, int volume, ItemStack filled, ItemStack empty) {
		injectLiquidContainer(name, volume, filled, empty, ForestryItem.beeswax.getItemStack(), 10);
	}

	public static void injectRefractoryContainer(String name, int volume, ItemStack filled, ItemStack empty) {
		injectLiquidContainer(name, volume, filled, empty, ForestryItem.refractoryWax.getItemStack(), 10);
	}

	public static void injectTinContainer(String name, int volume, ItemStack filled, ItemStack empty) {
		injectLiquidContainer(name, volume, filled, empty, ForestryItem.ingotTin.getItemStack(), 5);
	}

	public static void injectLiquidContainer(String name, int volume, ItemStack filled, ItemStack empty, ItemStack remnant, int chance) {
		FluidStack contained = getLiquid(name, volume);
		if (contained == null)
			throw new IllegalArgumentException(String.format("Attempted to inject a liquid container for the non-existent liquid '%s'.", name));

		FluidContainerData container = new FluidContainerData(contained, filled, empty);
		FluidContainerRegistry.registerFluidContainer(container);

		if (RecipeManagers.squeezerManager != null)
			if (!container.filledContainer.getItem().hasContainerItem(container.filledContainer))
				if (remnant != null)
					RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { container.filledContainer }, container.fluid, remnant, chance);
				else
					RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { container.filledContainer }, container.fluid);
	}

	public static void resetLiquidIcons(IIconRegister register) {
		for (String fluidString : myLiquids) {
			Fluid fluid = getFluid(fluidString);
			IIcon icon = TextureManager.getInstance().registerTex(register, "liquid/" + fluid.getName());
			fluid.setIcons(icon);

		}
	}

	public static boolean handleRightClick(IFluidHandler tank, ForgeDirection side, EntityPlayer player, boolean fill, boolean drain) {
		if (player == null) {
			return false;
		}
		ItemStack current = player.inventory.getCurrentItem();
		if (current != null) {

			FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(current);

			if (fill && liquid != null) {
				int used = tank.fill(side, liquid, true);

				if (used > 0) {
					if (!player.capabilities.isCreativeMode) {
						player.inventory.setInventorySlotContents(player.inventory.currentItem, StackUtils.consumeItem(current));
						player.inventory.markDirty();
					}
					return true;
				}

			} else if (drain) {

				FluidStack available = tank.drain(side, Integer.MAX_VALUE, false);
				if (available != null) {
					ItemStack filled = FluidContainerRegistry.fillFluidContainer(available, current);

					liquid = FluidContainerRegistry.getFluidForFilledItem(filled);
					if (liquid != null) {

						if (current.stackSize > 1) {
							if (!player.inventory.addItemStackToInventory(filled))
								return false;
							player.inventory.setInventorySlotContents(player.inventory.currentItem, StackUtils.consumeItem(current));
							player.inventory.markDirty();
						} else {
							player.inventory.setInventorySlotContents(player.inventory.currentItem, StackUtils.consumeItem(current));
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
		if (filled != null && (output == null || (output.stackSize < output.getMaxStackSize() && StackUtils.isIdenticalItem(filled, output)))) {
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

	public static boolean drainContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot) {
		ItemStack input = inv.getStackInSlot(inputSlot);
		ItemStack output = inv.getStackInSlot(outputSlot);

		if (input != null) {
			FluidStack fluidInContainer = getFluidStackInContainer(input);
			ItemStack emptyItem = input.getItem().getContainerItem(input);
			if (fluidInContainer != null && (emptyItem == null || output == null || (output.stackSize < output.getMaxStackSize() && StackUtils.isIdenticalItem(output, emptyItem)))) {
				int used = fluidHandler.fill(ForgeDirection.UNKNOWN, fluidInContainer, false);
				if (used >= fluidInContainer.amount) {
					fluidHandler.fill(ForgeDirection.UNKNOWN, fluidInContainer, true);
					if (emptyItem != null)
						if (output == null)
							inv.setInventorySlotContents(outputSlot, emptyItem);
						else
							output.stackSize++;
					inv.decrStackSize(inputSlot, 1);
					return true;
				}
			}
		}
		return false;
	}

	public static ItemStack getFilledContainer(Fluid fluid, ItemStack empty) {
		if (fluid == null || empty == null)
			return null;
		return FluidContainerRegistry.fillFluidContainer(new FluidStack(fluid, Integer.MAX_VALUE), empty);
	}

	public static FluidStack getFluidStackInContainer(ItemStack stack) {
		return FluidContainerRegistry.getFluidForFilledItem(stack);
	}

	public static boolean containsFluidStack(ItemStack stack, FluidStack fluidStack) {
		return FluidContainerRegistry.containsFluid(stack, fluidStack);
	}

	public static boolean containsFluid(ItemStack stack, Fluid fluid) {
		return FluidContainerRegistry.containsFluid(stack, new FluidStack(fluid, 1));
	}
}
