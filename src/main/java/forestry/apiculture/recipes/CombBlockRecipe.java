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
//package forestry.apiculture.recipes;
//	//TODO auto gen these with data run like forge does?
//import com.google.gson.JsonObject;
//
//import javax.annotation.Nonnull;
//
//import net.minecraft.inventory.CraftingInventory;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.crafting.IRecipe;
//import net.minecraft.item.crafting.ShapedRecipe;
//import net.minecraft.util.JSONUtils;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.world.World;
//
//import net.minecraftforge.common.crafting.CraftingHelper;
//import net.minecraftforge.common.crafting.IRecipeFactory;
//import net.minecraftforge.common.crafting.JsonContext;
//import net.minecraftforge.oredict.ShapedOreRecipe;
//
//import forestry.apiculture.ModuleApiculture;
//import forestry.apiculture.blocks.BlockHoneyComb;
//import forestry.apiculture.items.ItemHoneyComb;
//import forestry.core.utils.InventoryUtil;
//
//public class CombBlockRecipe extends ShapedRecipe {
//
//	BlockHoneyComb[] blockHoneyCombs;
//
//	public CombBlockRecipe(ResourceLocation group, @Nonnull ItemStack result, CraftingHelper.ShapedPrimer primer) {
//		super(group, result, primer);
//		blockHoneyCombs = ModuleApiculture.getBlocks().beeCombs;
//	}
//
//	@Override
//	public boolean matches(CraftingInventory inv, World world) {
//		int meta = -1;
//		for (ItemStack stack : InventoryUtil.getStacks(inv)) {
//			if (!(stack.getItem() instanceof ItemHoneyComb)) {
//				return false;
//			}
//			if (meta == -1) {
//				meta = stack.getMetadata();
//			} else if (stack.getMetadata() != meta) {
//				return false;
//			}
//		}
//		return true;
//	}
//
//	@Override
//	public ItemStack getCraftingResult(CraftingInventory inv) {
//		for (ItemStack stack : InventoryUtil.getStacks(inv)) {
//			if (stack.getItem() instanceof ItemHoneyComb) {
//				int meta = stack.getMetadata();
//				BlockHoneyComb block = blockHoneyCombs[meta / 16];
//				return new ItemStack(block, 1, meta & 15);
//			}
//		}
//		return new ItemStack(blockHoneyCombs[0], 1, 0);
//	}
//
//	public static class Factory implements IRecipeFactory {
//		@Override
//		public IRecipe parse(JsonContext context, JsonObject json) {
//			ShapedOreRecipe oreRecipe = ShapedOreRecipe.factory(context, json);
//
//			CraftingHelper.ShapedPrimer primer = new CraftingHelper.ShapedPrimer();
//			primer.width = oreRecipe.getRecipeWidth();
//			primer.height = oreRecipe.getRecipeHeight();
//			primer.mirrored = JSONUtils.getBoolean(json, "mirrored", true);
//			primer.input = oreRecipe.getIngredients();
//
//
//			return new CombBlockRecipe(new ResourceLocation(oreRecipe.getGroup()), oreRecipe.getRecipeOutput(), primer);
//		}
//	}
//}
