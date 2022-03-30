package forestry.arboriculture.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.arboriculture.IWoodTyped;

public class BlockForestryStairs extends StairBlock implements IWoodTyped {
	private final boolean fireproof;
	private final IWoodType woodType;

	public BlockForestryStairs(BlockForestryPlank plank) {
		super(plank.defaultBlockState(), Block.Properties.copy(plank));
		this.fireproof = plank.isFireproof();
		this.woodType = plank.getWoodType();
	}

	@Override
	public boolean isFireproof() {
		return fireproof;
	}

	@Override
	public IWoodType getWoodType() {
		return woodType;
	}

	@Override
	public WoodBlockKind getBlockKind() {
		return WoodBlockKind.STAIRS;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		if (fireproof) {
			return 0;
		}
		return 20;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		if (fireproof) {
			return 0;
		}
		return 5;
	}
}
