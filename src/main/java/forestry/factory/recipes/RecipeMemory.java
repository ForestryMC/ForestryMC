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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipesMapCloning;
import net.minecraft.item.crafting.RecipesMapExtending;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import forestry.api.core.INbtWritable;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;
import forestry.core.proxy.Proxies;


import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RecipeMemory implements INbtWritable, IStreamable {

	private static final int capacity = 9;
	
	private static final List<Class<? extends IRecipe>> memoryBlacklist = new ArrayList<>();

	static {
		// almost every ItemMap is unique
		memoryBlacklist.add(RecipesMapCloning.class);
		memoryBlacklist.add(RecipesMapExtending.class);
	}

	private final LinkedList<MemorizedRecipe> memorizedRecipes;
	private long lastUpdate;

	public RecipeMemory() {
		memorizedRecipes = new LinkedList<>();
	}

	public RecipeMemory(@Nonnull NBTTagCompound nbt) {
		memorizedRecipes = new LinkedList<>();
		if (!nbt.hasKey("RecipeMemory")) {
			return;
		}

		NBTTagList nbttaglist = nbt.getTagList("RecipeMemory", 10);
		for (int j = 0; j < nbttaglist.tagCount(); ++j) {
			NBTTagCompound recipeNbt = nbttaglist.getCompoundTagAt(j);
			MemorizedRecipe recipe = new MemorizedRecipe(recipeNbt);
			memorizedRecipes.add(recipe);
		}
	}

	private static boolean isValid(MemorizedRecipe recipe) {
		if (recipe == null) {
			return false;
		}
		IRecipe recipeOutput = recipe.getSelectedRecipe();
		return recipeOutput != null && !memoryBlacklist.contains(recipeOutput.getClass());
	}

	public void validate(World world) {
		Iterator<MemorizedRecipe> iterator = memorizedRecipes.iterator();
		while (iterator.hasNext()) {
			MemorizedRecipe recipe = iterator.next();
			if (recipe != null) {
				recipe.validate(world);
				if (!isValid(recipe)) {
					iterator.remove();
				}
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

	public MemorizedRecipe getRecipe(int recipeIndex) {
		if (recipeIndex < 0 || recipeIndex >= memorizedRecipes.size()) {
			return null;
		}
		return memorizedRecipes.get(recipeIndex);
	}
	
	public ItemStack getRecipeDisplayOutput(int recipeIndex) {
		MemorizedRecipe recipe = getRecipe(recipeIndex);
		if (recipe == null) {
			return null;
		}
		return recipe.getOutputIcon();
	}

	public boolean isLocked(int recipeIndex) {
		MemorizedRecipe recipe = getRecipe(recipeIndex);
		if (recipe == null) {
			return false;
		}
		return recipe.isLocked();
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
			if (recipe != null) {
				NBTTagCompound recipeNbt = new NBTTagCompound();
				recipe.writeToNBT(recipeNbt);
				nbttaglist.appendTag(recipeNbt);
			}
		}
		nbttagcompound.setTag("RecipeMemory", nbttaglist);
		return nbttagcompound;
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeStreamables(memorizedRecipes);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void readData(DataInputStreamForestry data) throws IOException {
		data.readStreamables(memorizedRecipes, data1 -> new MemorizedRecipe(data1, Proxies.common.getRenderWorld()));
	}
}
