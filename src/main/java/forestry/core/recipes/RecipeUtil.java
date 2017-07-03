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
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import forestry.api.recipes.IDescriptiveRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.utils.ItemStackUtil;
import forestry.factory.inventory.InventoryCraftingForestry;

public abstract class RecipeUtil {
	
	// TODO use json recipes
	
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

	@Nullable
	public static InventoryCraftingForestry getCraftRecipe(NonNullList<ItemStack> recipeItems, NonNullList<ItemStack> availableItems, World world, ItemStack recipeOutput) {
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
		NonNullList<ItemStack> stockCopy = ItemStackUtil.condenseStacks(availableItems);

		for (int slot = 0; slot < recipeItems.size(); slot++) {
			ItemStack recipeStack = recipeItems.get(slot);
			if (recipeStack.isEmpty()) {
				continue;
			}

			// Use crafting equivalent (not oredict) items first
			for (ItemStack stockStack : stockCopy) {
				if (stockStack.getCount() > 0 && ItemStackUtil.isCraftingEquivalent(recipeStack, stockStack, false, false)) {
					ItemStack stack = ItemStackUtil.createCopyWithCount(stockStack, 1);
					stockStack.shrink(1);
					crafting.setInventorySlotContents(slot, stack);
					break;
				}
			}

			// Use oredict items if crafting equivalent items aren't available
			if (crafting.getStackInSlot(slot).isEmpty()) {
				for (ItemStack stockStack : stockCopy) {
					if (stockStack.getCount() > 0 && ItemStackUtil.isCraftingEquivalent(recipeStack, stockStack, true, true)) {
						ItemStack stack = ItemStackUtil.createCopyWithCount(stockStack, 1);
						stockStack.shrink(1);
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

	public static NonNullList<ItemStack> findMatchingRecipes(InventoryCrafting inventory, World world) {
		NonNullList<ItemStack> matchingRecipes = NonNullList.create();
		ItemStack repairRecipe = findRepairRecipe(inventory);
		if (!repairRecipe.isEmpty()) {
			matchingRecipes.add(repairRecipe);
			return matchingRecipes;
		}
		
		for (Object recipe : ForgeRegistries.RECIPES.getValues()) {
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
		ItemStack itemstack0 = ItemStack.EMPTY;
		ItemStack itemstack1 = ItemStack.EMPTY;

		for (int j = 0; j < inventory.getSizeInventory(); j++) {
			ItemStack itemstack = inventory.getStackInSlot(j);

			if (!itemstack.isEmpty()) {
				if (craftIngredientCount == 0) {
					itemstack0 = itemstack;
				}

				if (craftIngredientCount == 1) {
					itemstack1 = itemstack;
				}

				++craftIngredientCount;
			}
		}

		if (craftIngredientCount == 2 && itemstack0.getItem() == itemstack1.getItem() && itemstack0.getCount() == 1 && itemstack1.getCount() == 1 && itemstack0.getItem().isRepairable()) {
			int damage0 = itemstack0.getMaxDamage() - itemstack0.getItemDamage();
			int damage1 = itemstack0.getMaxDamage() - itemstack1.getItemDamage();
			int repairAmount = damage0 + damage1 + itemstack0.getMaxDamage() * 5 / 100;
			int repairedDamage = itemstack0.getMaxDamage() - repairAmount;

			if (repairedDamage < 0) {
				repairedDamage = 0;
			}

			return new ItemStack(itemstack0.getItem(), 1, repairedDamage);
		}

		return ItemStack.EMPTY;
	}

	public static void addRecipe(String recipeName, Block block, Object... obj) {
		addRecipe(recipeName, new ItemStack(block), obj);
	}

	public static void addRecipe(String recipeName, Item item, Object... obj) {
		addRecipe(recipeName, new ItemStack(item), obj);
	}

	public static void addRecipe(String recipeName, ItemStack itemstack, Object... obj) {
		ShapedRecipeCustom recipe = new ShapedRecipeCustom(itemstack, obj);
		recipe.setRegistryName(Constants.MOD_ID, recipeName);
		ForgeRegistries.RECIPES.register(recipe);
	}

	public static void addShapelessRecipe(String recipeName, Item item, Object... obj) {
		addShapelessRecipe(recipeName, new ItemStack(item), obj);
	}

	public static void addShapelessRecipe(String recipeName, ItemStack itemstack, Object... obj) {
		ShapelessOreRecipe recipe = new ShapelessOreRecipe(null, itemstack, obj);
		recipe.setRegistryName(Constants.MOD_ID, recipeName);
		ForgeRegistries.RECIPES.register(recipe);
	}

	public static void addSmelting(ItemStack res, Item prod, float xp) {
		addSmelting(res, new ItemStack(prod), xp);
	}

	public static void addSmelting(ItemStack res, ItemStack prod, float xp) {
		GameRegistry.addSmelting(res, prod, xp);
	}

	public static String[][] matches(IDescriptiveRecipe recipe, IInventory inventoryCrafting) {
		NonNullList<NonNullList<ItemStack>> recipeIngredients = recipe.getRawIngredients();
		NonNullList<String> oreDicts = recipe.getOreDicts();
		int width = recipe.getWidth();
		int height = recipe.getHeight();
		return matches(recipeIngredients, oreDicts, width, height, inventoryCrafting);
	}

	public static String[][] matches(NonNullList<NonNullList<ItemStack>> recipeIngredients, NonNullList<String> oreDicts, int width, int height, IInventory inventoryCrafting) {
		ItemStack[][] resources = getResources(inventoryCrafting);
		return matches(recipeIngredients, oreDicts, width, height, resources);
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

	@Nullable
	public static String[][] matches(NonNullList<NonNullList<ItemStack>> recipeIngredients, NonNullList<String> oreDicts, int width, int height, ItemStack[][] resources) {
		for (int i = 0; i <= 3 - width; i++) {
			for (int j = 0; j <= 3 - height; j++) {
				String[][] resourceDicts = checkMatch(recipeIngredients, oreDicts, width, height, resources, i, j, true);
				if (resourceDicts != null) {
					return resourceDicts;
				}

				resourceDicts = checkMatch(recipeIngredients, oreDicts, width, height, resources, i, j, false);
				if (resourceDicts != null) {
					return resourceDicts;
				}
			}
		}

		return null;
	}

	@Nullable
	private static String[][] checkMatch(NonNullList<NonNullList<ItemStack>> recipeIngredients, NonNullList<String> oreDicts, int width, int height, ItemStack[][] resources, int xInGrid, int yInGrid, boolean mirror) {
		String[][] resourceDicts = new String[3][3];
		for (int k = 0; k < 3; k++) {
			for (int l = 0; l < 3; l++) {
				ItemStack resource = resources[k][l];

				int widthIt = k - xInGrid;
				int heightIt = l - yInGrid;
				NonNullList<ItemStack> recipeIngredient = null;
				String oreDict = "";

				if (widthIt >= 0 && heightIt >= 0 && widthIt < width && heightIt < height) {
					int position;
					if (mirror) {
						position = width - widthIt - 1 + heightIt * width;
					} else {
						position = widthIt + heightIt * width;
					}
					recipeIngredient = recipeIngredients.get(position);
					oreDict = oreDicts.get(position);
				}

				if (!checkIngredientMatch(recipeIngredient, resource)) {
					return null;
				}
				resourceDicts[k][l] = oreDict;
			}
		}

		return resourceDicts;
	}

	private static boolean checkIngredientMatch(@Nullable NonNullList<ItemStack> recipeIngredient, ItemStack resource) {
		if (recipeIngredient == null || recipeIngredient.isEmpty()) {
			return resource.isEmpty();
		}
		for (ItemStack item : recipeIngredient) {
			if (ItemStackUtil.isCraftingEquivalent(item, resource)) {
				return true;
			}
		}
		return false;
	}
}
