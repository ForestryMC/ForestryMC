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
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;
import forestry.core.proxy.Proxies;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.InventoryUtil;
import forestry.factory.inventory.InventoryCraftingForestry;

public final class MemorizedRecipe implements INbtWritable, INbtReadable, IStreamable {
	private InventoryCraftingForestry craftMatrix = new InventoryCraftingForestry();
	private List<IRecipe> recipes = new ArrayList<>();
	private int selectedRecipe;
	private long lastUsed;
	private boolean locked;
	
	public MemorizedRecipe(DataInputStreamForestry data, World world) throws IOException {
		readData(data, world);
	}
	
	public MemorizedRecipe(NBTTagCompound nbt) {
		readFromNBT(nbt);
	}
	
	public MemorizedRecipe(InventoryCraftingForestry craftMatrix, List<IRecipe> recipes) {
		InventoryUtil.deepCopyInventoryContents(craftMatrix, this.craftMatrix);
		this.recipes = recipes;
	}
	
	public InventoryCraftingForestry getCraftMatrix() {
		return craftMatrix;
	}
	
	public void setCraftMatrix(InventoryCraftingForestry craftMatrix) {
		this.craftMatrix = craftMatrix;
	}
	
	public void validate(World world) {
		recipes = RecipeUtil.findMatchingRecipes(craftMatrix, world);
		if (selectedRecipe > recipes.size()) {
			selectedRecipe = 0;
		}
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
	
	@Nullable
	public ItemStack getOutputIcon() {
		IRecipe selectedRecipe = getSelectedRecipe();
		if (selectedRecipe != null) {
			ItemStack recipeOutput = selectedRecipe.getCraftingResult(craftMatrix);
			if (recipeOutput != null) {
				return recipeOutput;
			}
		}
		return null;
	}
	
	@Nullable
	public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting, World world) {
		IRecipe selectedRecipe = getSelectedRecipe();
		if (selectedRecipe != null && selectedRecipe.matches(inventoryCrafting, world)) {
			ItemStack recipeOutput = selectedRecipe.getCraftingResult(inventoryCrafting);
			if (recipeOutput != null) {
				return recipeOutput;
			}
		}
		return null;
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
	
	/* INbtWritable */
	@Override
	public final void readFromNBT(NBTTagCompound nbttagcompound) {
		InventoryUtil.readFromNBT(craftMatrix, nbttagcompound);
		lastUsed = nbttagcompound.getLong("LastUsed");
		locked = nbttagcompound.getBoolean("Locked");
		
		if (nbttagcompound.hasKey("SelectedRecipe")) {
			selectedRecipe = nbttagcompound.getInteger("SelectedRecipe");
		}
		
		recipes.clear();
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
	public void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeInventory(craftMatrix);
		data.writeBoolean(locked);
		data.writeVarInt(selectedRecipe);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void readData(DataInputStreamForestry data) throws IOException {
		readData(data, Proxies.common.getRenderWorld());
	}
	
	public void readData(DataInputStreamForestry data, World world) throws IOException {
		data.readInventory(craftMatrix);
		locked = data.readBoolean();
		selectedRecipe = data.readVarInt();
		
		recipes.clear();
		validate(world);
	}
}
