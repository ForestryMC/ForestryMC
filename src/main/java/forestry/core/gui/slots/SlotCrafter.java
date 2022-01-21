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

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import forestry.worktable.inventory.CraftingInventoryForestry;
import forestry.worktable.tiles.ICrafterWorktable;

public class SlotCrafter extends Slot {

	/**
	 * The craft matrix inventory linked to this result slot.
	 */
	private final CraftingInventoryForestry craftMatrix;
	private final ICrafterWorktable crafter;

	/**
	 * The player that is using the GUI where this slot resides.
	 */
	private final Player player;
	/**
	 * The number of items that have been crafted so far. Gets passed to ItemStack.onCrafting before being reset.
	 */
	private int amountCrafted;

	public SlotCrafter(Player player, CraftingInventoryForestry craftMatrix, Container craftingDisplay, ICrafterWorktable crafter, int slot, int xPos, int yPos) {
		super(craftingDisplay, slot, xPos, yPos);
		this.craftMatrix = craftMatrix;
		this.crafter = crafter;
		this.player = player;
	}

	/**
	 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
	 */
	@Override
	public boolean mayPlace(ItemStack stack) {
		return false;
	}

	/**
	 * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
	 * internal count then calls onCrafting(item).
	 */
	@Override
	protected void onQuickCraft(ItemStack stack, int amount) {
		this.amountCrafted += amount;
		this.checkTakeAchievements(stack);
	}

	/**
	 * Copied from {@link CraftingResultSlot#onCrafting(ItemStack)}
	 */
	@Override
	protected void checkTakeAchievements(ItemStack stack) {
		if (this.amountCrafted > 0) {
			stack.onCraftedBy(this.player.level, this.player, this.amountCrafted);
			net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerCraftingEvent(this.player, stack, this.craftMatrix);
		}

		if (this.container instanceof RecipeHolder) {
			((RecipeHolder) this.container).awardUsedRecipes(this.player);
		}

		this.amountCrafted = 0;
	}

	@Override
	public ItemStack remove(int amount) {
		if (!this.hasItem()) {
			return ItemStack.EMPTY;
		}

		return this.getItem();
	}

	@Override
	public boolean mayPickup(Player player) {
		return crafter.canTakeStack(getSlotIndex());
	}

	@Override
	public ItemStack getItem() {
		return crafter.getResult(craftMatrix, player.level);
	}

	@Override
	public boolean hasItem() {
		return !getItem().isEmpty() && crafter.canTakeStack(getSlotIndex());
	}

	@Override
	public ItemStack onTake(Player player, ItemStack itemStack) {
		if (crafter.onCraftingStart(player)) {
			this.checkTakeAchievements(itemStack); // handles crafting achievements, maps, and statistics

			crafter.onCraftingComplete(player);
		}
		return itemStack;
	}
}
