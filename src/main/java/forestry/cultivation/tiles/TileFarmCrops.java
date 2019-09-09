package forestry.cultivation.tiles;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;

import forestry.cultivation.ModuleCultivation;
import forestry.farming.logic.ForestryFarmIdentifier;

public class TileFarmCrops extends TilePlanter {
	public TileFarmCrops() {
		super(ModuleCultivation.getTiles().CROPS, ForestryFarmIdentifier.CROPS);
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
