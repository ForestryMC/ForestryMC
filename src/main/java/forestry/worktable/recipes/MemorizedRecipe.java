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
import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.NBTUtilForestry;
import forestry.core.utils.RecipeUtils;
import forestry.worktable.inventory.CraftingInventoryForestry;

public final class MemorizedRecipe implements INbtWritable, INbtReadable, IStreamable {
	private CraftingInventoryForestry craftMatrix = new CraftingInventoryForestry();
	private List<ICraftingRecipe> recipes = new ArrayList<>();
	private final List<String> recipeNames = new ArrayList<>();
	private int selectedRecipe;
	private long lastUsed;
	private boolean locked;

	public MemorizedRecipe(PacketBufferForestry data) throws IOException {
		readData(data);
	}

	public MemorizedRecipe(CompoundNBT nbt) {
		read(nbt);
	}

	public MemorizedRecipe(CraftingInventoryForestry craftMatrix, List<ICraftingRecipe> recipes) {
		InventoryUtil.deepCopyInventoryContents(craftMatrix, this.craftMatrix);
		for (ICraftingRecipe recipe : recipes) {
			recipeNames.add(recipe.getId().toString());
		}
	}

	public CraftingInventoryForestry getCraftMatrix() {
		return craftMatrix;
	}

	public void setCraftMatrix(CraftingInventoryForestry craftMatrix) {
		this.craftMatrix = craftMatrix;
	}

	public void incrementRecipe() {
		selectedRecipe++;
		if (selectedRecipe >= recipes.size()) {
			selectedRecipe = 0;
		}
	}

	public void decrementRecipe() {
		selectedRecipe--;
		if (selectedRecipe < 0) {
			selectedRecipe = recipes.size() - 1;
		}
	}

	public boolean hasRecipeConflict() {
		return recipes.size() > 1;
	}

	public void removeRecipeConflicts(World world) {
		ICraftingRecipe recipe = getSelectedRecipe(world);
		recipes.clear();
		recipes.add(recipe);
		selectedRecipe = 0;
	}

	public ItemStack getOutputIcon(World world) {
		ICraftingRecipe selectedRecipe = getSelectedRecipe(world);
		if (selectedRecipe != null) {
			ItemStack recipeOutput = selectedRecipe.getCraftingResult(craftMatrix);
			if (!recipeOutput.isEmpty()) {
				return recipeOutput;
			}
		}
		return ItemStack.EMPTY;
	}

	public ItemStack getCraftingResult(CraftingInventory inventory, World world) {
		ICraftingRecipe selectedRecipe = getSelectedRecipe(world);
		if (selectedRecipe != null && selectedRecipe.matches(inventory, world)) {
			ItemStack recipeOutput = selectedRecipe.getCraftingResult(inventory);
			if (!recipeOutput.isEmpty()) {
				return recipeOutput;
			}
		}
		return ItemStack.EMPTY;
	}	public boolean hasRecipes() {
		return (!recipes.isEmpty() || !recipeNames.isEmpty());
	}

	public boolean hasSelectedRecipe() {
		return hasRecipes() && selectedRecipe >= 0 && recipeNames.size() > selectedRecipe && recipeNames.get(selectedRecipe) != null;
	}

	public List<ICraftingRecipe> getRecipes(@Nullable World world) {
		if(recipes.isEmpty() && !recipeNames.isEmpty()) {
			for(String recipeKey : recipeNames) {
				ResourceLocation key = new ResourceLocation(recipeKey);
				IRecipe<CraftingInventory> recipe = RecipeUtils.getRecipe(IRecipeType.CRAFTING, key, world);
				if (recipe instanceof ICraftingRecipe) {
					recipes.add((ICraftingRecipe) recipe);
				}
			}
			if (selectedRecipe > recipes.size()) {
				selectedRecipe = 0;
			}
		}
		return recipes;
	}

	@Nullable
	public ICraftingRecipe getSelectedRecipe(@Nullable World world) {
		List<ICraftingRecipe> recipes = getRecipes(world);
		if (recipes.isEmpty()) {
			return null;
		} else {
			return recipes.get(selectedRecipe);
		}
	}

	public boolean hasRecipe(@Nullable ICraftingRecipe recipe, @Nullable World world) {
		return getRecipes(world).contains(recipe);
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

	//and type is always Crafting.
	/* INbtWritable */
	@Override
	public final void read(CompoundNBT compoundNBT) {
		InventoryUtil.readFromNBT(craftMatrix, "inventory", compoundNBT);
		lastUsed = compoundNBT.getLong("LastUsed");
		locked = compoundNBT.getBoolean("Locked");

		if (compoundNBT.contains("SelectedRecipe")) {
			selectedRecipe = compoundNBT.getInt("SelectedRecipe");
		}

		recipes.clear();
		recipeNames.clear();
		ListNBT recipesNbt = compoundNBT.getList("Recipes", NBTUtilForestry.EnumNBTType.STRING.ordinal());
		for (int i = 0; i < recipesNbt.size(); i++) {
			String recipeKey = recipesNbt.getString(i);
			recipeNames.add(recipeKey);
		}

		if (selectedRecipe > recipeNames.size()) {
			selectedRecipe = 0;
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compoundNBT) {
		InventoryUtil.writeToNBT(craftMatrix, "inventory", compoundNBT);
		compoundNBT.putLong("LastUsed", lastUsed);
		compoundNBT.putBoolean("Locked", locked);
		compoundNBT.putInt("SelectedRecipe", selectedRecipe);

		ListNBT recipesNbt = new ListNBT();
		for (String recipeName : recipeNames) {
			recipesNbt.add(StringNBT.valueOf(recipeName));
		}
		compoundNBT.put("Recipes", recipesNbt);

		return compoundNBT;
	}

	/* IStreamable */
	@Override
	public void writeData(PacketBufferForestry data) {
		data.writeInventory(craftMatrix);
		data.writeBoolean(locked);
		data.writeVarInt(selectedRecipe);

		data.writeVarInt(recipeNames.size());
		for (String recipeName : recipeNames) {
			data.writeString(recipeName);
		}
	}

	@Override
	public void readData(PacketBufferForestry data) throws IOException {
		data.readInventory(craftMatrix);
		locked = data.readBoolean();
		selectedRecipe = data.readVarInt();

		recipes.clear();
		recipeNames.clear();
		int recipeCount = data.readVarInt();
		for (int i = 0; i < recipeCount; i++) {
			String recipeId = data.readString();
			recipeNames.add(recipeId);
		}
	}
}
