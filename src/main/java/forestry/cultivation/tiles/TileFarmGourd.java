package forestry.cultivation.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.block.state.BlockState;

import forestry.cultivation.features.CultivationTiles;
import forestry.farming.logic.ForestryFarmIdentifier;

public class TileFarmGourd extends TilePlanter {
	public TileFarmGourd(BlockPos pos, BlockState state) {
		super(CultivationTiles.GOURD.tileType(), pos, state, ForestryFarmIdentifier.GOURD);
	}

	@Override
	public NonNullList<ItemStack> createGermlingStacks() {
		return NonNullList.create();
	}

	@Override
	public NonNullList<ItemStack> createResourceStacks() {
		return NonNullList.create();
	}

	@Override
	public NonNullList<ItemStack> createProductionStacks() {
		return createList(
			new ItemStack(Blocks.MELON),
			new ItemStack(Blocks.PUMPKIN),
			new ItemStack(Blocks.PUMPKIN),
			new ItemStack(Blocks.MELON)
		);
	}
}
