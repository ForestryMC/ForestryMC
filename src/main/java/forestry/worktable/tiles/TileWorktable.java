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
package forestry.worktable.tiles;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;

import forestry.core.inventory.InventoryAdapterTile;
import forestry.core.inventory.InventoryGhostCrafting;
import forestry.core.inventory.wrappers.InventoryMapper;
import forestry.core.network.PacketBufferForestry;
import forestry.core.tiles.TileBase;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.RecipeUtils;
import forestry.worktable.features.WorktableTiles;
import forestry.worktable.gui.ContainerWorktable;
import forestry.worktable.inventory.CraftingInventoryForestry;
import forestry.worktable.inventory.InventoryWorktable;
import forestry.worktable.recipes.MemorizedRecipe;
import forestry.worktable.recipes.RecipeMemory;

public class TileWorktable extends TileBase implements ICrafterWorktable {

	private RecipeMemory recipeMemory;
	private final InventoryAdapterTile craftingDisplay;
	@Nullable
	private MemorizedRecipe currentRecipe;

	public TileWorktable() {
		super(WorktableTiles.WORKTABLE.tileType());
		setInternalInventory(new InventoryWorktable(this));

		craftingDisplay = new InventoryGhostCrafting<>(this, 10);
		recipeMemory = new RecipeMemory();
	}

	/* LOADING & SAVING */

	@Override
	public CompoundNBT save(CompoundNBT compoundNBT) {
		compoundNBT = super.save(compoundNBT);

		craftingDisplay.write(compoundNBT);
		recipeMemory.write(compoundNBT);
		return compoundNBT;
	}

	@Override
	public void load(BlockState state, CompoundNBT compoundNBT) {
		super.load(state, compoundNBT);

		craftingDisplay.read(compoundNBT);
		recipeMemory = new RecipeMemory(compoundNBT);
	}

	/* NETWORK */
	@Override
	public void writeData(PacketBufferForestry data) {
		super.writeData(data);

		craftingDisplay.writeData(data);
		recipeMemory.writeData(data);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void readData(PacketBufferForestry data) throws IOException {
		super.readData(data);

		craftingDisplay.readData(data);
		recipeMemory.readData(data);
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

	@Override
	public ItemStack getResult(CraftingInventory inventory, World world) {
		if (currentRecipe != null) {
			return currentRecipe.getCraftingResult(inventory, world);
		}
		return ItemStack.EMPTY;
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
	public boolean onCraftingStart(PlayerEntity player) {
		return craftRecipe(false);
	}

	private boolean craftRecipe(boolean simulate) {
		if (currentRecipe == null) {
			return false;
		}

		ICraftingRecipe selectedRecipe = currentRecipe.getSelectedRecipe(level);
		if (selectedRecipe == null) {
			return false;
		}

		IInventory inventory = new InventoryMapper(this, InventoryWorktable.SLOT_INVENTORY_1, InventoryWorktable.SLOT_INVENTORY_COUNT);
		if (!InventoryUtil.consumeIngredients(inventory, selectedRecipe.getIngredients(), null, true, false, !simulate)) {
			return false;
		}

		if (!simulate) {
			// update crafting display to match the ingredients that were actually used
			//currentRecipe.setCraftMatrix(crafting);
			setCurrentRecipe(currentRecipe);
		}

		return true;
	}

	@Override
	public void onCraftingComplete(PlayerEntity player) {
		Preconditions.checkNotNull(currentRecipe);
		ICraftingRecipe selectedRecipe = currentRecipe.getSelectedRecipe(level);
		Preconditions.checkNotNull(selectedRecipe);

		ForgeHooks.setCraftingPlayer(player);
		CraftingInventoryForestry craftMatrix = currentRecipe.getCraftMatrix();
		NonNullList<ItemStack> remainingItems = selectedRecipe.getRemainingItems(craftMatrix.copy());
		ForgeHooks.setCraftingPlayer(null);

		for (ItemStack remainingItem : remainingItems) {
			if (remainingItem != null && !remainingItem.isEmpty()) {
				if (!InventoryUtil.tryAddStack(this, remainingItem, true)) {
					player.drop(remainingItem, false);
				}
			}
		}

		if (!level.isClientSide) {
			recipeMemory.memorizeRecipe(level.getGameTime(), currentRecipe, level);
		}
	}

	@Nullable
	@Override
	public ICraftingRecipe getRecipeUsed() {
		if (currentRecipe == null) {
			return null;
		}

		return currentRecipe.getSelectedRecipe(level);
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
		for (int slot = 0; slot < craftMatrix.getContainerSize(); slot++) {
			ItemStack stack = craftMatrix.getItem(slot);
			craftingDisplay.setItem(slot, stack.copy());
		}
	}

	public IInventory getCraftingDisplay() {
		return new InventoryMapper(craftingDisplay, InventoryGhostCrafting.SLOT_CRAFTING_1, InventoryGhostCrafting.SLOT_CRAFTING_COUNT);
	}

	public void clearCraftMatrix() {
		for (int slot = 0; slot < craftingDisplay.getContainerSize(); slot++) {
			craftingDisplay.setItem(slot, ItemStack.EMPTY);
		}
	}

	public void setCurrentRecipe(CraftingInventoryForestry crafting) {
		List<ICraftingRecipe> recipes = RecipeUtils.getRecipes(IRecipeType.CRAFTING, crafting, level);
		MemorizedRecipe recipe = recipes.isEmpty() ? null : new MemorizedRecipe(crafting, recipes);

		if (currentRecipe != null && recipe != null) {
			//TODO: Find a new way to find unneeded updates
			if (recipe.hasRecipe(currentRecipe.getSelectedRecipe(level), level)) {
				/*NonNullList<ItemStack> stacks = InventoryUtil.getStacks(crafting);
				NonNullList<ItemStack> currentStacks = InventoryUtil.getStacks(currentRecipe.getCraftMatrix());
				if (ItemStackUtil.equalSets(stacks, currentStacks)) {
					return;
				}*/
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

	@Nullable
	@Override
	public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new ContainerWorktable(windowId, playerInventory, this);
	}
}
