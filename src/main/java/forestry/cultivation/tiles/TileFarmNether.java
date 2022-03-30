package forestry.cultivation.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.block.state.BlockState;

import forestry.cultivation.features.CultivationTiles;
import forestry.farming.logic.ForestryFarmIdentifier;

public class TileFarmNether extends TilePlanter {
	public TileFarmNether(BlockPos pos, BlockState state) {
		super(CultivationTiles.NETHER.tileType(), pos, state, ForestryFarmIdentifier.INFERNAL);
	}

	@Override
	public NonNullList<ItemStack> createGermlingStacks() {
		return createList(
			new ItemStack(Items.NETHER_WART),
			new ItemStack(Items.NETHER_WART),
			new ItemStack(Items.NETHER_WART),
			new ItemStack(Items.NETHER_WART)
		);
	}

	@Override
	public NonNullList<ItemStack> createResourceStacks() {
		return createList(
			new ItemStack(Blocks.SOUL_SAND),
			new ItemStack(Blocks.SOUL_SAND),
			new ItemStack(Blocks.SOUL_SAND),
			new ItemStack(Blocks.SOUL_SAND)
		);
	}

	@Override
	public NonNullList<ItemStack> createProductionStacks() {
		return createList(
			new ItemStack(Items.NETHER_WART),
			new ItemStack(Items.NETHER_WART),
			new ItemStack(Items.NETHER_WART),
			new ItemStack(Items.NETHER_WART)
		);
	}
}
