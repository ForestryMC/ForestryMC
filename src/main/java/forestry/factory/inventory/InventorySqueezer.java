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
package forestry.factory.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.inventory.InventoryAdapterTile;
import forestry.core.inventory.wrappers.InventoryMapper;
import forestry.core.utils.InventoryUtil;
import forestry.factory.recipes.SqueezerRecipeManager;
import forestry.factory.tiles.TileSqueezer;

public class InventorySqueezer extends InventoryAdapterTile<TileSqueezer> {
	public static final short SLOT_RESOURCE_1 = 0;
	public static final short SLOTS_RESOURCE_COUNT = 9;
	public static final short SLOT_REMNANT = 9;
	public static final short SLOT_REMNANT_COUNT = 1;
	public static final short SLOT_CAN_INPUT = 10;
	public static final short SLOT_CAN_OUTPUT = 11;

	public InventorySqueezer(TileSqueezer squeezer) {
		super(squeezer, 12, "Items");
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if (slotIndex == SLOT_CAN_INPUT) {
			return FluidHelper.isEmptyContainer(itemStack);
		}

		if (slotIndex >= SLOT_RESOURCE_1 && slotIndex < SLOT_RESOURCE_1 + SLOTS_RESOURCE_COUNT) {
			if (FluidHelper.isEmptyContainer(itemStack)) {
				return false;
			}

			if (SqueezerRecipeManager.canUse(itemStack)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemstack, int side) {
		return slotIndex == SLOT_REMNANT || slotIndex == SLOT_CAN_OUTPUT;
	}

	public boolean hasResources() {
		return !InventoryUtil.isEmpty(this, SLOT_RESOURCE_1, SLOTS_RESOURCE_COUNT);
	}

	public ItemStack[] getResources() {
		return InventoryUtil.getStacks(this, SLOT_RESOURCE_1, SLOTS_RESOURCE_COUNT);
	}

	public boolean removeResources(ItemStack[] stacks, EntityPlayer player) {
		IInventory inventory = new InventoryMapper(this, SLOT_RESOURCE_1, SLOTS_RESOURCE_COUNT);
		return InventoryUtil.removeSets(inventory, 1, stacks, player, false, true, false, true);
	}

	public boolean addRemnant(ItemStack remnant, boolean doAdd) {
		return InventoryUtil.tryAddStack(this, remnant, SLOT_REMNANT, SLOT_REMNANT_COUNT, true, doAdd);
	}

	public void fillContainers(FluidStack fluidStack, TankManager tankManager) {
		if (getStackInSlot(SLOT_CAN_INPUT) == null || fluidStack == null) {
			return;
		}
		FluidHelper.fillContainers(tankManager, this, SLOT_CAN_INPUT, SLOT_CAN_OUTPUT, fluidStack.getFluid());
	}
}
