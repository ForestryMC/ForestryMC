package forestry.core.recipes.json;

import java.util.Arrays;
import java.util.Comparator;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistryEntry;

import forestry.api.arboriculture.IWoodAccess;
import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.arboriculture.IWoodTyped;
import forestry.core.utils.ItemStackUtil;

/**
 * Very messy, there's probably a lot that can be done to clean this up
 */
public class WoodTypeRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IShapedRecipe {

	private ItemStack result = ItemStack.EMPTY;
	private int width;
	private int height;
	private int outputCount;
	private WoodBlockKind inputKind;
	private WoodBlockKind outputKind;
	private boolean inputFireproof;
	private boolean outputFireproof;
	private IWoodAccess access;
	private ItemStack stickStack = new ItemStack(Items.STICK);
	private NonNullList<Character> input;
	//# is woodtype, X is stick, ' ' is nothing

	public WoodTypeRecipe(WoodBlockKind inputKind, WoodBlockKind outputKind, boolean inputFireproof, boolean outputFireproof, String[] recipe, int outputCount) {
		access = TreeManager.woodAccess;
		this.inputKind = inputKind;
		this.outputKind = outputKind;
		this.inputFireproof = inputFireproof;
		this.outputFireproof = outputFireproof;

		this.height = recipe.length;
		this.width = Arrays.stream(recipe).max(Comparator.comparing(String::length)).get().length();
		this.outputCount = outputCount;
		input = NonNullList.withSize(width * height, ' ');
		int i = 0;
		for (String s : recipe) {
			for (char c : s.toCharArray()) {
				input.set(i, c);
				i++;
			}
		}
	}


	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		this.result = ItemStack.EMPTY;
		for (int x = 0; x <= inv.getWidth() - width; x++) {
			for (int y = 0; y <= inv.getHeight() - height; ++y) {
				if (checkMatch(inv, x, y, false)) {
					return true;
				}

				if (checkMatch(inv, x, y, true)) {
					return true;
				}
			}
		}
		return false;
	}

	//TODO - not working atm
	private boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror) {
		IWoodType woodType = null;
		for (int y = 0; y < inv.getHeight(); y++) {
			for (int x = 0; x < inv.getWidth(); x++) {
				int subX = x - startX;
				int subY = y - startY;
				char target = ' ';

				if (subX < width && subY < height && subX >= 0 && subY >= 0) {
					if (mirror) {
						target = input.get(width - subX - 1 + subY * width);
					} else {
						target = input.get(subX + subY * width);
					}
				}

				ItemStack stackInSlot = inv.getStackInRowAndColumn(x, y);
				if (target == ' ' && !stackInSlot.isEmpty()) {
					return false;
				} else if (target == 'X' && !OreDictionary.itemMatches(stickStack, stackInSlot, false)) {
					return false;
				}
				Block block = ItemStackUtil.getBlock(stackInSlot);
				if (target == '#') {
					if (!(block instanceof IWoodTyped)) {
						return false;
					}

					IWoodTyped typed = (IWoodTyped) block;
					if (typed == null) {
						return false;
					}
					if (typed.getBlockKind() != inputKind || typed.isFireproof() != inputFireproof) {
						return false;
					}
					if (woodType == null) {
						woodType = typed.getWoodType(stackInSlot.getMetadata());
					} else if (typed.getWoodType(stackInSlot.getMetadata()) != woodType) {
						return false;
					}

				}
			}
		}
		if (woodType == null) {
			return false;
		}
		result = access.getStack(woodType, outputKind, outputFireproof);
		result.setCount(outputCount);
		return true;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		return this.result.copy();
	}

	@Override
	public boolean canFit(int width, int height) {
		return width >= this.width && height >= this.height;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return this.result;
	}

	@Override
	public int getRecipeWidth() {
		return width;
	}

	@Override
	public int getRecipeHeight() {
		return height;
	}
}
