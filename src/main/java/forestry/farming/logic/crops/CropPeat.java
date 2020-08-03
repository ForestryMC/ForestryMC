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
package forestry.farming.logic.crops;

import forestry.core.config.Constants;
import forestry.core.features.CoreBlocks;
import forestry.core.features.CoreItems;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.utils.NetworkUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CropPeat extends Crop {

    public CropPeat(World world, BlockPos position) {
        super(world, position);
    }

    @Override
    protected boolean isCrop(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return CoreBlocks.PEAT.blockEqual(blockState);
    }

    @Override
    protected NonNullList<ItemStack> harvestBlock(World world, BlockPos pos) {
        NonNullList<ItemStack> drops = NonNullList.create();
        drops.add(CoreItems.PEAT.stack());

        BlockState blockState = world.getBlockState(pos);

        PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, blockState);
        NetworkUtil.sendNetworkPacket(packet, pos, world);

        world.setBlockState(pos, Blocks.DIRT.getDefaultState(), Constants.FLAG_BLOCK_SYNC);
        return drops;
    }

}
