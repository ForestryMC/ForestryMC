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

import javax.annotation.Nonnull;
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

	public static InventoryCraftingForestry getCraftRecipe(ItemStack[] recipeItems, ItemStack[] availableItems, World world, ItemStack recipeOutput) {
		// Need at least one matched set
		if (ItemStackUtil.containsSets(recipeItems, availableItems, true, true) == 0) {
			return null;
		}

		// Check that it doesn't make a different recipe.
		// For example:
		// Wood Logs are all ore dictionary equivalent with each other,
		// but an Oak Log shouldn't be able to make Ebony Wood Planks
		// because it makes Oak Wood Planks using the same recipe.
		// Strategy:
		// Create a fake crafting inventory using items we have in availableItems
		// in place of the ones in the saved crafting inventory.
		// Check that the recipe it makes is the same as the currentRecipe.
		InventoryCraftingForestry crafting = new InventoryCraftingForestry();
		ItemStack[] stockCopy = ItemStackUtil.condenseStacks(availableItems);

		for (int slot = 0; slot < recipeItems.length; slot++) {
			ItemStack recipeStack = recipeItems[slot];
			if (recipeStack == null) {
				continue;
			}

			// Use crafting equivalent (not oredict) items first
			for (ItemStack stockStack : stockCopy) {
				if (stockStack.stackSize > 0 && ItemStackUtil.isCraftingEquivalent(recipeStack, stockStack, false, false)) {
					ItemStack stack = ItemStackUtil.createSplitStack(stockStack, 1);
					stockStack.stackSize--;
					crafting.setInventorySlotContents(slot, stack);
					break;
				}
			}

			// Use oredict items if crafting equivalent items aren't available
			if (crafting.getStackInSlot(slot) == null) {
				for (ItemStack stockStack : stockCopy) {
					if (stockStack.stackSize > 0 && ItemStackUtil.isCraftingEquivalent(recipeStack, stockStack, true, true)) {
						ItemStack stack = ItemStackUtil.createSplitStack(stockStack, 1);
						stockStack.stackSize--;
						crafting.setInventorySlotContents(slot, stack);
						break;
					}
				}
			}
		}

		List<ItemStack> outputs = findMatchingRecipes(crafting, world);
		if (!ItemStackUtil.containsItemStack(outputs, recipeOutput)) {
			return null;
		}
		return crafting;
	}

	@Nonnull
	public static List<ItemStack> findMatchingRecipes(InventoryCrafting inventory, World world) {
		ItemStack repairRecipe = findRepairRecipe(inventory);
		if (repairRecipe != null) {
			return Collections.singletonList(repairRecipe);
		}

		List<ItemStack> matchingRecipes = new ArrayList<>();

		for (Object recipe : CraftingManager.getInstance().getRecipeList()) {
			IRecipe irecipe = (IRecipe) recipe;

			if (irecipe.matches(inventory, world)) {
				ItemStack result = irecipe.getCraftingResult(inventory);
				if (!ItemStackUtil.containsItemStack(matchingRecipes, result)) {
					matchingRecipes.add(result);
				}
			}
		}

		return matchingRecipes;
	}

	private static ItemStack findRepairRecipe(InventoryCrafting inventory) {
		int craftIngredientCount = 0;
		ItemStack itemstack0 = null;
		ItemStack itemstack1 = null;

		for (int j = 0; j < inventory.getSizeInventory(); j++) {
			ItemStack itemstack = inventory.getStackInSlot(j);

			if (itemstack != null) {
				if (craftIngredientCount == 0) {
					itemstack0 = itemstack;
				}

				if (craftIngredientCount == 1) {
					itemstack1 = itemstack;
				}

				++craftIngredientCount;
			}
		}

		if (craftIngredientCount == 2 && itemstack0.getItem() == itemstack1.getItem() && itemstack0.stackSize == 1 && itemstack1.stackSize == 1 && itemstack0.getItem().isRepairable()) {
			Item item = itemstack0.getItem();
			int damage0 = item.getMaxDamage() - itemstack0.getItemDamage();
			int damage1 = item.getMaxDamage() - itemstack1.getItemDamage();
			int repairAmount = damage0 + damage1 + item.getMaxDamage() * 5 / 100;
			int repairedDamage = item.getMaxDamage() - repairAmount;

			if (repairedDamage < 0) {
				repairedDamage = 0;
			}

			return new ItemStack(itemstack0.getItem(), 1, repairedDamage);
		}

		return null;
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
