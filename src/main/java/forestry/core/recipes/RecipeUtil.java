/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.core.recipes;

import forestry.core.utils.ItemStackUtil;
import forestry.worktable.inventory.CraftingInventoryForestry;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public abstract class RecipeUtil {
    @Nullable
    public static CraftingInventoryForestry getCraftRecipe(
            CraftingInventory originalCrafting,
            NonNullList<ItemStack> availableItems,
            World world,
            IRecipe recipe
    ) {
        if (!recipe.matches(originalCrafting, world)) {
            return null;
        }

        ItemStack expectedOutput = recipe.getCraftingResult(originalCrafting);
        if (expectedOutput.isEmpty()) {
            return null;
        }

        CraftingInventoryForestry crafting = new CraftingInventoryForestry();
        NonNullList<ItemStack> stockCopy = ItemStackUtil.condenseStacks(availableItems);

        for (int slot = 0; slot < originalCrafting.getSizeInventory(); slot++) {
            ItemStack stackInSlot = originalCrafting.getStackInSlot(slot);
            if (!stackInSlot.isEmpty()) {
                ItemStack equivalent = getCraftingEquivalent(
                        stockCopy,
                        originalCrafting,
                        slot,
                        world,
                        recipe,
                        expectedOutput
                );
                if (equivalent.isEmpty()) {
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

    private static ItemStack getCraftingEquivalent(
            NonNullList<ItemStack> stockCopy,
            CraftingInventory crafting,
            int slot,
            World world,
            IRecipe recipe,
            ItemStack expectedOutput
    ) {
        ItemStack originalStack = crafting.getStackInSlot(slot);
        for (ItemStack stockStack : stockCopy) {
            if (!stockStack.isEmpty()) {
                ItemStack singleStockStack = ItemStackUtil.createCopyWithCount(stockStack, 1);
                crafting.setInventorySlotContents(slot, singleStockStack);
                if (recipe.matches(crafting, world)) {
                    ItemStack output = recipe.getCraftingResult(crafting);
                    if (ItemStack.areItemStacksEqual(output, expectedOutput)) {
                        crafting.setInventorySlotContents(slot, originalStack);
                        return stockStack.split(1);
                    }
                }
            }
        }

        crafting.setInventorySlotContents(slot, originalStack);
        return ItemStack.EMPTY;
    }

    public static List<IRecipe> findMatchingRecipes(
            CraftingInventory inventory,
            World world
    ) {    //TODO - is the stream() needed anymore?
        return world.getRecipeManager()
                    .getRecipes(IRecipeType.CRAFTING, inventory, world)
                    .stream()
                    .filter(recipe -> recipe.matches(inventory, world))
                    .collect(Collectors.toList());
    }

    @Nullable
    public static Ingredient[][] matches(ShapedRecipe recipe, IInventory inventory) {
        NonNullList<Ingredient> recipeIngredients = recipe.getIngredients();
        int width = recipe.getWidth();
        int height = recipe.getHeight();
        return matches(recipeIngredients, width, height, inventory);
    }

    @Nullable
    public static Ingredient[][] matches(
            NonNullList<Ingredient> recipeIngredients,
            int width,
            int height,
            IInventory inventory
    ) {
        ItemStack[][] resources = getResources(inventory);
        return matches(recipeIngredients, width, height, resources);
    }

    public static ItemStack[][] getResources(IInventory inventory) {
        ItemStack[][] resources = new ItemStack[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int k = i + j * 3;
                resources[i][j] = inventory.getStackInSlot(k);
            }
        }

        return resources;
    }

    @Nullable
    public static Ingredient[][] matches(
            NonNullList<Ingredient> recipeIngredients,
            int width,
            int height,
            ItemStack[][] resources
    ) {
        for (int i = 0; i <= 3 - width; i++) {
            for (int j = 0; j <= 3 - height; j++) {
                Ingredient[][] resourceDicts = checkMatch(
                        recipeIngredients,
                        width,
                        height,
                        resources,
                        i,
                        j,
                        true
                );
                if (resourceDicts != null) {
                    return resourceDicts;
                }

                resourceDicts = checkMatch(recipeIngredients, width, height, resources, i, j, false);
                if (resourceDicts != null) {
                    return resourceDicts;
                }
            }
        }

        return null;
    }

    @Nullable
    private static Ingredient[][] checkMatch(
            NonNullList<Ingredient> recipeIngredients,
            int width,
            int height,
            ItemStack[][] resources,
            int xInGrid,
            int yInGrid,
            boolean mirror
    ) {
        Ingredient[][] resourceDicts = new Ingredient[3][3];
        for (int k = 0; k < 3; k++) {
            for (int l = 0; l < 3; l++) {
                ItemStack resource = resources[k][l];

                int widthIt = k - xInGrid;
                int heightIt = l - yInGrid;
                Ingredient recipeIngredient = null;

                if (widthIt >= 0 && heightIt >= 0 && widthIt < width && heightIt < height) {
                    int position;
                    if (mirror) {
                        position = width - widthIt - 1 + heightIt * width;
                    } else {
                        position = widthIt + heightIt * width;
                    }

                    recipeIngredient = recipeIngredients.get(position);
                }

                if (!checkIngredientMatch(recipeIngredient, resource)) {
                    return null;
                }

                resourceDicts[k][l] = recipeIngredient;
            }
        }

        return resourceDicts;
    }

    private static boolean checkIngredientMatch(@Nullable Ingredient recipeIngredient, ItemStack resource) {
        if (recipeIngredient == null || recipeIngredient.hasNoMatchingItems()) {
            return resource.isEmpty();
        }

        return recipeIngredient.test(resource);
    }
}
