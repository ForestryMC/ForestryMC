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
package forestry.factory.tiles;

import javax.annotation.Nullable;
import java.io.IOException;

import com.google.common.base.Preconditions;
import forestry.core.inventory.InventoryAdapterTile;
import forestry.core.inventory.wrappers.InventoryMapper;
import forestry.core.network.PacketBufferForestry;
import forestry.core.recipes.RecipeUtil;
import forestry.core.tiles.TileBase;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.factory.gui.ContainerWorktable;
import forestry.factory.gui.GuiWorktable;
import forestry.factory.inventory.InventoryCraftingForestry;
import forestry.factory.inventory.InventoryGhostCrafting;
import forestry.factory.inventory.InventoryWorktable;
import forestry.factory.recipes.MemorizedRecipe;
import forestry.factory.recipes.RecipeMemory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeHooks;

public class TileWorktable extends TileBase implements ICrafterWorktable {
	private RecipeMemory recipeMemory;
	private final InventoryAdapterTile craftingDisplay;
	@Nullable
	private MemorizedRecipe currentRecipe;

	public TileWorktable() {
		setInternalInventory(new InventoryWorktable(this));

		craftingDisplay = new InventoryGhostCrafting<>(this, 10);
		recipeMemory = new RecipeMemory();
	}

	/* LOADING & SAVING */

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);

		craftingDisplay.writeToNBT(nbttagcompound);
		recipeMemory.writeToNBT(nbttagcompound);
		return nbttagcompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		craftingDisplay.readFromNBT(nbttagcompound);
		recipeMemory = new RecipeMemory(nbttagcompound);
	}

	/* NETWORK */
	@Override
	public void writeData(PacketBufferForestry data) {
		super.writeData(data);

		craftingDisplay.writeData(data);
		recipeMemory.writeData(data);
	}

	@Override
	public void readData(PacketBufferForestry data) throws IOException {
		super.readData(data);

		craftingDisplay.readData(data);
		recipeMemory.readData(data);
	}

	@Override
	public void validate() {
		super.validate();
		recipeMemory.validate(world);
	}

	/* Recipe Conflicts */
	public boolean hasRecipeConflict() {
		return currentRecipe != null && currentRecipe.hasRecipeConflict();
	}

	public void chooseNextConflictRecipe() {
		if (currentRecipe != null) {
			currentRecipe.incrementRecipe();
		}
	}

	public void choosePreviousConflictRecipe() {
		if (currentRecipe != null) {
			currentRecipe.decrementRecipe();
		}
	}

	/* ICrafterWorktable */
	@Override
	public boolean canTakeStack(int craftingSlotIndex) {
		return craftingSlotIndex != InventoryGhostCrafting.SLOT_CRAFTING_RESULT ||
				canCraftCurrentRecipe();
	}

	private boolean canCraftCurrentRecipe() {
		return craftRecipe(true);
	}

	@Override
	public boolean onCraftingStart(EntityPlayer player) {
		return craftRecipe(false);
	}

	private boolean craftRecipe(boolean simulate) {
		if (currentRecipe == null) {
			return false;
		}

		NonNullList<ItemStack> recipeItems = InventoryUtil.getStacks(currentRecipe.getCraftMatrix());
		NonNullList<ItemStack> inventoryStacks = InventoryUtil.getStacks(this);
		InventoryCraftingForestry crafting = RecipeUtil.getCraftRecipe(recipeItems, inventoryStacks, world, currentRecipe.getRecipeOutput());
		if (crafting == null) {
			return false;
		}

		recipeItems = InventoryUtil.getStacks(crafting);

		IInventory inventory;
		if (simulate) {
			inventory = new InventoryBasic("copy", false, this.getSizeInventory());
			InventoryUtil.deepCopyInventoryContents(this, inventory);
		} else {
			inventory = this;
		}

		// craft recipe should exactly match ingredients here, so no oreDict or tool matching
		NonNullList<ItemStack> removed = InventoryUtil.removeSets(inventory, 1, recipeItems, null, false, false, false);
		if (removed == null) {
			return false;
		}

		if (!simulate) {
			// update crafting display to match the ingredients that were actually used
			setCraftingDisplay(crafting);
		}

		return true;
	}

	@Override
	public void onCraftingComplete(EntityPlayer player) {
		Preconditions.checkState(currentRecipe != null);

		ForgeHooks.setCraftingPlayer(player);
		NonNullList<ItemStack> remainingItems = CraftingManager.getInstance().getRemainingItems(currentRecipe.getCraftMatrix(), player.world);
		ForgeHooks.setCraftingPlayer(null);

		for (ItemStack remainingItem : remainingItems) {
			if (remainingItem != null) {
				if (!InventoryUtil.tryAddStack(this, remainingItem, true)) {
					player.dropItem(remainingItem, false);
				}
			}
		}

		if (!world.isRemote) {
			recipeMemory.memorizeRecipe(world.getTotalWorldTime(), currentRecipe);
		}
	}

	@Override
	public ItemStack getResult() {
		if (currentRecipe == null) {
			return ItemStack.EMPTY;
		}

		ItemStack result = currentRecipe.getRecipeOutput();
		if (!result.isEmpty()) {
			result = result.copy();
		}
		return result;
	}

	/* Crafting Container methods */
	public RecipeMemory getMemory() {
		return recipeMemory;
	}

	public void chooseRecipeMemory(int recipeIndex) {
		MemorizedRecipe recipe = recipeMemory.getRecipe(recipeIndex);
		setCurrentRecipe(recipe);
	}

	private void setCraftingDisplay(IInventory craftMatrix) {
		for (int slot = 0; slot < craftMatrix.getSizeInventory(); slot++) {
			craftingDisplay.setInventorySlotContents(slot, craftMatrix.getStackInSlot(slot));
		}
	}

	public IInventory getCraftingDisplay() {
		return new InventoryMapper(craftingDisplay, InventoryGhostCrafting.SLOT_CRAFTING_1, InventoryGhostCrafting.SLOT_CRAFTING_COUNT);
	}

	public void clearCraftMatrix() {
		for (int slot = 0; slot < craftingDisplay.getSizeInventory(); slot++) {
			craftingDisplay.setInventorySlotContents(slot, ItemStack.EMPTY);
		}
	}

	public void setCurrentRecipe(InventoryCraftingForestry crafting) {
		NonNullList<ItemStack> recipeOutputs = RecipeUtil.findMatchingRecipes(crafting, world);
		MemorizedRecipe recipe = recipeOutputs.isEmpty() ? null : new MemorizedRecipe(crafting, recipeOutputs);

		if (currentRecipe != null && recipe != null) {
			if (recipe.hasRecipeOutput(currentRecipe.getRecipeOutput())) {
				NonNullList<ItemStack> stacks = InventoryUtil.getStacks(crafting);
				NonNullList<ItemStack> currentStacks = InventoryUtil.getStacks(currentRecipe.getCraftMatrix());
				if (ItemStackUtil.equalSets(stacks, currentStacks)) {
					return;
				}
			}
		}

		setCurrentRecipe(recipe);
	}

	/* Network Sync with PacketWorktableRecipeUpdate */
	@Nullable
	public MemorizedRecipe getCurrentRecipe() {
		return currentRecipe;
	}

	public void setCurrentRecipe(@Nullable MemorizedRecipe recipe) {
		this.currentRecipe = recipe;
		if (currentRecipe != null) {
			setCraftingDisplay(currentRecipe.getCraftMatrix());
		}
	}

	@Override
	public Object getGui(EntityPlayer player, int data) {
		return new GuiWorktable(player, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerWorktable(player, this);
	}
}
