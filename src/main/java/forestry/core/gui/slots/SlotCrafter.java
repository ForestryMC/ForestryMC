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
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.stats.AchievementList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import forestry.core.interfaces.ICrafterWorktable;

public class SlotCrafter extends Slot {

	private final IInventory craftMatrix;
	private final ICrafterWorktable crafter;
	private final EntityPlayer player;
	private int amountCrafted;

	public SlotCrafter(EntityPlayer player, IInventory craftMatrix, ICrafterWorktable crafter, int slot, int xPos,
			int yPos) {
		super(craftMatrix, slot, xPos, yPos);
		this.craftMatrix = craftMatrix;
		this.crafter = crafter;
		this.player = player;
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
		this.onCrafting(itemStack); // handles crafting achievements, maps, and
									// statistics

		crafter.onCraftingComplete(player);
	}

	@Override
	protected void onCrafting(ItemStack stack) {
		if (this.amountCrafted > 0) {
			stack.onCrafting(this.player.worldObj, this.player, this.amountCrafted);
		}

		this.amountCrafted = 0;

		if (stack.getItem() == Item.getItemFromBlock(Blocks.crafting_table)) {
			this.player.triggerAchievement(AchievementList.buildWorkBench);
		}

		if (stack.getItem() instanceof ItemPickaxe) {
			this.player.triggerAchievement(AchievementList.buildPickaxe);
		}

		if (stack.getItem() == Item.getItemFromBlock(Blocks.furnace)) {
			this.player.triggerAchievement(AchievementList.buildFurnace);
		}

		if (stack.getItem() instanceof ItemHoe) {
			this.player.triggerAchievement(AchievementList.buildHoe);
		}

		if (stack.getItem() == Items.bread) {
			this.player.triggerAchievement(AchievementList.makeBread);
		}

		if (stack.getItem() == Items.cake) {
			this.player.triggerAchievement(AchievementList.bakeCake);
		}

		if (stack.getItem() instanceof ItemPickaxe
				&& ((ItemPickaxe) stack.getItem()).getToolMaterial() != Item.ToolMaterial.WOOD) {
			this.player.triggerAchievement(AchievementList.buildBetterPickaxe);
		}

		if (stack.getItem() instanceof ItemSword) {
			this.player.triggerAchievement(AchievementList.buildSword);
		}

		if (stack.getItem() == Item.getItemFromBlock(Blocks.enchanting_table)) {
			this.player.triggerAchievement(AchievementList.enchantments);
		}

		if (stack.getItem() == Item.getItemFromBlock(Blocks.bookshelf)) {
			this.player.triggerAchievement(AchievementList.bookcase);
		}

		if (stack.getItem() == Items.golden_apple && stack.getMetadata() == 1) {
			this.player.triggerAchievement(AchievementList.overpowered);
		}
	}

	@Override
	protected void onCrafting(ItemStack stack, int amount) {
		this.amountCrafted += amount;
		this.onCrafting(stack);
	}
}
