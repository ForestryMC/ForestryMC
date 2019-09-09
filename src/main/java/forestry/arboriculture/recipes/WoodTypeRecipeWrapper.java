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
//package forestry.arboriculture.recipes;
//
//import javax.annotation.Nullable;
//
//import net.minecraft.block.Block;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.NonNullList;
//
//import forestry.api.arboriculture.IWoodAccess;
//import forestry.api.arboriculture.IWoodType;
//import forestry.api.arboriculture.TreeManager;
//import forestry.api.arboriculture.WoodBlockKind;
//import forestry.arboriculture.IWoodTyped;
//import forestry.core.utils.ItemStackUtil;
//
//import mezz.jei.api.ingredients.IIngredients;
//import mezz.jei.api.recipe.IRecipeWrapper;
//import mezz.jei.api.recipe.IStackHelper;
//import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
//
//public class WoodTypeRecipeWrapper implements IRecipeWrapper {
//
//	private WoodTypeRecipeBase recipe;
//	private IWoodType woodType;
//	@Nullable
//	private static IWoodAccess access;
//	private IStackHelper helper;
//
//	public WoodTypeRecipeWrapper(WoodTypeRecipeBase recipe, IWoodType woodType, IStackHelper helper) {
//		if (access == null) {
//			access = TreeManager.woodAccess;
//		}
//		this.recipe = recipe;
//		this.woodType = woodType;
//		this.helper = helper;
//	}
//
//	@Override
//	public void getIngredients(IIngredients ingredients) {
//		if (recipe.getOutputKind() == WoodBlockKind.SLAB) {
//			int i = 0;
//		}
//		NonNullList<ItemStack> stacks = recipe.getStacks();
//		for (int i = 0; i < stacks.size(); i++) {
//			Block block = ItemStackUtil.getBlock(stacks.get(i));    //TODO - work out how to get this to work without using block
//			if (block instanceof IWoodTyped) {
//				stacks.set(i, access.getStack(woodType, recipe.getInputKind(), recipe.isInputFireproof()));
//			}
//		}
//		ingredients.setInputLists(ItemStack.class, helper.expandRecipeItemStackInputs(stacks));
//
//		ItemStack output = access.getStack(woodType, recipe.getOutputKind(), recipe.isOutputFireproof());
//		output.setCount(recipe.getOutputCount());
//		ingredients.setOutput(ItemStack.class, output);
//
//
//	}
//
//	public static class Shaped extends WoodTypeRecipeWrapper implements IShapedCraftingRecipeWrapper {
//
//		public Shaped(WoodTypeRecipeBase recipe, IWoodType woodType, IStackHelper stackHelper) {
//			super(recipe, woodType, stackHelper);
//		}
//
//		@Override
//		public int getWidth() {
//			return ((WoodTypeRecipe) super.recipe).getRecipeWidth();
//		}
//
//		@Override
//		public int getHeight() {
//			return ((WoodTypeRecipe) super.recipe).getRecipeHeight();
//		}
//	}
//}
