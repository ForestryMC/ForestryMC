/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.UUID;

public interface IForestryRecipe extends IRecipe<IInventory> {

    static ResourceLocation anonymous() {
        return new ResourceLocation("forestry", "anonymous_" + UUID.randomUUID());
    }

    // <editor-fold desc="Ignore these methods, we just piggy back off Minecraft's system for recipe sync">
    @Deprecated
    @Override
    default boolean matches(IInventory inv, World worldIn) {
        return false;
    }

    @Deprecated
    @Override
    default ItemStack getCraftingResult(IInventory inv) {
        return ItemStack.EMPTY;
    }

    @Deprecated
    @Override
    default boolean canFit(int width, int height) {
        return false;
    }

    @Deprecated
    @Override
    default ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Deprecated
    @Override
    default NonNullList<ItemStack> getRemainingItems(IInventory inv) {
        return NonNullList.create();
    }

    @Deprecated
    @Override
    default NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }

    @Deprecated
    @Override
    default boolean isDynamic() {
        return true;
    }

    @Deprecated
    @Override
    default String getGroup() {
        return "forestry";
    }

    @Deprecated
    @Override
    default ItemStack getIcon() {
        return ItemStack.EMPTY;
    }
    // </editor-fold>

    @Override
    ResourceLocation getId();

    @Override
    IRecipeSerializer<?> getSerializer();

    @Override
    IRecipeType<?> getType();
}
