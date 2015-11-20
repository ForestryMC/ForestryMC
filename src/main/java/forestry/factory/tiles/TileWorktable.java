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

import java.io.IOException;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

import forestry.core.inventory.InventoryAdapterTile;
import forestry.core.inventory.wrappers.InventoryMapper;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
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

public class TileWorktable extends TileBase implements ICrafterWorktable {
	private final RecipeMemory recipeMemory;
	private final InventoryAdapterTile craftingDisplay;
	private MemorizedRecipe currentRecipe;

	public TileWorktable() {
		super("worktable");
		setInternalInventory(new InventoryWorktable(this));

		craftingDisplay = new InventoryGhostCrafting<>(this, 10);
		recipeMemory = new RecipeMemory();
	}

	/* LOADING & SAVING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		craftingDisplay.writeToNBT(nbttagcompound);
		recipeMemory.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		craftingDisplay.readFromNBT(nbttagcompound);
		recipeMemory.readFromNBT(nbttagcompound);
	}

	/* NETWORK */
	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);

		craftingDisplay.writeData(data);
		recipeMemory.writeData(data);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);

		craftingDisplay.readData(data);
		recipeMemory.readData(data);
	}

	@Override
	public void validate() {
		super.validate();
		recipeMemory.validate(worldObj);
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
		if (craftingSlotIndex == InventoryGhostCrafting.SLOT_CRAFTING_RESULT) {
			return canCraftCurrentRecipe();
		}
		return true;
	}

	private boolean canCraftCurrentRecipe() {
		if (currentRecipe == null) {
			return false;
		}

		ItemStack[] recipeItems = InventoryUtil.getStacks(currentRecipe.getCraftMatrix());
		ItemStack[] inventory = InventoryUtil.getStacks(this);
		InventoryCraftingForestry crafting = RecipeUtil.getCraftRecipe(recipeItems, inventory, worldObj, currentRecipe.getRecipeOutput());
		return crafting != null;
	}

	@Override
	public boolean onCraftingStart(EntityPlayer player) {
		if (currentRecipe == null) {
			return false;
		}

		ItemStack[] recipeItems = InventoryUtil.getStacks(currentRecipe.getCraftMatrix());
		ItemStack[] inventory = InventoryUtil.getStacks(this);
		InventoryCraftingForestry crafting = RecipeUtil.getCraftRecipe(recipeItems, inventory, worldObj, currentRecipe.getRecipeOutput());
		if (crafting == null) {
			return false;
		}

		recipeItems = InventoryUtil.getStacks(crafting);

		// craft recipe should exactly match ingredients here, so no oreDict or tool matching
		ItemStack[] removed = InventoryUtil.removeSets(this, 1, recipeItems, player, false, false, false);
		if (removed == null) {
			return false;
		}

		// update crafting display to match the ingredients that were actually used
		setCraftingDisplay(crafting);
		return true;
	}

	@Override
	public void onCraftingComplete(EntityPlayer player) {
		IInventory craftingInventory = getCraftingDisplay();
		for (int i = 0; i < craftingInventory.getSizeInventory(); ++i) {
			ItemStack itemStack = craftingInventory.getStackInSlot(i);
			if (itemStack == null) {
				continue;
			}

			ItemStack container = null;

			if (itemStack.getItem().hasContainerItem(itemStack)) {
				container = itemStack.getItem().getContainerItem(itemStack);
			} else if (itemStack.stackSize > 1) {
				// TerraFirmaCraft's crafting event handler does some tricky stuff with its tools.
				// It sets the tool's stack size to 2 instead of using a container.
				container = ItemStackUtil.createSplitStack(itemStack, itemStack.stackSize - 1);
				itemStack.stackSize = 1;
			}

			if (container == null) {
				continue;
			}

			if (container != null && container.isItemStackDamageable() && container.getItemDamage() > container.getMaxDamage()) {
				MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, container));
				continue;
			}

			if (!InventoryUtil.tryAddStack(this, container, true)) {
				player.dropPlayerItemWithRandomChoice(container, false);
			}
		}
		
		if (!worldObj.isRemote) {
			recipeMemory.memorizeRecipe(worldObj.getTotalWorldTime(), currentRecipe);
		}
	}

	@Override
	public ItemStack getResult() {
		if (currentRecipe == null) {
			return null;
		}

		ItemStack result = currentRecipe.getRecipeOutput();
		if (result != null) {
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
		if (craftMatrix == null) {
			return;
		}

		for (int slot = 0; slot < craftMatrix.getSizeInventory(); slot++) {
			craftingDisplay.setInventorySlotContents(slot, craftMatrix.getStackInSlot(slot));
		}
	}

	public IInventory getCraftingDisplay() {
		return new InventoryMapper(craftingDisplay, InventoryGhostCrafting.SLOT_CRAFTING_1, InventoryGhostCrafting.SLOT_CRAFTING_COUNT);
	}

	public void clearCraftMatrix() {
		for (int slot = 0; slot < craftingDisplay.getSizeInventory(); slot++) {
			craftingDisplay.setInventorySlotContents(slot, null);
		}
	}

	public void setCurrentRecipe(InventoryCraftingForestry crafting) {
		List<ItemStack> recipeOutputs = RecipeUtil.findMatchingRecipes(crafting, worldObj);
		MemorizedRecipe recipe = recipeOutputs.size() == 0 ? null : new MemorizedRecipe(crafting, recipeOutputs);

		if (currentRecipe != null && recipe != null) {
			if (recipe.hasRecipeOutput(currentRecipe.getRecipeOutput())) {
				ItemStack[] stacks = InventoryUtil.getStacks(crafting);
				ItemStack[] currentStacks = InventoryUtil.getStacks(currentRecipe.getCraftMatrix());
				if (ItemStackUtil.equalSets(stacks, currentStacks)) {
					return;
				}
			}
		}

		setCurrentRecipe(recipe);
	}

	/* Network Sync with PacketWorktableRecipeUpdate */
	public MemorizedRecipe getCurrentRecipe() {
		return currentRecipe;
	}

	public void setCurrentRecipe(MemorizedRecipe recipe) {
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
