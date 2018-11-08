package forestry.cultivation.tiles;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import forestry.farming.logic.ForestryFarmIdentifier;

public class TileFarmCrops extends TilePlanter {
	public TileFarmCrops() {
		super(ForestryFarmIdentifier.CROPS);
	}

	@Override
	public NonNullList<ItemStack> createGermlingStacks() {
		return createList(
			new ItemStack(Items.WHEAT_SEEDS),
			new ItemStack(Items.POTATO),
			new ItemStack(Items.CARROT),
			new ItemStack(Items.BEETROOT_SEEDS)
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
			new ItemStack(Items.WHEAT),
			new ItemStack(Items.POTATO),
			new ItemStack(Items.CARROT),
			new ItemStack(Items.BEETROOT)
		);
	}
}
