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

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.INbtWritable;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.core.utils.WorldUtils;

public class RecipeMemory implements INbtWritable, IStreamable {

	private static final int capacity = 9;

	private final LinkedList<MemorizedRecipe> memorizedRecipes;
	private long lastUpdate;

	public RecipeMemory() {
		memorizedRecipes = new LinkedList<>();
	}

	public RecipeMemory(CompoundTag nbt) {
		memorizedRecipes = new LinkedList<>();
		if (!nbt.contains("RecipeMemory")) {
			return;
		}

		ListTag nbttaglist = nbt.getList("RecipeMemory", 10);
		for (int j = 0; j < nbttaglist.size(); ++j) {
			CompoundTag recipeNbt = nbttaglist.getCompound(j);
			MemorizedRecipe recipe = new MemorizedRecipe(recipeNbt);
			if (recipe.hasSelectedRecipe()) {
				memorizedRecipes.add(recipe);
			}
		}
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public void memorizeRecipe(long worldTime, MemorizedRecipe recipe, Level world) {
		CraftingRecipe selectedRecipe = recipe.getSelectedRecipe(world);
		if (selectedRecipe == null) {
			return;
		}

		lastUpdate = worldTime;
		recipe.updateLastUse(lastUpdate);

		if (recipe.hasRecipeConflict()) {
			recipe.removeRecipeConflicts(world);
		}

		// update existing matching recipes
		MemorizedRecipe memory = getExistingMemorizedRecipe(selectedRecipe, world);
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

	//Client Only
	public ItemStack getRecipeDisplayOutput(int recipeIndex) {
		MemorizedRecipe recipe = getRecipe(recipeIndex);
		if (recipe == null) {
			return ItemStack.EMPTY;
		}
		Level world = WorldUtils.client();
		return recipe.getOutputIcon(world);
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
	private MemorizedRecipe getExistingMemorizedRecipe(@Nullable CraftingRecipe recipe, Level world) {
		if (recipe != null) {
			for (MemorizedRecipe memorizedRecipe : memorizedRecipes) {
				if (memorizedRecipe.hasRecipe(recipe, world)) {
					return memorizedRecipe;
				}
			}
		}

		return null;
	}

	@Override
	public CompoundTag write(CompoundTag compoundNBT) {
		ListTag listNBT = new ListTag();
		for (MemorizedRecipe recipe : memorizedRecipes) {
			if (recipe != null && recipe.hasSelectedRecipe()) {
				CompoundTag recipeNbt = new CompoundTag();
				recipe.write(recipeNbt);
				listNBT.add(recipeNbt);
			}
		}
		compoundNBT.put("RecipeMemory", listNBT);
		return compoundNBT;
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		data.writeStreamables(memorizedRecipes);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void readData(PacketBufferForestry data) throws IOException {
		data.readStreamables(memorizedRecipes, MemorizedRecipe::new);
	}
}
