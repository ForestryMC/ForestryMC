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

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import forestry.api.core.INBTTagable;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;
import forestry.core.utils.InventoryUtil;
import forestry.factory.inventory.InventoryWorktableCrafting;

public final class MemorizedRecipe implements INBTTagable, IStreamable {
	private final InventoryWorktableCrafting craftMatrix = new InventoryWorktableCrafting();
	private ItemStack recipeOutput;
	private long lastUsed;
	private boolean locked;

	public MemorizedRecipe() {
		// required for IStreamable serialization
	}

	public MemorizedRecipe(InventoryWorktableCrafting craftMatrix, ItemStack recipeOutput) {
		InventoryUtil.deepCopyInventoryContents(craftMatrix, this.craftMatrix);
		this.recipeOutput = recipeOutput;
	}

	public InventoryWorktableCrafting getCraftMatrix() {
		return craftMatrix;
	}

	/**
	 * Calculate the recipe output.
	 * For older memorized recipes that were not created with an output.
	 * @since Forestry 4.2
	 */
	public void updateRecipeOutputLegacy(World world) {
		if (recipeOutput == null) {
			recipeOutput = CraftingManager.getInstance().findMatchingRecipe(craftMatrix, world);
		}
	}

	public ItemStack getRecipeOutput() {
		return recipeOutput;
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

	/* INBTTagable */
	@Override
	public final void readFromNBT(NBTTagCompound nbttagcompound) {
		InventoryUtil.readFromNBT(craftMatrix, nbttagcompound);
		lastUsed = nbttagcompound.getLong("LastUsed");
		locked = nbttagcompound.getBoolean("Locked");

		if (nbttagcompound.hasKey("Output")) {
			NBTTagCompound recipeOutputNbt = nbttagcompound.getCompoundTag("Output");
			recipeOutput = ItemStack.loadItemStackFromNBT(recipeOutputNbt);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		InventoryUtil.writeToNBT(craftMatrix, nbttagcompound);
		nbttagcompound.setLong("LastUsed", lastUsed);
		nbttagcompound.setBoolean("Locked", locked);

		NBTTagCompound recipeOutputNbt = new NBTTagCompound();
		recipeOutput.writeToNBT(recipeOutputNbt);
		nbttagcompound.setTag("Output", recipeOutputNbt);
	}

	/* IStreamable */
	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeInventory(craftMatrix);
		data.writeItemStack(recipeOutput);
		data.writeLong(lastUsed);
		data.writeBoolean(locked);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		data.readInventory(craftMatrix);
		recipeOutput = data.readItemStack();
		lastUsed = data.readLong();
		locked = data.readBoolean();
	}
}
