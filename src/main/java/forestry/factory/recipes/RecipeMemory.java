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

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import forestry.api.core.INbtWritable;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RecipeMemory implements INbtWritable, IStreamable {

	private static final int capacity = 9;

	private static final List<Class<? extends Item>> memoryBlacklist = new ArrayList<>();

	static {
		memoryBlacklist.add(ItemMap.class); // almost every ItemMap is unique
	}

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
			memorizedRecipes.add(recipe);
		}
	}

	private static boolean isValid(@Nullable MemorizedRecipe recipe) {
		if (recipe == null) {
			return false;
		}
		ItemStack recipeOutput = recipe.getRecipeOutput();
		if (recipeOutput.isEmpty()) {
			return false;
		}
		Item item = recipeOutput.getItem();
		return !memoryBlacklist.contains(item.getClass());
	}

	public void validate(World world) {
		Iterator<MemorizedRecipe> iterator = memorizedRecipes.iterator();
		while (iterator.hasNext()) {
			MemorizedRecipe recipe = iterator.next();
			if (recipe != null) {
				recipe.calculateRecipeOutput(world);
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
		if (!isValid(recipe)) {
			return;
		}

		lastUpdate = worldTime;
		recipe.updateLastUse(lastUpdate);

		if (recipe.hasRecipeConflict()) {
			recipe.removeRecipeConflicts();
		}

		// update existing matching recipes
		MemorizedRecipe memory = getExistingMemorizedRecipe(recipe.getRecipeOutput());
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
		return recipe.getRecipeOutput();
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
	private MemorizedRecipe getExistingMemorizedRecipe(ItemStack craftingRecipeOutput) {
		for (MemorizedRecipe memorizedRecipe : memorizedRecipes) {
			if (memorizedRecipe.hasRecipeOutput(craftingRecipeOutput)) {
				return memorizedRecipe;
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
	public void writeData(PacketBufferForestry data) {
		data.writeStreamables(memorizedRecipes);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void readData(PacketBufferForestry data) throws IOException {
		data.readStreamables(memorizedRecipes, MemorizedRecipe::new);
	}
}
