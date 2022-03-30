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

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.Level;

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
	private final InventoryAdapterTile<TileWorktable> craftingDisplay;
	@Nullable
	private MemorizedRecipe currentRecipe;

	public TileWorktable(BlockPos pos, BlockState state) {
		super(WorktableTiles.WORKTABLE.tileType(), pos, state);
		setInternalInventory(new InventoryWorktable(this));

		craftingDisplay = new InventoryGhostCrafting<>(this, 10);
		recipeMemory = new RecipeMemory();
	}

	/* LOADING & SAVING */

	@Override
	public void saveAdditional(CompoundTag compoundNBT) {
		super.saveAdditional(compoundNBT);

		craftingDisplay.write(compoundNBT);
		recipeMemory.write(compoundNBT);
	}

	@Override
	public void load(CompoundTag compoundNBT) {
		super.load(compoundNBT);

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
	public ItemStack getResult(CraftingContainer inventory, Level world) {
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
	public boolean onCraftingStart(Player player) {
		return craftRecipe(false);
	}

	private boolean craftRecipe(boolean simulate) {
		if (currentRecipe == null) {
			return false;
		}

		CraftingRecipe selectedRecipe = currentRecipe.getSelectedRecipe(level);
		if (selectedRecipe == null) {
			return false;
		}

		Container inventory = new InventoryMapper(this, InventoryWorktable.SLOT_INVENTORY_1, InventoryWorktable.SLOT_INVENTORY_COUNT);
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
	public void onCraftingComplete(Player player) {
		Preconditions.checkNotNull(currentRecipe);
		CraftingRecipe selectedRecipe = currentRecipe.getSelectedRecipe(level);
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

		if (level != null && !level.isClientSide) {
			recipeMemory.memorizeRecipe(level.getGameTime(), currentRecipe, level);
		}
	}

	@Nullable
	@Override
	public CraftingRecipe getRecipeUsed() {
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

	private void setCraftingDisplay(Container craftMatrix) {
		for (int slot = 0; slot < craftMatrix.getContainerSize(); slot++) {
			ItemStack stack = craftMatrix.getItem(slot);
			craftingDisplay.setItem(slot, stack.copy());
		}
	}

	public Container getCraftingDisplay() {
		return new InventoryMapper(craftingDisplay, InventoryGhostCrafting.SLOT_CRAFTING_1, InventoryGhostCrafting.SLOT_CRAFTING_COUNT);
	}

	public void clearCraftMatrix() {
		for (int slot = 0; slot < craftingDisplay.getContainerSize(); slot++) {
			craftingDisplay.setItem(slot, ItemStack.EMPTY);
		}
	}

	public void setCurrentRecipe(CraftingInventoryForestry crafting) {
		List<CraftingRecipe> recipes = RecipeUtils.getRecipes(RecipeType.CRAFTING, crafting, level);
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
	public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
		return new ContainerWorktable(windowId, playerInventory, this);
	}
}
