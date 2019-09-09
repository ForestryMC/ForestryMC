package forestry.cultivation.tiles;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import forestry.core.ModuleCore;
import forestry.cultivation.ModuleCultivation;
import forestry.farming.logic.ForestryFarmIdentifier;

public class TileBog extends TilePlanter {
	public TileBog() {
		super(ModuleCultivation.getTiles().BOG, ForestryFarmIdentifier.PEAT);
	}

	@Override
	public NonNullList<ItemStack> createGermlingStacks() {
		return NonNullList.create();
	}

	@Override
	public NonNullList<ItemStack> createResourceStacks() {
		return createList(
			new ItemStack(ModuleCore.getBlocks().bogEarth),
			new ItemStack(ModuleCore.getBlocks().bogEarth),
			new ItemStack(ModuleCore.getBlocks().bogEarth),
			new ItemStack(ModuleCore.getBlocks().bogEarth)
		);
	}

	@Override
	public NonNullList<ItemStack> createProductionStacks() {
		return createList(
			new ItemStack(ModuleCore.getItems().peat),
			new ItemStack(ModuleCore.getItems().peat),
			new ItemStack(ModuleCore.getItems().peat),
			new ItemStack(ModuleCore.getItems().peat)
		);
	}
}
