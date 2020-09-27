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
package forestry.apiculture.blocks;

import forestry.apiculture.features.ApicultureBlocks;
import forestry.apiculture.tiles.TileCandle;
import forestry.core.config.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import java.util.Random;

public class BlockStump extends TorchBlock {

    public BlockStump() {
        super(Block.Properties.create(Material.MISCELLANEOUS)
                              .hardnessAndResistance(0.0f)
                              .sound(SoundType.WOOD), ParticleTypes.FLAME);
    }

    @Override
    public ActionResultType onBlockActivated(
            BlockState state,
            World worldIn,
            BlockPos pos,
            PlayerEntity playerIn,
            Hand hand,
            BlockRayTraceResult hit
    ) {
        ItemStack heldItem = playerIn.getHeldItem(hand);
        if (BlockCandle.lightingItems.contains(heldItem.getItem())) {
            BlockState activatedState = ApicultureBlocks.BASE.get(BlockTypeApiculture.APIARY).with(
                    BlockCandle.STATE,
                    BlockCandle.State.ON
            );
            worldIn.setBlockState(pos, activatedState, Constants.FLAG_BLOCK_SYNC);
            TileCandle tc = new TileCandle();
            tc.setColour(16777215); // default to white
            tc.setLit(true);
            worldIn.setTileEntity(pos, tc);
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
    }
}
