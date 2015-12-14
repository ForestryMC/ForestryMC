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
package forestry.core.gui.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.FMLCommonHandler;

import forestry.factory.tiles.ICrafterWorktable;

public class SlotCrafter extends SlotCrafting {

	private final IInventory craftMatrix;
	private final ICrafterWorktable crafter;

	public SlotCrafter(EntityPlayer player, IInventory craftMatrix, ICrafterWorktable crafter, int slot, int xPos, int yPos) {
		super(player, craftMatrix, craftMatrix, slot, xPos, yPos);
		this.craftMatrix = craftMatrix;
		this.crafter = crafter;
	}

	@Override
	public ItemStack decrStackSize(int amount) {
		if (!this.getHasStack()) {
			return null;
		}

		return this.getStack();
	}

	@Override
	public boolean canTakeStack(EntityPlayer player) {
		return crafter.canTakeStack(getSlotIndex());
	}

	@Override
	public ItemStack getStack() {
		return crafter.getResult();
	}

	@Override
	public boolean getHasStack() {
		return getStack() != null && crafter.canTakeStack(getSlotIndex());
	}

	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack itemStack) {
		if (!crafter.onCraftingStart(player)) {
			return;
		}

		FMLCommonHandler.instance().firePlayerCraftingEvent(player, itemStack, craftMatrix);
		this.onCrafting(itemStack); // handles crafting achievements, maps, and statistics

		crafter.onCraftingComplete(player);
	}
}
