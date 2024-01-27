package forestry.core.blocks;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;

import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.utils.RenderUtil;

public class BlockHumus extends Block {
	private static final int degradeDelimiter = Config.humusDegradeDelimiter;
	public static final IntegerProperty DEGRADE = IntegerProperty.create("degrade", 0, degradeDelimiter); // degradation level of humus

	public BlockHumus() {
		super(Block.Properties.of(Material.DIRT)
				.randomTicks()
				.strength(0.5f)
				.sound(SoundType.GRAVEL));

		registerDefaultState(this.getStateDefinition().any().setValue(DEGRADE, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(DEGRADE);
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		if (world.isClientSide || world.random.nextInt(140) != 0) {
			return;
		}

		if (isEnrooted(world, pos)) {
			degradeSoil(world, pos);
		}
	}

	private static boolean isEnrooted(Level world, BlockPos pos) {
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				if (i == 0 && j == 0) {
					continue; // We are not returning true if we are the base of a sapling.
				}
				BlockPos blockPos = pos.offset(i, 1, j);
				BlockState state = world.getBlockState(blockPos);
				Block block = state.getBlock();
				if (state.is(BlockTags.LOGS) || block instanceof BonemealableBlock) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * If a tree or sapling is in the vicinity, there is a chance, that the soil will degrade.
	 */
	private static void degradeSoil(Level world, final BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);

		int degrade = blockState.getValue(DEGRADE);
		degrade++;

		if (degrade >= degradeDelimiter) {
			world.setBlock(pos, Blocks.SAND.defaultBlockState(), Constants.FLAG_BLOCK_SYNC);
		} else {
			world.setBlock(pos, blockState.setValue(DEGRADE, degrade), Constants.FLAG_BLOCK_SYNC);
		}
		//TODO: Is this still needed ? Should now be marked with setBlockState
		RenderUtil.markForUpdate(pos);
	}

	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction direction, IPlantable plantable) {
		PlantType plantType = plantable.getPlantType(world, pos);
		return plantType == PlantType.CROP || plantType == PlantType.PLAINS;
	}
}
