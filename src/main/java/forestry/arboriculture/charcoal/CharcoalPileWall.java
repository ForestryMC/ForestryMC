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
package forestry.arboriculture.charcoal;

import com.google.common.base.Preconditions;
import forestry.api.arboriculture.ICharcoalPileWall;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;

public class CharcoalPileWall implements ICharcoalPileWall {

    @Nullable
    private final BlockState blockState;
    @Nullable
    private final Block block;
    private final int charcoalAmount;

    public CharcoalPileWall(BlockState blockState, int charcoalAmount) {
        this.blockState = blockState;
        this.block = null;
        this.charcoalAmount = charcoalAmount;
    }

    public CharcoalPileWall(Block block, int charcoalAmount) {
        this.blockState = null;
        this.block = block;
        this.charcoalAmount = charcoalAmount;
    }

    @Override
    public int getCharcoalAmount() {
        return charcoalAmount;
    }

    @Override
    public boolean matches(BlockState state) {
        return block == state.getBlock() || blockState == state;
    }

    @Override
    public NonNullList<ItemStack> getDisplayItems() {
        if (block == null) {
            Preconditions.checkNotNull(blockState);
            return NonNullList.withSize(1, new ItemStack(blockState.getBlock()));    //TODO loss of properties?
        } else if (blockState == null) {
            Preconditions.checkNotNull(block);
            return NonNullList.withSize(1, new ItemStack(block));
        }
        return NonNullList.create();
    }

}
