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
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
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
import forestry.worktable.inventory.CraftingInventoryForestry;

public final class MemorizedRecipe implements INbtWritable, INbtReadable, IStreamable {
	private CraftingInventoryForestry craftMatrix = new CraftingInventoryForestry();
	private List<IRecipe> recipes = new ArrayList<>();
	private int selectedRecipe;
	private long lastUsed;
	private boolean locked;

	public MemorizedRecipe(PacketBufferForestry data) throws IOException {
		readData(data);
	}

	public MemorizedRecipe(CompoundNBT nbt) {
		read(nbt);
	}

	public MemorizedRecipe(CraftingInventoryForestry craftMatrix, List<IRecipe> recipes) {
		InventoryUtil.deepCopyInventoryContents(craftMatrix, this.craftMatrix);
		this.recipes = recipes;
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

	public void removeRecipeConflicts() {
		IRecipe recipe = getSelectedRecipe();
		recipes.clear();
		recipes.add(recipe);
		selectedRecipe = 0;
	}

	public ItemStack getOutputIcon() {
		IRecipe selectedRecipe = getSelectedRecipe();
		if (selectedRecipe != null) {
			ItemStack recipeOutput = selectedRecipe.getCraftingResult(craftMatrix);
			if (!recipeOutput.isEmpty()) {
				return recipeOutput;
			}
		}
		return ItemStack.EMPTY;
	}

	public ItemStack getCraftingResult(CraftingInventory CraftingInventory, World world) {
		IRecipe selectedRecipe = getSelectedRecipe();
		if (selectedRecipe != null && selectedRecipe.matches(CraftingInventory, world)) {
			ItemStack recipeOutput = selectedRecipe.getCraftingResult(CraftingInventory);
			if (!recipeOutput.isEmpty()) {
				return recipeOutput;
			}
		}
		return ItemStack.EMPTY;
	}

	@Nullable
	public IRecipe getSelectedRecipe() {
		if (recipes.isEmpty()) {
			return null;
		} else {
			return recipes.get(selectedRecipe);
		}
	}

	public boolean hasRecipe(@Nullable IRecipe recipe) {
		return this.recipes.contains(recipe);
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

	//TODO use recipemanager, maybe AT method. Then just need the type and the recipe id.
	//and type is always Crafting.
	/* INbtWritable */
	@Override
	public final void read(CompoundNBT compoundNBT) {
		InventoryUtil.readFromNBT(craftMatrix, compoundNBT);
		lastUsed = compoundNBT.getLong("LastUsed");
		locked = compoundNBT.getBoolean("Locked");

		if (compoundNBT.contains("SelectedRecipe")) {
			selectedRecipe = compoundNBT.getInt("SelectedRecipe");
		}

		recipes.clear();
		ListNBT recipesNbt = compoundNBT.getList("Recipes", NBTUtilForestry.EnumNBTType.STRING.ordinal());
		for (int i = 0; i < recipesNbt.size(); i++) {
			String recipeKey = recipesNbt.getString(i);
			ResourceLocation key = new ResourceLocation(recipeKey);
			//TODO are we on server or client? Not sure how to access this on server...
			Map<ResourceLocation, IRecipe<CraftingInventory>> recipeMap = Minecraft.getInstance().player.connection.getRecipeManager().getRecipes(IRecipeType.CRAFTING);
			IRecipe recipe = recipeMap.get(key);
			if (recipe != null) {
				recipes.add(recipe);
			}
		}

		if (selectedRecipe > recipes.size()) {
			selectedRecipe = 0;
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compoundNBT) {
		InventoryUtil.writeToNBT(craftMatrix, compoundNBT);
		compoundNBT.putLong("LastUsed", lastUsed);
		compoundNBT.putBoolean("Locked", locked);
		compoundNBT.putInt("SelectedRecipe", selectedRecipe);

		ListNBT recipesNbt = new ListNBT();
		for (IRecipe recipe : recipes) {
			ResourceLocation recipeKey = recipe.getId();
			recipesNbt.add(new StringNBT(recipeKey.toString()));
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

		data.writeVarInt(recipes.size());
		for (IRecipe recipe : recipes) {
			ResourceLocation recipeId = recipe.getId();
			data.writeString(recipeId.toString());
		}
	}

	@Override
	public void readData(PacketBufferForestry data) throws IOException {
		data.readInventory(craftMatrix);
		locked = data.readBoolean();
		selectedRecipe = data.readVarInt();

		recipes.clear();
		int recipeCount = data.readVarInt();
		for (int i = 0; i < recipeCount; i++) {
			String recipeId = data.readString();
			//TODO sidedness issues
			Map<ResourceLocation, IRecipe<CraftingInventory>> recipeMap = Minecraft.getInstance().player.connection.getRecipeManager().getRecipes(IRecipeType.CRAFTING);
			IRecipe recipe = recipeMap.get(new ResourceLocation(recipeId));
			if (recipe != null) {
				recipes.add(recipe);
			}
		}
	}
}
