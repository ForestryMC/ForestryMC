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
package forestry.core.tiles;

import forestry.core.blocks.BlockBase;
import forestry.core.blocks.IBlockType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

public abstract class TileBase extends TileForestry {

    public TileBase(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public void openGui(ServerPlayerEntity player, BlockPos pos) {
        if (!hasGui()) {
            return;
        }
        NetworkHooks.openGui(player, this, pos);
    }

    protected boolean hasGui() {
        return true;
    }

    @Override
    public String getUnlocalizedTitle() {
        Block block = getBlockState().getBlock();
        if (block instanceof BlockBase) {
            return block.getTranslationKey();
        }
        return super.getUnlocalizedTitle();
    }

    @SuppressWarnings("unchecked")
    public <T extends IBlockType> T getBlockType(T fallbackType) {
        BlockState blockState = getBlockState();
        Block block = blockState.getBlock();
        if (!(block instanceof BlockBase)) {
            return fallbackType;
        }
        BlockBase blockBase = (BlockBase) block;
        return (T) blockBase.blockType;
    }


    //TODO
    //	@Override
    //	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newState) {
    //		Block oldBlock = oldState.getBlock();
    //		Block newBlock = newState.getBlock();
    //		return oldBlock != newBlock || !(oldBlock instanceof BlockBase) || !(newBlock instanceof BlockBase);
    //	}

    @Nonnull
    public Direction getFacing() {
        return getWorld().getBlockState(getPos()).get(BlockBase.FACING);
    }

}
