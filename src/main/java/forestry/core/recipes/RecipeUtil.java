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

import forestry.api.recipes.RecipeManagers;
import forestry.core.fluids.ForestryFluids;
import forestry.core.utils.ItemStackUtil;
import forestry.worktable.inventory.CraftingInventoryForestry;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public abstract class RecipeUtil {
    // TODO use json recipes
    public static void addFermenterRecipes(ItemStack resource, int fermentationValue, ForestryFluids output) {
        if (RecipeManagers.fermenterManager == null) {
            return;
        }

        FluidStack outputStack = output.getFluid(1);
        if (outputStack.isEmpty()) {
            return;
        }

        RecipeManagers.fermenterManager.addRecipe(
                resource,
                fermentationValue,
                1.0f,
                outputStack,
                new FluidStack(Fluids.WATER, 1)
        );

        if (ForgeRegistries.FLUIDS.containsValue(ForestryFluids.JUICE.getFluid())) {
            RecipeManagers.fermenterManager.addRecipe(
                    resource,
                    fermentationValue,
                    1.5f,
                    outputStack,
                    ForestryFluids.JUICE.getFluid(1)
            );
        }

        if (ForgeRegistries.FLUIDS.containsValue(ForestryFluids.HONEY.getFluid())) {
            RecipeManagers.fermenterManager.addRecipe(
                    resource,
                    fermentationValue,
                    1.5f,
                    outputStack,
                    ForestryFluids.HONEY.getFluid(1)
            );
        }
    }

    public static void addFermenterRecipes(String resource, int fermentationValue, ForestryFluids output) {
        if (RecipeManagers.fermenterManager == null) {
            return;
        }

        FluidStack outputStack = output.getFluid(1);
        if (outputStack.isEmpty()) {
            return;
        }

        RecipeManagers.fermenterManager.addRecipe(
                resource,
                fermentationValue,
                1.0f,
                outputStack,
                new FluidStack(Fluids.WATER, 1)
        );

        if (ForgeRegistries.FLUIDS.containsValue(ForestryFluids.JUICE.getFluid())) {
            RecipeManagers.fermenterManager.addRecipe(
                    resource,
                    fermentationValue,
                    1.5f,
                    outputStack,
                    ForestryFluids.JUICE.getFluid(1)
            );
        }

        if (ForgeRegistries.FLUIDS.containsValue(ForestryFluids.HONEY.getFluid())) {
            RecipeManagers.fermenterManager.addRecipe(
                    resource,
                    fermentationValue,
                    1.5f,
                    outputStack,
                    ForestryFluids.HONEY.getFluid(1)
            );
        }
    }

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

    //TODO - smelting needs to be json now?
    public static void addSmelting(ItemStack res, Item prod, float xp) {
        addSmelting(res, new ItemStack(prod), xp);
    }

    public static void addSmelting(ItemStack res, ItemStack prod, float xp) {
        //		GameRegistry.addSmelting(res, prod, xp);
    }

    @Nullable
    public static String[][] matches(ShapedRecipe recipe, IInventory CraftingInventory) {
        NonNullList<Ingredient> recipeIngredients = recipe.getIngredients();
        NonNullList<String> oreDicts = NonNullList.create();
        int width = recipe.getWidth();
        int height = recipe.getHeight();
        return matches(recipeIngredients, oreDicts, width, height, CraftingInventory);
    }

    @Nullable
    public static String[][] matches(
            NonNullList<Ingredient> recipeIngredients,
            NonNullList<String> oreDicts,
            int width,
            int height,
            IInventory CraftingInventory
    ) {
        ItemStack[][] resources = getResources(CraftingInventory);
        return matches(recipeIngredients, oreDicts, width, height, resources);
    }

    public static ItemStack[][] getResources(IInventory CraftingInventory) {
        ItemStack[][] resources = new ItemStack[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int k = i + j * 3;
                resources[i][j] = CraftingInventory.getStackInSlot(k);
            }
        }

        return resources;
    }

    @Nullable
    public static String[][] matches(
            NonNullList<Ingredient> recipeIngredients,
            NonNullList<String> oreDicts,
            int width,
            int height,
            ItemStack[][] resources
    ) {
        for (int i = 0; i <= 3 - width; i++) {
            for (int j = 0; j <= 3 - height; j++) {
                String[][] resourceDicts = checkMatch(
                        recipeIngredients,
                        oreDicts,
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

                resourceDicts = checkMatch(recipeIngredients, oreDicts, width, height, resources, i, j, false);
                if (resourceDicts != null) {
                    return resourceDicts;
                }
            }
        }

        return null;
    }

    @Nullable
    private static String[][] checkMatch(
            NonNullList<Ingredient> recipeIngredients,
            NonNullList<String> oreDicts,
            int width,
            int height,
            ItemStack[][] resources,
            int xInGrid,
            int yInGrid,
            boolean mirror
    ) {
        String[][] resourceDicts = new String[3][3];
        for (int k = 0; k < 3; k++) {
            for (int l = 0; l < 3; l++) {
                ItemStack resource = resources[k][l];

                int widthIt = k - xInGrid;
                int heightIt = l - yInGrid;
                Ingredient recipeIngredient = null;
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

    private static boolean checkIngredientMatch(@Nullable Ingredient recipeIngredient, ItemStack resource) {
        if (recipeIngredient == null || recipeIngredient.hasNoMatchingItems()) {
            return resource.isEmpty();
        }

        return recipeIngredient.test(resource);
    }
}
