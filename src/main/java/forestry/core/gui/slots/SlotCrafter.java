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

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

import forestry.worktable.inventory.CraftingInventoryForestry;
import forestry.worktable.tiles.ICrafterWorktable;

public class SlotCrafter extends Slot {

	/**
	 * The craft matrix inventory linked to this result slot.
	 */
	private final CraftingInventoryForestry craftSlots;
	private final ICrafterWorktable crafter;

	/**
	 * The player that is using the GUI where this slot resides.
	 */
	private final Player player;
	/**
	 * The number of items that have been crafted so far. Gets passed to ItemStack.onCrafting before being reset.
	 */
	private int removeCount;

	public SlotCrafter(Player player, CraftingInventoryForestry craftSlots, Container craftingDisplay, ICrafterWorktable crafter, int slot, int xPos, int yPos) {
		super(craftingDisplay, slot, xPos, yPos);
		this.craftSlots = craftSlots;
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
		this.removeCount += amount;
		this.checkTakeAchievements(stack);
	}

	/**
	 * Copied from {@link net.minecraft.world.inventory.ResultSlot#checkTakeAchievements(ItemStack)}
	 */
	@Override
	protected void checkTakeAchievements(ItemStack stack) {
		if (this.removeCount > 0) {
			stack.onCraftedBy(this.player.level, this.player, this.removeCount);
			net.minecraftforge.event.ForgeEventFactory.firePlayerCraftingEvent(this.player, stack, this.craftSlots);
		}

		if (this.container instanceof RecipeHolder holder) {
			holder.awardUsedRecipes(this.player);
		}

		this.removeCount = 0;
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
		return crafter.getResult(craftSlots, player.level);
	}

	@Override
	public boolean hasItem() {
		return !getItem().isEmpty() && crafter.canTakeStack(getSlotIndex());
	}

	@Override
	public void onTake(Player player, ItemStack itemStack) {
		this.checkTakeAchievements(itemStack);
		net.minecraftforge.common.ForgeHooks.setCraftingPlayer(player);
		NonNullList<ItemStack> list = player.level.getRecipeManager().getRemainingItemsFor(RecipeType.CRAFTING, this.craftSlots, player.level);
		net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);
		for (int i = 0; i < list.size(); ++i) {
			ItemStack itemstack = this.craftSlots.getItem(i);
			ItemStack itemstack1 = list.get(i);
			if (!itemstack.isEmpty()) {
				this.craftSlots.removeItem(i, 1);
				itemstack = this.craftSlots.getItem(i);
			}

			if (!itemstack1.isEmpty()) {
				if (itemstack.isEmpty()) {
					this.craftSlots.setItem(i, itemstack1);
				} else if (ItemStack.isSame(itemstack, itemstack1) && ItemStack.tagMatches(itemstack, itemstack1)) {
					itemstack1.grow(itemstack.getCount());
					this.craftSlots.setItem(i, itemstack1);
				} else if (!this.player.getInventory().add(itemstack1)) {
					this.player.drop(itemstack1, false);
				}
			}
		}
	}
}
