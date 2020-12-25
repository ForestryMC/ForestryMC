/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.arboriculture.blocks;

import forestry.arboriculture.tiles.TileTreeContainer;
import forestry.core.tiles.TileUtil;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

//TODO inline?
public abstract class BlockTreeContainer extends ContainerBlock {
    protected BlockTreeContainer(Properties properties) {
        super(properties
                .tickRandomly()
                .sound(SoundType.PLANT)
                .doesNotBlockMovement());
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
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
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
