/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import java.util.UUID;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public interface IForestryRecipe extends Recipe<Container> {

	static ResourceLocation anonymous() {
		return new ResourceLocation("forestry", "anonymous_" + UUID.randomUUID());
	}

	// <editor-fold desc="Ignore these methods, we just piggy back off Minecraft's system for recipe sync">
	@Deprecated
	@Override
	default boolean matches(Container inv, Level worldIn) {
		return false;
	}

	@Deprecated
	@Override
	default ItemStack assemble(Container inv) {
		return ItemStack.EMPTY;
	}

	@Deprecated
	@Override
	default boolean canCraftInDimensions(int width, int height) {
		return false;
	}

	@Deprecated
	@Override
	default ItemStack getResultItem() {
		return ItemStack.EMPTY;
	}

	@Deprecated
	@Override
	default NonNullList<ItemStack> getRemainingItems(Container inv) {
		return NonNullList.create();
	}

	@Deprecated
	@Override
	default NonNullList<Ingredient> getIngredients() {
		return NonNullList.create();
	}

	@Deprecated
	@Override
	default boolean isSpecial() {
		return true;
	}

	@Deprecated
	@Override
	default String getGroup() {
		return "forestry";
	}

	@Deprecated
	@Override
	default ItemStack getToastSymbol() {
		return ItemStack.EMPTY;
	}
	// </editor-fold>

	@Override
	ResourceLocation getId();

	@Override
	RecipeSerializer<?> getSerializer();

	@Override
	RecipeType<?> getType();
}
