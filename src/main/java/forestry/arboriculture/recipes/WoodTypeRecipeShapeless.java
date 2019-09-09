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
//import net.minecraft.block.Block;
//import net.minecraft.inventory.CraftingInventory;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.crafting.IRecipe;
//import net.minecraft.util.NonNullList;
//import net.minecraft.world.World;
//
//import forestry.api.arboriculture.EnumForestryWoodType;
//import forestry.api.arboriculture.IWoodType;
//import forestry.api.arboriculture.WoodBlockKind;
//import forestry.arboriculture.IWoodTyped;
//import forestry.core.utils.InventoryUtil;
//import forestry.core.utils.ItemStackUtil;
//
///**
// * used for logs -> planks (fireproof and not)
// */
//public class WoodTypeRecipeShapeless extends WoodTypeRecipeBase implements IRecipe {
//
//
//	public WoodTypeRecipeShapeless(WoodBlockKind inputKind, WoodBlockKind outputKind, boolean inputFireproof, boolean outputFireproof, int outputCount) {
//		super(outputCount, inputKind, outputKind, inputFireproof, outputFireproof);
//	}
//
//	@Override
//	public boolean matches(CraftingInventory inv, World worldIn) {
//		result = ItemStack.EMPTY;
//		IWoodType type = null;
//		int logCount = 0;
//		for (ItemStack stack : InventoryUtil.getStacks(inv)) {
//			if (stack.isEmpty()) {
//				continue;
//			}
//			Block block = ItemStackUtil.getBlock(stack);
//			if (!(block instanceof IWoodTyped)) {
//				return false;
//			}
//			IWoodTyped typed = (IWoodTyped) block;
//			if (typed.getBlockKind() != inputKind || typed.isFireproof() != inputFireproof) {
//				return false;
//			}
//			if (type == null) {
//				type = typed.getWoodType(stack.getMetadata()); //TODO - don't use metadata here
//			}
//			logCount++;
//		}
//		if (type != null) {
//			result = access.getStack(type, outputKind, outputFireproof);
//		}
//		return logCount == 1;
//	}
//
//
//	@Override
//	public boolean canFit(int width, int height) {
//		return width >= 1 && height >= 1;
//	}
//
//	@Override
//	public NonNullList<ItemStack> getStacks() {
//		NonNullList<ItemStack> ret = NonNullList.create();
//		ret.add(access.getStack(EnumForestryWoodType.values()[0], inputKind, inputFireproof));
//		return ret;
//	}
//}
