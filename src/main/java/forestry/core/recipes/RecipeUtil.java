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
package forestry.core.recipes;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import forestry.api.recipes.IDescriptiveRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.core.fluids.Fluids;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.factory.inventory.InventoryCraftingForestry;

public abstract class RecipeUtil {

	public static void addFermenterRecipes(ItemStack resource, int fermentationValue, Fluids output) {
		if (RecipeManagers.fermenterManager == null) {
			return;
		}
		
		RecipeManagers.fermenterManager.addRecipe(resource, fermentationValue, 1.0f, output.getFluid(1), new FluidStack(FluidRegistry.WATER, 1));

		if (FluidRegistry.isFluidRegistered(Fluids.JUICE.getFluid())) {
			RecipeManagers.fermenterManager.addRecipe(resource, fermentationValue, 1.5f, output.getFluid(1), Fluids.JUICE.getFluid(1));
		}

		if (FluidRegistry.isFluidRegistered(Fluids.FOR_HONEY.getFluid())) {
			RecipeManagers.fermenterManager.addRecipe(resource, fermentationValue, 1.5f, output.getFluid(1), Fluids.FOR_HONEY.getFluid(1));
		}
	}

	/**
	 * Returns a list of the ore dictionary names if they exist.
	 * Returns a list containing itemStack if there are no ore dictionary names.
	 * Used for creating recipes that should accept equivalent itemStacks, based on the ore dictionary.
	 */
	public static List getOreDictRecipeEquivalents(ItemStack itemStack) {
		int[] oreDictIds = OreDictionary.getOreIDs(itemStack);
		List<String> oreDictNames = new ArrayList<>(oreDictIds.length);
		for (int oreId : oreDictIds) {
			String oreDictName = OreDictionary.getOreName(oreId);
			oreDictNames.add(oreDictName);
		}
		if (oreDictNames.isEmpty()) {
			return Collections.singletonList(itemStack);
		}
		return oreDictNames;
	}

	public static Object[] getCraftingRecipeAsArray(IDescriptiveRecipe recipe) {

		try {
			return getShapedRecipeAsArray(recipe.getWidth(), recipe.getHeight(), recipe.getIngredients(), recipe.getRecipeOutput());
		} catch (RuntimeException ex) {
			Log.warning("Exception while trying to parse an ItemStack[10] from an IRecipe:", ex);
		}

		return null;
	}

	private static Object[] getShapedRecipeAsArray(int width, int height, Object[] ingredients, ItemStack output) {
		Object[] result = new Object[10];

		for (int y = 0; y < height; y++) {
			System.arraycopy(ingredients, y * width, result, y * 3, width);
		}

		result[9] = output;
		return result;
	}
	
	@Nullable
	public static InventoryCraftingForestry getCraftRecipe(InventoryCrafting originalCrafting, ItemStack[] availableItems, World world, IRecipe recipe) {
		if (!recipe.matches(originalCrafting, world)) {
			return null;
		}
		
		ItemStack expectedOutput = recipe.getCraftingResult(originalCrafting);
		if (expectedOutput == null) {
			return null;
		}
		
		InventoryCraftingForestry crafting = new InventoryCraftingForestry();
		ItemStack[] stockCopy = ItemStackUtil.condenseStacks(availableItems);
		
		for (int slot = 0; slot < originalCrafting.getSizeInventory(); slot++) {
			ItemStack stackInSlot = originalCrafting.getStackInSlot(slot);
			if (stackInSlot != null) {
				ItemStack equivalent = getCraftingEquivalent(stockCopy, originalCrafting, slot, world, recipe, expectedOutput);
				if (equivalent == null) {
					return null;
				} else {
					crafting.setInventorySlotContents(slot, equivalent);
				}
			}
		}
		
		if (recipe.matches(crafting, world)) {
			ItemStack output = recipe.getCraftingResult(crafting);
			if (ItemStack.areItemStacksEqual(output, expectedOutput)) {
				return crafting;
			}
		}

		return null;
	}
	
	@Nullable
	private static ItemStack getCraftingEquivalent(ItemStack[] stockCopy, InventoryCrafting crafting, int slot, World world, IRecipe recipe, ItemStack expectedOutput)
	{
		ItemStack originalStack = crafting.getStackInSlot(slot);
		for (ItemStack stockStack : stockCopy)
		{
			if (stockStack != null && stockStack.stackSize > 0)
			{
				ItemStack singleStockStack = ItemStackUtil.createCopyWithCount(stockStack, 1);
				crafting.setInventorySlotContents(slot, singleStockStack);
				if (recipe.matches(crafting, world))
				{
					ItemStack output = recipe.getCraftingResult(crafting);
					if (ItemStack.areItemStacksEqual(output, expectedOutput))
					{
						crafting.setInventorySlotContents(slot, originalStack);
						return stockStack.splitStack(1);
					}
				}
			}
		}
		crafting.setInventorySlotContents(slot, originalStack);
		return null;
	}
	
	public static List<IRecipe> findMatchingRecipes(InventoryCrafting inventory, World world) {
		List<IRecipe> matchingRecipes = new ArrayList<>();
		for (IRecipe recipe : CraftingManager.getInstance().getRecipeList()) {
			if (recipe.matches(inventory, world)) {
				matchingRecipes.add(recipe);
			}
		}
		
		return matchingRecipes;
	}

	public static void addRecipe(Block block, Object... obj) {
		addRecipe(new ItemStack(block), obj);
	}

	public static void addRecipe(Item item, Object... obj) {
		addRecipe(new ItemStack(item), obj);
	}

	public static void addRecipe(ItemStack itemstack, Object... obj) {
		CraftingManager.getInstance().getRecipeList().add(new ShapedRecipeCustom(itemstack, obj));
	}

	public static void addPriorityRecipe(ItemStack itemStack, Object... obj) {
		CraftingManager.getInstance().getRecipeList().add(0, new ShapedRecipeCustom(itemStack, obj));
	}

	public static void addShapelessRecipe(Item item, Object... obj) {
		addShapelessRecipe(new ItemStack(item), obj);
	}

	public static void addShapelessRecipe(ItemStack itemstack, Object... obj) {
		CraftingManager.getInstance().getRecipeList().add(new ShapelessOreRecipe(itemstack, obj));
	}

	public static void addSmelting(ItemStack res, Item prod, float xp) {
		addSmelting(res, new ItemStack(prod), xp);
	}

	public static void addSmelting(ItemStack res, ItemStack prod, float xp) {
		if (res == null || res.getItem() == null) {
			throw new IllegalArgumentException("Tried to register smelting recipe with null input");
		}
		if (prod == null || prod.getItem() == null) {
			throw new IllegalArgumentException("Tried to register smelting recipe with null output");
		}
		GameRegistry.addSmelting(res, prod, xp);
	}

	public static boolean matches(IDescriptiveRecipe recipe, IInventory inventoryCrafting) {
		Object[] recipeIngredients = recipe.getIngredients();
		int width = recipe.getWidth();
		int height = recipe.getHeight();
		return matches(recipeIngredients, width, height, inventoryCrafting);
	}

	public static boolean matches(Object[] recipeIngredients, int width, int height, IInventory inventoryCrafting) {
		ItemStack[][] resources = getResources(inventoryCrafting);
		return matches(recipeIngredients, width, height, resources);
	}

	public static ItemStack[][] getResources(IInventory inventoryCrafting) {
		ItemStack[][] resources = new ItemStack[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int k = i + j * 3;
				resources[i][j] = inventoryCrafting.getStackInSlot(k);
			}
		}
		return resources;
	}

	public static boolean matches(Object[] recipeIngredients, int width, int height, ItemStack[][] resources) {
		for (int i = 0; i <= 3 - width; i++) {
			for (int j = 0; j <= 3 - height; j++) {
				if (checkMatch(recipeIngredients, width, height, resources, i, j, true)) {
					return true;
				}

				if (checkMatch(recipeIngredients, width, height, resources, i, j, false)) {
					return true;
				}
			}
		}

		return false;
	}

	private static boolean checkMatch(Object[] recipeIngredients, int width, int height, ItemStack[][] resources, int xInGrid, int yInGrid, boolean mirror) {
		for (int k = 0; k < 3; k++) {
			for (int l = 0; l < 3; l++) {
				ItemStack resource = resources[k][l];

				int widthIt = k - xInGrid;
				int heightIt = l - yInGrid;
				Object recipeIngredient = null;

				if (widthIt >= 0 && heightIt >= 0 && widthIt < width && heightIt < height) {
					if (mirror) {
						recipeIngredient = recipeIngredients[width - widthIt - 1 + heightIt * width];
					} else {
						recipeIngredient = recipeIngredients[widthIt + heightIt * width];
					}
				}

				if (!checkIngredientMatch(recipeIngredient, resource)) {
					return false;
				}
			}
		}

		return true;
	}

	private static boolean checkIngredientMatch(Object recipeIngredient, ItemStack resource) {
		if (recipeIngredient == null && resource == null) {
			return true;
		} else if (recipeIngredient instanceof ItemStack) {
			return ItemStackUtil.isCraftingEquivalent((ItemStack) recipeIngredient, resource);
		} else if (recipeIngredient instanceof Iterable) {
			for (Object item : (Iterable) recipeIngredient) {
				if (checkIngredientMatch(item, resource)) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}
}
