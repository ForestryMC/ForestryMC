///*
// *******************************************************************************
// * Copyright (c) 2011-2014 SirSengir.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the GNU Lesser Public License v3
// * which accompanies this distribution, and is available at
// * http://www.gnu.org/licenses/lgpl-3.0.txt
// *
// * Various Contributors including, but not limited to:
// * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
// *******************************************************************************
// */
//package forestry.arboriculture.recipes;
//
//import java.util.Arrays;
//import java.util.Comparator;
//
//import net.minecraft.block.Block;
//import net.minecraft.inventory.CraftingInventory;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.Items;
//import net.minecraft.util.NonNullList;
//import net.minecraft.world.World;
//
//import net.minecraftforge.common.crafting.IShapedRecipe;
//import net.minecraftforge.oredict.OreDictionary;
//
//import forestry.api.arboriculture.EnumForestryWoodType;
//import forestry.api.arboriculture.IWoodType;
//import forestry.api.arboriculture.WoodBlockKind;
//import forestry.arboriculture.IWoodTyped;
//import forestry.core.utils.ItemStackUtil;
//
//public class WoodTypeRecipe extends WoodTypeRecipeBase implements IShapedRecipe {
//
//	private int width;
//	private int height;
//	private static final ItemStack STICK_STACK = new ItemStack(Items.STICK);
//	private NonNullList<Character> input; //# is woodtype, X is stick, ' ' is nothing
//
//	public WoodTypeRecipe(WoodBlockKind inputKind, WoodBlockKind outputKind, boolean inputFireproof, boolean outputFireproof, String[] recipe, int outputCount) {
//		super(outputCount, inputKind, outputKind, inputFireproof, outputFireproof);
//		this.height = recipe.length;
//		this.width = Arrays.stream(recipe).max(Comparator.comparing(String::length)).get().length();
//		this.outputCount = outputCount;
//		input = NonNullList.withSize(width * height, ' ');
//		int i = 0;
//		for (String s : recipe) {
//			for (char c : s.toCharArray()) {
//				input.set(i, c);
//				i++;
//			}
//		}
//	}
//
//	@Override
//	public boolean matches(CraftingInventory inv, World worldIn) {
//		this.result = ItemStack.EMPTY;
//		for (int x = 0; x <= inv.getWidth() - width; x++) {
//			for (int y = 0; y <= inv.getHeight() - height; ++y) {
//				if (checkMatch(inv, x, y, false)) {
//					return true;
//				}
//
//				if (checkMatch(inv, x, y, true)) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}
//
//	private boolean checkMatch(CraftingInventory inv, int startX, int startY, boolean mirror) {
//		IWoodType woodType = null;
//		for (int y = 0; y < inv.getHeight(); y++) {
//			for (int x = 0; x < inv.getWidth(); x++) {
//				int subX = x - startX;
//				int subY = y - startY;
//				char target = ' ';
//
//				if (subX < width && subY < height && subX >= 0 && subY >= 0) {
//					if (mirror) {
//						target = input.get(width - subX - 1 + subY * width);
//					} else {
//						target = input.get(subX + subY * width);
//					}
//				}
//
//				ItemStack stackInSlot = inv.getStackInRowAndColumn(x, y);
//				if (target == ' ' && !stackInSlot.isEmpty()) {
//					return false;
//				} else if (target == 'X' && !OreDictionary.itemMatches(STICK_STACK, stackInSlot, false)) {
//					return false;
//				}
//				Block block = ItemStackUtil.getBlock(stackInSlot);
//				if (target == '#') {
//					if (!(block instanceof IWoodTyped)) {
//						return false;
//					}
//
//					IWoodTyped typed = (IWoodTyped) block;
//					if (typed == null) {
//						return false;
//					}
//					if (typed.getBlockKind() != inputKind || typed.isFireproof() != inputFireproof) {
//						return false;
//					}
//					if (woodType == null) {
//						woodType = typed.getWoodType(stackInSlot.getMetadata());
//					} else if (typed.getWoodType(stackInSlot.getMetadata()) != woodType) {
//						return false;
//					}
//
//				}
//			}
//		}
//		if (woodType == null) {
//			return false;
//		}
//		result = access.getStack(woodType, outputKind, outputFireproof);
//		return true;
//	}
//
//	/**
//	 * @return won't return exact stacks because this is impossible, however this gives information about the
//	 * shape of the recipe and what type of stacks should be there.
//	 */
//	@Override
//	public NonNullList<ItemStack> getStacks() {
//		NonNullList<ItemStack> stacks = NonNullList.create();
//		for (char c : input) {
//			if (c == 'X') {
//				stacks.add(STICK_STACK.copy());
//			} else if (c == '#') {
//				stacks.add(access.getStack(EnumForestryWoodType.values()[0], inputKind, inputFireproof));
//			} else {
//				stacks.add(ItemStack.EMPTY);
//			}
//		}
//		return stacks;
//	}
//
//	@Override
//	public boolean canFit(int width, int height) {
//		return width >= this.width && height >= this.height;
//	}
//
//	@Override
//	public int getRecipeWidth() {
//		return width;
//	}
//
//	@Override
//	public int getRecipeHeight() {
//		return height;
//	}
//}
