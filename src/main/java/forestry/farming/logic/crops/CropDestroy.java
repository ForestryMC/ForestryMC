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
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.NetworkUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

public class CropDestroy extends Crop {

    protected final BlockState blockState;
    @Nullable
    protected final BlockState replantState;

    protected final ItemStack germling;

    public CropDestroy(World world, BlockState blockState, BlockPos position, @Nullable BlockState replantState) {
        this(world, blockState, position, replantState, ItemStack.EMPTY);
    }

    public CropDestroy(World world, BlockState blockState, BlockPos position, @Nullable BlockState replantState, ItemStack germling) {
        super(world, position);
        this.blockState = blockState;
        this.replantState = replantState;
        this.germling = germling;
    }

    @Override
    protected boolean isCrop(World world, BlockPos pos) {
        return world.getBlockState(pos) == blockState;
    }

    @Override
    protected NonNullList<ItemStack> harvestBlock(World world, BlockPos pos) {
        Block block = blockState.getBlock();
        List<ItemStack> harvested = Block.getDrops(blockState, (ServerWorld) world, pos, world.getTileEntity(pos));    //TODO - method safety
        NonNullList<ItemStack> nnHarvested = NonNullList.from(ItemStack.EMPTY, harvested.toArray(new ItemStack[0]));    //TODO very messy
        float chance = ForgeEventFactory.fireBlockHarvesting(nnHarvested, world, pos, blockState, 0, 1.0F, false, null);

        boolean removedSeed = germling.isEmpty();
        Iterator<ItemStack> dropIterator = harvested.iterator();
        while (dropIterator.hasNext()) {
            ItemStack next = dropIterator.next();
            if (world.rand.nextFloat() <= chance) {
                if (!removedSeed && ItemStackUtil.isIdenticalItem(next, germling)) {
                    next.shrink(1);
                    if (next.isEmpty()) {
                        dropIterator.remove();
                    }
                    removedSeed = true;
                }
            } else {
                dropIterator.remove();
            }
        }

        PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, blockState);
        NetworkUtil.sendNetworkPacket(packet, pos, world);

        if (replantState != null) {
            world.setBlockState(pos, replantState, Constants.FLAG_BLOCK_SYNC);
        } else {
            //TODO right call?
            world.removeBlock(pos, false);
        }
        if (!(harvested instanceof NonNullList)) {
            return NonNullList.from(ItemStack.EMPTY, harvested.toArray(new ItemStack[0]));
        } else {
            return (NonNullList<ItemStack>) harvested;
        }
    }

    @Override
    public String toString() {
        return String.format("CropDestroy [ position: [ %s ]; block: %s ]", position.toString(), blockState);
    }
}
