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
import forestry.core.inventory.InventoryAdapterTile;
import forestry.core.inventory.InventoryGhostCrafting;
import forestry.core.inventory.wrappers.InventoryMapper;
import forestry.core.network.PacketBufferForestry;
import forestry.core.recipes.RecipeUtil;
import forestry.core.tiles.TileBase;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.worktable.features.WorktableTiles;
import forestry.worktable.gui.ContainerWorktable;
import forestry.worktable.inventory.CraftingInventoryForestry;
import forestry.worktable.inventory.InventoryWorktable;
import forestry.worktable.recipes.MemorizedRecipe;
import forestry.worktable.recipes.RecipeMemory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

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
    public CompoundNBT write(CompoundNBT compoundNBT) {
        compoundNBT = super.write(compoundNBT);

        craftingDisplay.write(compoundNBT);
        recipeMemory.write(compoundNBT);
        return compoundNBT;
    }

    @Override
    public void read(BlockState state, CompoundNBT compoundNBT) {
        super.read(state, compoundNBT);

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

        IRecipe selectedRecipe = currentRecipe.getSelectedRecipe();
        if (selectedRecipe == null) {
            return false;
        }

        NonNullList<ItemStack> inventoryStacks = InventoryUtil.getStacks(this);
        CraftingInventoryForestry crafting = RecipeUtil.getCraftRecipe(
                currentRecipe.getCraftMatrix(),
                inventoryStacks,
                world,
                selectedRecipe
        );
        if (crafting == null) {
            return false;
        }

        NonNullList<ItemStack> recipeItems = InventoryUtil.getStacks(crafting);

        IInventory inventory;
        if (simulate) {
            inventory = new Inventory(this.getSizeInventory());    //TODO use copy constructor in inventory?
            InventoryUtil.deepCopyInventoryContents(this, inventory);
        } else {
            inventory = this;
        }

        if (!InventoryUtil.deleteExactSet(inventory, recipeItems)) {
            return false;
        }

        if (!simulate) {
            // update crafting display to match the ingredients that were actually used
            currentRecipe.setCraftMatrix(crafting);
            setCurrentRecipe(currentRecipe);
        }

        return true;
    }

    @Override
    public void onCraftingComplete(PlayerEntity player) {
        Preconditions.checkNotNull(currentRecipe);
        IRecipe selectedRecipe = currentRecipe.getSelectedRecipe();
        Preconditions.checkNotNull(selectedRecipe);

        ForgeHooks.setCraftingPlayer(player);
        CraftingInventoryForestry craftMatrix = currentRecipe.getCraftMatrix();
        NonNullList<ItemStack> remainingItems = selectedRecipe.getRemainingItems(craftMatrix.copy());
        ForgeHooks.setCraftingPlayer(null);

        for (ItemStack remainingItem : remainingItems) {
            if (remainingItem != null && !remainingItem.isEmpty()) {
                if (!InventoryUtil.tryAddStack(this, remainingItem, true)) {
                    player.dropItem(remainingItem, false);
                }
            }
        }

        if (!world.isRemote) {
            recipeMemory.memorizeRecipe(world.getGameTime(), currentRecipe);
        }
    }

    @Nullable
    @Override
    public IRecipe getRecipeUsed() {
        if (currentRecipe == null) {
            return null;
        }

        return currentRecipe.getSelectedRecipe();
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
        return new InventoryMapper(
                craftingDisplay,
                InventoryGhostCrafting.SLOT_CRAFTING_1,
                InventoryGhostCrafting.SLOT_CRAFTING_COUNT
        );
    }

    public void clearCraftMatrix() {
        for (int slot = 0; slot < craftingDisplay.getSizeInventory(); slot++) {
            craftingDisplay.setInventorySlotContents(slot, ItemStack.EMPTY);
        }
    }

    public void setCurrentRecipe(CraftingInventoryForestry crafting) {
        List<IRecipe> recipes = RecipeUtil.findMatchingRecipes(crafting, world);
        MemorizedRecipe recipe = recipes.isEmpty() ? null : new MemorizedRecipe(crafting, recipes);

        if (currentRecipe != null && recipe != null) {
            if (recipe.hasRecipe(currentRecipe.getSelectedRecipe())) {
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

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new ContainerWorktable(windowId, playerInventory, this);
    }
}
