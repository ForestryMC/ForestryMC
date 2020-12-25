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
package forestry.farming.logic.crops;

import forestry.core.config.Constants;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.utils.NetworkUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//TODO consider movnig compat specific crops to compat
//TODO follow up in forge on generic crop interface...
public class CropBasicGrowthCraft extends Crop {

    private final BlockState blockState;
    private final boolean isRice;
    private final boolean isGrape;

    public CropBasicGrowthCraft(
            World world,
            BlockState blockState,
            BlockPos position,
            boolean isRice,
            boolean isGrape
    ) {
        super(world, position);
        this.blockState = blockState;
        this.isRice = isRice;
        this.isGrape = isGrape;
    }

    @Override
    protected boolean isCrop(World world, BlockPos pos) {
        return world.getBlockState(pos) == blockState;
    }

    @Override
    protected NonNullList<ItemStack> harvestBlock(World world, BlockPos pos) {
        Block block = blockState.getBlock();
        NonNullList<ItemStack> harvest = NonNullList.create();
        //TODO cast
//        LootContext.Builder ctx = new LootContext.Builder((ServerWorld) world)
//                .withParameter(LootParameters.POSITION, pos);
//        harvest.addAll(block.getDrops(blockState, ctx));
        if (harvest.size() > 1) {
            harvest.remove(0); //Hops have rope as first drop.
        }

        PacketFXSignal packet = new PacketFXSignal(
                PacketFXSignal.VisualFXType.BLOCK_BREAK,
                PacketFXSignal.SoundFXType.BLOCK_BREAK,
                pos,
                blockState
        );
        NetworkUtil.sendNetworkPacket(packet, pos, world);

        if (isGrape) {
            world.removeBlock(pos, false);
        } else {
            world.setBlockState(pos, block.getDefaultState(), Constants.FLAG_BLOCK_SYNC);
        }

        if (isRice) {
            // TODO: GrowthCraft for MC 1.9. Don't use meta, get the actual block state.
            world.setBlockState(pos.down(), block.getDefaultState(), Constants.FLAG_BLOCK_SYNC);
            //TODO flatten
        }

        return harvest;
    }

    @Override
    public String toString() {
        return String.format("CropBasicGrowthCraft [ position: [ %s ]; block: %s ]", position.toString(), blockState);
    }
}
