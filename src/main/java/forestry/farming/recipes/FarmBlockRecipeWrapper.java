///*******************************************************************************
// * Copyright (c) 2011-2014 SirSengir.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the GNU Lesser Public License v3
// * which accompanies this distribution, and is available at
// * http://www.gnu.org/licenses/lgpl-3.0.txt
// *
// * Various Contributors including, but not limited to:
// * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
// ******************************************************************************/
//package forestry.farming.recipes;
//
//import javax.annotation.Nullable;
//import java.util.List;
//
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.crafting.Ingredient;
//import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.util.NonNullList;
//
//import forestry.farming.ModuleFarming;
//import forestry.farming.models.EnumFarmBlockTexture;
//
//import mezz.jei.api.ingredients.IIngredients;
//import mezz.jei.api.recipe.IStackHelper;
//import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
//
//public class FarmBlockRecipeWrapper implements IShapedCraftingRecipeWrapper {
//
//	private FarmBlockRecipe recipe;
//	@Nullable
//	private static ItemStack input;
//	private CompoundNBT textureTag = new CompoundNBT();
//	private IStackHelper helper;
//
//	public FarmBlockRecipeWrapper(FarmBlockRecipe recipe, EnumFarmBlockTexture texture, IStackHelper helper) {
//		this.recipe = recipe;
//		this.helper = helper;
//		texture.saveToCompound(textureTag);
//	}
//
//	@Override
//	public void getIngredients(IIngredients ingredients) {
//		if (input == null) {
//			input = new ItemStack(ModuleFarming.getBlocks().farm, 1, 0);
//		}
//		NonNullList<Ingredient> inputs = recipe.getIngredients();
//		for (Ingredient ingredient : inputs) {    //A bit hacky but it's difficult to create new ingredients.
//			if (ingredient.apply(input)) {
//				for (ItemStack stack : ingredient.getMatchingStacks()) {
//					stack.setTagCompound(textureTag.copy());
//				}
//			}
//		}
//		List<List<ItemStack>> inputLists = helper.expandRecipeItemStackInputs(recipe.getIngredients());
//		ingredients.setInputLists(ItemStack.class, inputLists);
//		ItemStack output = recipe.getRecipeOutput().copy();
//		output.setTagCompound(textureTag.copy());
//		ingredients.setOutput(ItemStack.class, output);
//	}
//
//	@Override
//	public int getWidth() {
//		return recipe.getRecipeWidth();
//	}
//
//	@Override
//	public int getHeight() {
//		return recipe.getRecipeHeight();
//	}
//}
