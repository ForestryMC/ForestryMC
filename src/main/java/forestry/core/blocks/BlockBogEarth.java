/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.blocks;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;

import net.minecraftforge.common.IPlantable;

import forestry.core.config.Constants;
import forestry.core.features.CoreBlocks;

/**
 * bog earth, which becomes peat
 */
public class BlockBogEarth extends Block {
	private static final int maturityDelimiter = 3; //maturity at which bogEarth becomes peat
	public static final IntegerProperty MATURITY = IntegerProperty.create("maturity", 0, maturityDelimiter);

	public BlockBogEarth() {
		super(Block.Properties.of(Material.DIRT)
				.randomTicks()
				.strength(0.5f)
				.sound(SoundType.GRAVEL));

		registerDefaultState(this.getStateDefinition().any().setValue(MATURITY, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(MATURITY);
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		if (world.isClientSide || world.random.nextInt(13) != 0) {
			return;
		}

		int maturity = state.getValue(MATURITY);
		if (isMoistened(world, pos)) {
			if (maturity == maturityDelimiter - 1) {
				world.setBlock(pos, CoreBlocks.PEAT.defaultState(), Constants.FLAG_BLOCK_SYNC);
			} else {
				world.setBlock(pos, state.setValue(MATURITY, maturity + 1), Constants.FLAG_BLOCK_SYNC);
			}
		}
	}

	private static boolean isMoistened(Level world, BlockPos pos) {
		for (BlockPos waterPos : BlockPos.betweenClosed(pos.offset(-2, -2, -2), pos.offset(2, 2, 2))) {
			BlockState blockState = world.getBlockState(waterPos);
			Block block = blockState.getBlock();
			if (block == Blocks.WATER) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction direction, IPlantable plantable) {
		return false;
	}
}
