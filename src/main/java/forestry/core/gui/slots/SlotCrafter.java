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
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.FMLCommonHandler;

import forestry.core.interfaces.ICrafter;

public class SlotCrafter extends SlotCrafting {

	private final ICrafter crafter;
	private final IInventory craftMatrix;

	public SlotCrafter(EntityPlayer player, IInventory craftMatrix, ICrafter crafter, int slot, int xPos, int yPos) {
		super(player, craftMatrix, craftMatrix, slot, xPos, yPos);
		this.crafter = crafter;
		this.craftMatrix = craftMatrix;
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		return false;
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
		return this.crafter.getResult();
	}

	@Override
	public boolean getHasStack() {
		return this.getStack() != null && crafter.canTakeStack(getSlotIndex());
	}

	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack itemStack) {
		InventoryCrafting
		FMLCommonHandler.instance().firePlayerCraftingEvent(player, itemStack, craftMatrix);
		this.onCrafting(itemStack, itemStack.stackSize); // handles crafting achievements, maps, and statistics
		crafter.takenFromSlot(getSlotIndex(), true, player);
	}
}
