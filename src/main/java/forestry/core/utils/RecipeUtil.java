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
package forestry.core.utils;

import forestry.api.recipes.RecipeManagers;
import forestry.core.fluids.Fluids;
import forestry.core.gui.ContainerDummy;
import forestry.core.interfaces.IDescriptiveRecipe;
import forestry.core.proxy.Proxies;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeUtil {

	private static final Container DUMMY_CONTAINER = new ContainerDummy();

	public static void injectLeveledRecipe(ItemStack resource, int fermentationValue, Fluids output) {
		if (RecipeManagers.fermenterManager == null)
			return;

		RecipeManagers.fermenterManager.addRecipe(resource, fermentationValue, 1.0f, output.get(1), Fluids.WATER.get(1));

		if (FluidRegistry.isFluidRegistered(Fluids.JUICE.get()))
			RecipeManagers.fermenterManager.addRecipe(resource, fermentationValue, 1.5f, output.get(1), Fluids.JUICE.get(1));

		if (FluidRegistry.isFluidRegistered(Fluids.HONEY.get()))
			RecipeManagers.fermenterManager.addRecipe(resource, fermentationValue, 1.5f, output.get(1), Fluids.HONEY.get(1));
	}

	/**
	 * Returns a list of the ore dictionary names if they exist.
	 * Returns a list containing itemStack if there are no ore dictionary names.
	 * Used for creating recipes that should accept equivalent itemStacks, based on the ore dictionary.
	 */
	public static List getOreDictRecipeEquivalents(ItemStack itemStack) {
		int[] oreDictIds = OreDictionary.getOreIDs(itemStack);
		List<String> oreDictNames = new ArrayList<String>(oreDictIds.length);
		for (int oreId : oreDictIds) {
			String oreDictName = OreDictionary.getOreName(oreId);
			oreDictNames.add(oreDictName);
		}
		if (oreDictNames.isEmpty())
			return Collections.singletonList(itemStack);
		return oreDictNames;
	}

	public static Object[] getCraftingRecipeAsArray(Object rec) {

		try {

			if (rec instanceof IDescriptiveRecipe) {

				IDescriptiveRecipe recipe = (IDescriptiveRecipe) rec;
				return getShapedRecipeAsArray(recipe.getWidth(), recipe.getHeight(), recipe.getIngredients(), recipe.getRecipeOutput());

			}

		} catch (Exception ex) {
			Proxies.log.warning("Exception while trying to parse an ItemStack[10] from an IRecipe:");
			Proxies.log.warning(ex.getMessage());
		}

		return null;
	}

	/*
	 * private static Object[] getSmallShapedRecipeAsArray(int width, int height, Object[] ingredients, ItemStack output) { Object[] result = new Object[5];
	 * 
	 * for(int y = 0; y < height; y++) for(int x = 0; x < width; x++) result[y * 2 + x] = ingredients[y * width + x];
	 * 
	 * result[4] = output; return result; }
	 */
	private static Object[] getShapedRecipeAsArray(int width, int height, Object[] ingredients, ItemStack output) {
		Object[] result = new Object[10];

		for (int y = 0; y < height; y++) {
			System.arraycopy(ingredients, y * width, result, y * 3, width);
		}

		result[9] = output;
		return result;
	}

	public static boolean canCraftRecipe(World world, ItemStack[] recipeItems, ItemStack recipeOutput, ItemStack[] availableItems) {
		// Need at least one matched set
		if (StackUtils.containsSets(recipeItems, availableItems, true, true) == 0)
			return false;

		// Check that it doesn't make a different recipe.
		// For example:
		// Wood Logs are all ore dictionary equivalent with each other,
		// but an Oak Log shouldn't be able to make Ebony Wood Planks
		// because it makes Oak Wood Planks using the same recipe.
		// Strategy:
		// Create a fake crafting inventory using items we have in availableItems
		// in place of the ones in the saved crafting inventory.
		// Check that the recipe it makes is the same as the currentRecipe.
		InventoryCrafting crafting = new InventoryCrafting(DUMMY_CONTAINER, 3, 3);
		ItemStack[] stockCopy = StackUtils.condenseStacks(availableItems);

		for (int slot = 0; slot < recipeItems.length; slot++) {
			ItemStack recipeStack = recipeItems[slot];
			if (recipeStack == null)
				continue;

			// Use crafting equivalent (not oredict) items first
			for (ItemStack stockStack : stockCopy) {
				if (stockStack.stackSize > 0 && StackUtils.isCraftingEquivalent(recipeStack, stockStack, false, false)) {
					ItemStack stack = new ItemStack(stockStack.getItem(), 1, stockStack.getItemDamage());
					stockStack.stackSize--;
					crafting.setInventorySlotContents(slot, stack);
					break;
				}
			}

			// Use oredict items if crafting equivalent items aren't available
			if (crafting.getStackInSlot(slot) == null) {
				for (ItemStack stockStack : stockCopy) {
					if (stockStack.stackSize > 0 && StackUtils.isCraftingEquivalent(recipeStack, stockStack, true, true)) {
						ItemStack stack = new ItemStack(stockStack.getItem(), 1, stockStack.getItemDamage());
						stockStack.stackSize--;
						crafting.setInventorySlotContents(slot, stack);
						break;
					}
				}
			}
		}
		ItemStack output = CraftingManager.getInstance().findMatchingRecipe(crafting, world);

		return ItemStack.areItemStacksEqual(output, recipeOutput);
	}
}
