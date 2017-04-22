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

import forestry.factory.tiles.ICrafterWorktable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.stats.AchievementList;

public class SlotCrafter extends Slot {

	/**
	 * The craft matrix inventory linked to this result slot.
	 */
	private final IInventory craftMatrix;
	private final ICrafterWorktable crafter;

	/**
	 * The player that is using the GUI where this slot resides.
	 */
	private final EntityPlayer player;
	/**
	 * The number of items that have been crafted so far. Gets passed to ItemStack.onCrafting before being reset.
	 */
	private int amountCrafted;

	public SlotCrafter(EntityPlayer player, IInventory craftMatrix, ICrafterWorktable crafter, int slot, int xPos, int yPos) {
		super(craftMatrix, slot, xPos, yPos);
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
	 * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
	 * Copied from {@link SlotCrafting#onCrafting(ItemStack)}
	 */
	@Override
	protected void onCrafting(ItemStack stack) {
		if (this.amountCrafted > 0) {
			stack.onCrafting(this.player.world, this.player, this.amountCrafted);
			net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(this.player, stack, craftMatrix);
		}

		this.amountCrafted = 0;

		if (stack.getItem() == Item.getItemFromBlock(Blocks.CRAFTING_TABLE)) {
			this.player.addStat(AchievementList.BUILD_WORK_BENCH);
		}

		if (stack.getItem() instanceof ItemPickaxe) {
			this.player.addStat(AchievementList.BUILD_PICKAXE);
		}

		if (stack.getItem() == Item.getItemFromBlock(Blocks.FURNACE)) {
			this.player.addStat(AchievementList.BUILD_FURNACE);
		}

		if (stack.getItem() instanceof ItemHoe) {
			this.player.addStat(AchievementList.BUILD_HOE);
		}

		if (stack.getItem() == Items.BREAD) {
			this.player.addStat(AchievementList.MAKE_BREAD);
		}

		if (stack.getItem() == Items.CAKE) {
			this.player.addStat(AchievementList.BAKE_CAKE);
		}

		if (stack.getItem() instanceof ItemPickaxe && ((ItemPickaxe) stack.getItem()).getToolMaterial() != Item.ToolMaterial.WOOD) {
			this.player.addStat(AchievementList.BUILD_BETTER_PICKAXE);
		}

		if (stack.getItem() instanceof ItemSword) {
			this.player.addStat(AchievementList.BUILD_SWORD);
		}

		if (stack.getItem() == Item.getItemFromBlock(Blocks.ENCHANTING_TABLE)) {
			this.player.addStat(AchievementList.ENCHANTMENTS);
		}

		if (stack.getItem() == Item.getItemFromBlock(Blocks.BOOKSHELF)) {
			this.player.addStat(AchievementList.BOOKCASE);
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
		return crafter.getResult();
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
