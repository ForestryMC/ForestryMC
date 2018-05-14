package forestry.core.recipes.json;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import net.minecraftforge.registries.IForgeRegistryEntry;

import forestry.api.arboriculture.IWoodAccess;
import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.arboriculture.IWoodTyped;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.ItemStackUtil;

/**
 * used for logs -> planks (fireproof and not)
 */
public class WoodTypeRecipeShapeless extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	private ItemStack result = ItemStack.EMPTY;
	private int outputCount;
	private WoodBlockKind inputKind;
	private WoodBlockKind outputKind;
	private boolean inputFireproof;
	private boolean outputFireproof;
	private IWoodAccess access;

	public WoodTypeRecipeShapeless(WoodBlockKind inputKind, WoodBlockKind outputKind, boolean inputFireproof, boolean outputFireproof, int outputCount) {
		access = TreeManager.woodAccess;
		this.inputKind = inputKind;
		this.outputKind = outputKind;
		this.inputFireproof = inputFireproof;
		this.outputFireproof = outputFireproof;

		this.outputCount = outputCount;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		result = ItemStack.EMPTY;
		IWoodType type = null;
		int logCount = 0;
		for (ItemStack stack : InventoryUtil.getStacks(inv)) {
			if (stack.isEmpty()) {
				continue;
			}
			Block block = ItemStackUtil.getBlock(stack);
			if (!(block instanceof IWoodTyped)) {
				return false;
			}
			IWoodTyped typed = (IWoodTyped) block;
			if (typed.getBlockKind() != inputKind || typed.isFireproof() != inputFireproof) {
				return false;
			}
			if (type == null) {
				type = typed.getWoodType(stack.getMetadata()); //TODO - don't use metadata here
			}
			logCount++;
		}
		if (type != null) {
			result = access.getStack(type, outputKind, outputFireproof);
		}
		return logCount == 1;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		result.setCount(outputCount);
		return result.copy();
	}

	@Override
	public boolean canFit(int width, int height) {
		return width >= 1 && height >= 1;
	}

	@Override
	public ItemStack getRecipeOutput() {
		result.setCount(outputCount);
		return result.copy();
	}
}
