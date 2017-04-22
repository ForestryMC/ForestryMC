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
package forestry.factory.recipes;

import java.io.IOException;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.factory.inventory.InventoryCraftingForestry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public final class MemorizedRecipe implements INbtWritable, INbtReadable, IStreamable {
	private final InventoryCraftingForestry craftMatrix = new InventoryCraftingForestry();
	private NonNullList<ItemStack> recipeOutputs = NonNullList.create();
	private int selectedRecipe;
	private long lastUsed;
	private boolean locked;

	public MemorizedRecipe(PacketBufferForestry data) throws IOException {
		readData(data);
	}

	public MemorizedRecipe(NBTTagCompound nbt) {
		readFromNBT(nbt);
	}

	public MemorizedRecipe(InventoryCraftingForestry craftMatrix, NonNullList<ItemStack> recipeOutputs) {
		InventoryUtil.deepCopyInventoryContents(craftMatrix, this.craftMatrix);
		this.recipeOutputs = recipeOutputs;
	}

	public InventoryCraftingForestry getCraftMatrix() {
		return craftMatrix;
	}

	public void calculateRecipeOutput(World world) {
		recipeOutputs = RecipeUtil.findMatchingRecipes(craftMatrix, world);
		if (selectedRecipe >= recipeOutputs.size()) {
			selectedRecipe = 0;
		}
		if (hasRecipeConflict()) {
			removeRecipeConflicts();
		}
	}

	public void incrementRecipe() {
		selectedRecipe++;
		if (selectedRecipe >= recipeOutputs.size()) {
			selectedRecipe = 0;
		}
	}

	public void decrementRecipe() {
		selectedRecipe--;
		if (selectedRecipe < 0) {
			selectedRecipe = recipeOutputs.size() - 1;
		}
	}

	public boolean hasRecipeConflict() {
		return recipeOutputs.size() > 1;
	}

	public void removeRecipeConflicts() {
		ItemStack recipeOutput = getRecipeOutput();
		recipeOutputs.clear();
		recipeOutputs.add(recipeOutput);
		selectedRecipe = 0;
	}

	public ItemStack getRecipeOutput() {
		if (recipeOutputs.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			return recipeOutputs.get(selectedRecipe);
		}
	}

	public boolean hasRecipeOutput(ItemStack output) {
		return ItemStackUtil.containsItemStack(recipeOutputs, output);
	}

	public void updateLastUse(long lastUsed) {
		this.lastUsed = lastUsed;
	}

	public long getLastUsed() {
		return lastUsed;
	}

	public void toggleLock() {
		locked = !locked;
	}

	public boolean isLocked() {
		return locked;
	}

	/* INbtWritable */
	@Override
	public final void readFromNBT(NBTTagCompound nbttagcompound) {
		InventoryUtil.readFromNBT(craftMatrix, nbttagcompound);
		lastUsed = nbttagcompound.getLong("LastUsed");
		locked = nbttagcompound.getBoolean("Locked");

		if (nbttagcompound.hasKey("SelectedRecipe")) {
			selectedRecipe = nbttagcompound.getInteger("SelectedRecipe");
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		InventoryUtil.writeToNBT(craftMatrix, nbttagcompound);
		nbttagcompound.setLong("LastUsed", lastUsed);
		nbttagcompound.setBoolean("Locked", locked);
		nbttagcompound.setInteger("SelectedRecipe", selectedRecipe);
		return nbttagcompound;
	}

	/* IStreamable */
	@Override
	public void writeData(PacketBufferForestry data) {
		data.writeInventory(craftMatrix);
		data.writeBoolean(locked);
		data.writeItemStacks(recipeOutputs);
		data.writeVarInt(selectedRecipe);
	}

	@Override
	public void readData(PacketBufferForestry data) throws IOException {
		data.readInventory(craftMatrix);
		locked = data.readBoolean();
		recipeOutputs = data.readItemStacks();
		selectedRecipe = data.readVarInt();
	}
}
