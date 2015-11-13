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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

import forestry.core.inventory.InventoryAdapterTile;
import forestry.core.inventory.wrappers.InventoryMapper;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.GuiId;
import forestry.core.recipes.RecipeUtil;
import forestry.core.tiles.TileBase;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.factory.inventory.InventoryGhostCrafting;
import forestry.factory.inventory.InventoryWorktable;
import forestry.factory.inventory.InventoryWorktableCrafting;
import forestry.factory.recipes.MemorizedRecipe;
import forestry.factory.recipes.RecipeMemory;

public class TileWorktable extends TileBase implements ICrafterWorktable {
	private final RecipeMemory recipeMemory;
	private final InventoryAdapterTile craftingDisplay;
	private MemorizedRecipe currentRecipe;

	public TileWorktable() {
		super(GuiId.WorktableGUI, "worktable");
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

	/* RECIPE SELECTION */
	public RecipeMemory getMemory() {
		return recipeMemory;
	}

	public void clearCraftMatrix() {
		for (int slot = 0; slot < craftingDisplay.getSizeInventory(); slot++) {
			craftingDisplay.setInventorySlotContents(slot, null);
		}
	}

	public void chooseRecipe(int recipeIndex) {
		IInventory craftMatrix = recipeMemory.getRecipeCraftMatrix(recipeIndex);
		if (craftMatrix == null) {
			return;
		}

		for (int slot = 0; slot < craftMatrix.getSizeInventory(); slot++) {
			craftingDisplay.setInventorySlotContents(slot, craftMatrix.getStackInSlot(slot));
		}
	}

	/* CRAFTING */
	public void setRecipe(InventoryWorktableCrafting crafting) {
		ItemStack recipeOutput = CraftingManager.getInstance().findMatchingRecipe(crafting, worldObj);
		if (recipeOutput == null) {
			currentRecipe = null;
		} else {
			currentRecipe = new MemorizedRecipe(crafting, recipeOutput);
		}
		updateCraftResult();
	}

	private void updateCraftResult() {
		if (currentRecipe != null) {
			ItemStack result = currentRecipe.getRecipeOutput();
			if (result != null) {
				craftingDisplay.setInventorySlotContents(InventoryGhostCrafting.SLOT_CRAFTING_RESULT, result.copy());
				return;
			}
		}

		craftingDisplay.setInventorySlotContents(InventoryGhostCrafting.SLOT_CRAFTING_RESULT, null);
	}

	private boolean canCraftCurrentRecipe() {
		if (currentRecipe == null) {
			return false;
		}

		ItemStack[] recipeItems = InventoryUtil.getStacks(craftingDisplay, InventoryGhostCrafting.SLOT_CRAFTING_1, InventoryGhostCrafting.SLOT_CRAFTING_COUNT);
		ItemStack[] inventory = InventoryUtil.getStacks(this);
		ItemStack recipeOutput = currentRecipe.getRecipeOutput();

		return RecipeUtil.canCraftRecipe(worldObj, recipeItems, recipeOutput, inventory);
	}

	@Override
	public boolean canTakeStack(int slotIndex) {
		if (slotIndex == InventoryGhostCrafting.SLOT_CRAFTING_RESULT) {
			return canCraftCurrentRecipe();
		}
		return true;
	}

	@Override
	public boolean onCraftingStart(EntityPlayer player) {
		ItemStack[] set = InventoryUtil.getStacks(currentRecipe.getCraftMatrix());

		IInventory inventory = new InventoryMapper(this, InventoryWorktable.SLOT_INVENTORY_1, InventoryWorktable.SLOT_INVENTORY_COUNT);
		ItemStack[] removed = InventoryUtil.removeSets(inventory, 1, set, player, false, true, true);

		if (removed == null) {
			return false;
		}

		for (int i = 0; i < removed.length; i++) {
			craftingDisplay.setInventorySlotContents(i, removed[i]);
		}
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

		updateCraftResult();
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

	public IInventory getCraftingDisplay() {
		return new InventoryMapper(craftingDisplay, InventoryGhostCrafting.SLOT_CRAFTING_1, InventoryGhostCrafting.SLOT_CRAFTING_COUNT);
	}
}
