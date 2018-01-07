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
package forestry.worktable.recipes;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.LinkedList;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.INbtWritable;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;

public class RecipeMemory implements INbtWritable, IStreamable {

	private static final int capacity = 9;

	private final LinkedList<MemorizedRecipe> memorizedRecipes;
	private long lastUpdate;

	public RecipeMemory() {
		memorizedRecipes = new LinkedList<>();
	}

	public RecipeMemory(NBTTagCompound nbt) {
		memorizedRecipes = new LinkedList<>();
		if (!nbt.hasKey("RecipeMemory")) {
			return;
		}

		NBTTagList nbttaglist = nbt.getTagList("RecipeMemory", 10);
		for (int j = 0; j < nbttaglist.tagCount(); ++j) {
			NBTTagCompound recipeNbt = nbttaglist.getCompoundTagAt(j);
			MemorizedRecipe recipe = new MemorizedRecipe(recipeNbt);
			if (recipe.getSelectedRecipe() != null) {
				memorizedRecipes.add(recipe);
			}
		}
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public void memorizeRecipe(long worldTime, MemorizedRecipe recipe) {
		if (recipe.getSelectedRecipe() == null) {
			return;
		}

		lastUpdate = worldTime;
		recipe.updateLastUse(lastUpdate);

		if (recipe.hasRecipeConflict()) {
			recipe.removeRecipeConflicts();
		}

		// update existing matching recipes
		MemorizedRecipe memory = getExistingMemorizedRecipe(recipe.getSelectedRecipe());
		if (memory != null) {
			updateExistingRecipe(memory, recipe);
			return;
		}

		// add a new recipe
		if (memorizedRecipes.size() < capacity) {
			memorizedRecipes.add(recipe);
		} else {
			MemorizedRecipe oldest = getOldestUnlockedRecipe();
			if (oldest != null) {
				memorizedRecipes.remove(oldest);
				memorizedRecipes.add(recipe);
			}
		}
	}

	private void updateExistingRecipe(MemorizedRecipe existingRecipe, MemorizedRecipe updatedRecipe) {
		if (existingRecipe.isLocked() != updatedRecipe.isLocked()) {
			updatedRecipe.toggleLock();
		}
		int index = memorizedRecipes.indexOf(existingRecipe);
		memorizedRecipes.set(index, updatedRecipe);
	}

	@Nullable
	private MemorizedRecipe getOldestUnlockedRecipe() {
		MemorizedRecipe oldest = null;
		for (MemorizedRecipe existing : memorizedRecipes) {
			if (oldest != null && oldest.getLastUsed() < existing.getLastUsed()) {
				continue;
			}

			if (!existing.isLocked()) {
				oldest = existing;
			}
		}
		return oldest;
	}

	@Nullable
	public MemorizedRecipe getRecipe(int recipeIndex) {
		if (recipeIndex < 0 || recipeIndex >= memorizedRecipes.size()) {
			return null;
		}
		return memorizedRecipes.get(recipeIndex);
	}

	public ItemStack getRecipeDisplayOutput(int recipeIndex) {
		MemorizedRecipe recipe = getRecipe(recipeIndex);
		if (recipe == null) {
			return ItemStack.EMPTY;
		}
		return recipe.getOutputIcon();
	}

	public boolean isLocked(int recipeIndex) {
		MemorizedRecipe recipe = getRecipe(recipeIndex);
		return recipe != null && recipe.isLocked();
	}

	public void toggleLock(long worldTime, int recipeIndex) {
		lastUpdate = worldTime;
		if (memorizedRecipes.size() > recipeIndex) {
			memorizedRecipes.get(recipeIndex).toggleLock();
		}
	}

	@Nullable
	private MemorizedRecipe getExistingMemorizedRecipe(@Nullable IRecipe recipe) {
		if (recipe != null) {
			for (MemorizedRecipe memorizedRecipe : memorizedRecipes) {
				if (memorizedRecipe.hasRecipe(recipe)) {
					return memorizedRecipe;
				}
			}
		}

		return null;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		NBTTagList nbttaglist = new NBTTagList();
		for (MemorizedRecipe recipe : memorizedRecipes) {
			if (recipe != null && recipe.getSelectedRecipe() != null) {
				NBTTagCompound recipeNbt = new NBTTagCompound();
				recipe.writeToNBT(recipeNbt);
				nbttaglist.appendTag(recipeNbt);
			}
		}
		nbttagcompound.setTag("RecipeMemory", nbttaglist);
		return nbttagcompound;
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		data.writeStreamables(memorizedRecipes);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void readData(PacketBufferForestry data) throws IOException {
		data.readStreamables(memorizedRecipes, MemorizedRecipe::new);
	}
}
