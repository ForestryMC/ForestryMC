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

import forestry.core.config.Constants;
import forestry.core.features.CoreBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.ToolType;

import java.util.Random;

/**
 * bog earth, which becomes peat
 */
public class BlockBogEarth extends Block {
    private static final int maturityDelimiter = 3; //maturity at which bogEarth becomes peat
    public static final IntegerProperty MATURITY = IntegerProperty.create("maturity", 0, maturityDelimiter);

    public BlockBogEarth() {
        super(Block.Properties.create(Material.EARTH)
                .tickRandomly()
                .hardnessAndResistance(0.5f)
                .sound(SoundType.GROUND)
                .harvestTool(ToolType.SHOVEL)
                .harvestLevel(0));

        setDefaultState(this.getStateContainer().getBaseState().with(MATURITY, 0));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(MATURITY);
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        if (world.isRemote || world.rand.nextInt(13) != 0) {
            return;
        }

        int maturity = state.get(MATURITY);
        if (isMoistened(world, pos)) {
            if (maturity == maturityDelimiter - 1) {
                world.setBlockState(pos, CoreBlocks.PEAT.defaultState(), Constants.FLAG_BLOCK_SYNC);
            } else {
                world.setBlockState(pos, state.with(MATURITY, maturity + 1), Constants.FLAG_BLOCK_SYNC);
            }
        }
    }

    private static boolean isMoistened(World world, BlockPos pos) {
        for (BlockPos waterPos : BlockPos.getAllInBoxMutable(pos.add(-2, -2, -2), pos.add(2, 2, 2))) {
            BlockState blockState = world.getBlockState(waterPos);
            Block block = blockState.getBlock();
            if (block == Blocks.WATER) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction direction, IPlantable plantable) {
        return false;
    }
}
