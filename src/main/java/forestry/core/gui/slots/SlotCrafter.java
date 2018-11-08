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

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import forestry.worktable.inventory.InventoryCraftingForestry;
import forestry.worktable.tiles.ICrafterWorktable;

public class SlotCrafter extends Slot {

	/**
	 * The craft matrix inventory linked to this result slot.
	 */
	private final InventoryCraftingForestry craftMatrix;
	private final ICrafterWorktable crafter;

	/**
	 * The player that is using the GUI where this slot resides.
	 */
	private final EntityPlayer player;
	/**
	 * The number of items that have been crafted so far. Gets passed to ItemStack.onCrafting before being reset.
	 */
	private int amountCrafted;

	public SlotCrafter(EntityPlayer player, InventoryCraftingForestry craftMatrix, IInventory craftingDisplay, ICrafterWorktable crafter, int slot, int xPos, int yPos) {
		super(craftingDisplay, slot, xPos, yPos);
		this.craftMatrix = craftMatrix;
		this.crafter = crafter;
		this.player = player;
	}

	/**
	 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
	 */
	@Override
	public boolean isItemValid(ItemStack stack) {
		return false;
	}

	/**
	 * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
	 * internal count then calls onCrafting(item).
	 */
	@Override
	protected void onCrafting(ItemStack stack, int amount) {
		this.amountCrafted += amount;
		this.onCrafting(stack);
	}

	/**
	 * Copied from {@link SlotCrafting#onCrafting(ItemStack)}
	 */
	@Override
	protected void onCrafting(ItemStack stack) {
		if (this.amountCrafted > 0) {
			stack.onCrafting(this.player.world, this.player, this.amountCrafted);
			net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(this.player, stack, craftMatrix);
		}

		this.amountCrafted = 0;
		IRecipe irecipe = crafter.getRecipeUsed();
		if (irecipe != null && !irecipe.isDynamic()) {
			this.player.unlockRecipes(Lists.newArrayList(irecipe));
		}
	}

	@Override
	public ItemStack decrStackSize(int amount) {
		if (!this.getHasStack()) {
			return ItemStack.EMPTY;
		}

		return this.getStack();
	}

	@Override
	public boolean canTakeStack(EntityPlayer player) {
		return crafter.canTakeStack(getSlotIndex());
	}

	@Override
	public ItemStack getStack() {
		return crafter.getResult(craftMatrix, player.world);
	}

	@Override
	public boolean getHasStack() {
		return !getStack().isEmpty() && crafter.canTakeStack(getSlotIndex());
	}

	@Override
	public ItemStack onTake(EntityPlayer player, ItemStack itemStack) {
		if (crafter.onCraftingStart(player)) {
			this.onCrafting(itemStack); // handles crafting achievements, maps, and statistics

			crafter.onCraftingComplete(player);
		}
		return itemStack;
	}
}
