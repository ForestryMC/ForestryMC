package forestry.cultivation.tiles;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import forestry.farming.logic.ForestryFarmIdentifier;

public class TileArboretum extends TilePlanter {
	public TileArboretum() {
		super(ForestryFarmIdentifier.ARBOREAL);
	}

	@Override
	public NonNullList<ItemStack> createGermlingStacks() {
		return createList(
			new ItemStack(Blocks.SAPLING),
			new ItemStack(Blocks.SAPLING, 1, 2),
			new ItemStack(Blocks.SAPLING, 1, 2),
			new ItemStack(Blocks.SAPLING)
		);
	}

	@Override
	public NonNullList<ItemStack> createResourceStacks() {
		return createList(
			new ItemStack(Blocks.DIRT),
			new ItemStack(Blocks.DIRT),
			new ItemStack(Blocks.DIRT),
			new ItemStack(Blocks.DIRT)
		);
	}

	@Override
	public NonNullList<ItemStack> createProductionStacks() {
		return createList(
			new ItemStack(Blocks.LOG),
			new ItemStack(Items.APPLE),
			new ItemStack(Items.APPLE),
			new ItemStack(Blocks.LOG)
		);
	}
}
