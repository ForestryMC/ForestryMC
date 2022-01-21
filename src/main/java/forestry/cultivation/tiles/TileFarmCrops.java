package forestry.cultivation.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.block.state.BlockState;

import forestry.cultivation.features.CultivationTiles;
import forestry.farming.logic.ForestryFarmIdentifier;

public class TileFarmCrops extends TilePlanter {
	public TileFarmCrops(BlockPos pos, BlockState state) {
		super(CultivationTiles.CROPS.tileType(), pos, state, ForestryFarmIdentifier.CROPS);
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
