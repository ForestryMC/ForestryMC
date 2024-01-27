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
package forestry.arboriculture.blocks;

import java.util.Random;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import forestry.arboriculture.tiles.TileTreeContainer;
import forestry.core.tiles.TileUtil;

//TODO inline?

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public abstract class BlockTreeContainer extends BaseEntityBlock {

	protected BlockTreeContainer(Properties properties) {
		super(properties
				.randomTicks()
				.sound(SoundType.GRASS)
				.noCollission());
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		if (rand.nextFloat() > 0.1) {
			return;
		}

		TileTreeContainer tile = TileUtil.getTile(world, pos, TileTreeContainer.class);
		if (tile == null) {
			return;
		}

		tile.onBlockTick(world, pos, state, rand);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
}
