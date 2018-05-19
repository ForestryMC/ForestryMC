package forestry.arboriculture.recipes;

import javax.annotation.Nullable;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import net.minecraftforge.registries.IForgeRegistryEntry;

import forestry.api.arboriculture.IWoodAccess;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;

public abstract class WoodTypeRecipeBase extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	protected ItemStack result = ItemStack.EMPTY;
	protected int outputCount;
	protected WoodBlockKind inputKind;
	protected WoodBlockKind outputKind;
	protected boolean inputFireproof;
	protected boolean outputFireproof;
	@Nullable
	protected static IWoodAccess access;

	protected WoodTypeRecipeBase(int outputCount, WoodBlockKind inputKind, WoodBlockKind outputKind, boolean inputFireproof, boolean outputFireproof) {
		if (access == null) {
			access = TreeManager.woodAccess;
		}
		this.outputCount = outputCount;
		this.inputKind = inputKind;
		this.outputKind = outputKind;
		this.inputFireproof = inputFireproof;
		this.outputFireproof = outputFireproof;
	}

	@Override
	public abstract boolean matches(InventoryCrafting inv, World world);

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		result.setCount(outputCount);
		return result.copy();
	}

	@Override
	public ItemStack getRecipeOutput() {
		result.setCount(outputCount);
		return result.copy();
	}

	public abstract NonNullList<ItemStack> getStacks();

	public int getOutputCount() {
		return outputCount;
	}

	public WoodBlockKind getInputKind() {
		return inputKind;
	}

	public WoodBlockKind getOutputKind() {
		return outputKind;
	}

	public boolean isInputFireproof() {
		return inputFireproof;
	}

	public boolean isOutputFireproof() {
		return outputFireproof;
	}
}
