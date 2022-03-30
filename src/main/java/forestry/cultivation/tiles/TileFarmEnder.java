package forestry.cultivation.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.block.state.BlockState;

import forestry.cultivation.features.CultivationTiles;
import forestry.farming.logic.ForestryFarmIdentifier;

public class TileFarmEnder extends TilePlanter {
	public TileFarmEnder(BlockPos pos, BlockState state) {
		super(CultivationTiles.ENDER.tileType(), pos, state, ForestryFarmIdentifier.ENDER);
	}

	@Override
	public NonNullList<ItemStack> createGermlingStacks() {
		return createList(
			new ItemStack(Blocks.CHORUS_FLOWER),
			new ItemStack(Blocks.CHORUS_FLOWER),
			new ItemStack(Blocks.CHORUS_FLOWER),
			new ItemStack(Blocks.CHORUS_FLOWER)
		);
	}

	@Override
	public NonNullList<ItemStack> createResourceStacks() {
		return createList(
			new ItemStack(Blocks.END_STONE),
			new ItemStack(Blocks.END_STONE),
			new ItemStack(Blocks.END_STONE),
			new ItemStack(Blocks.END_STONE)
		);
	}

	@Override
	public NonNullList<ItemStack> createProductionStacks() {
		return createList(
			new ItemStack(Blocks.CHORUS_FLOWER),
			new ItemStack(Items.CHORUS_FRUIT),
			new ItemStack(Items.CHORUS_FRUIT),
			new ItemStack(Blocks.CHORUS_FLOWER)
		);
	}
}
