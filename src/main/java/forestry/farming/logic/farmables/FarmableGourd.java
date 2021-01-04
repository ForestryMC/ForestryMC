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
package forestry.farming.logic.farmables;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.api.farming.IFarmableInfo;
import forestry.core.utils.BlockUtil;
import forestry.farming.logic.crops.CropDestroy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FarmableGourd implements IFarmable {

    private final ItemStack seed;
    private final Block stem;
    private final Block fruit;

    public FarmableGourd(ItemStack seed, Block stem, Block fruit) {
        this.seed = seed;
        this.stem = stem;
        this.fruit = fruit;
    }

    @Override
    public boolean isSaplingAt(World world, BlockPos pos, BlockState blockState) {
        return blockState.getBlock() == stem;
    }

    @Override
    public ICrop getCropAt(World world, BlockPos pos, BlockState blockState) {
        if (blockState.getBlock() != fruit) {
            return null;
        }

        return new CropDestroy(world, blockState, pos, null);
    }

    @Override
    public boolean isGermling(ItemStack itemstack) {
        return ItemStack.areItemsEqual(itemstack, seed);
    }

    @Override
    public void addInformation(IFarmableInfo info) {
        info.addSeedlings(seed);
        info.addProducts(new ItemStack(fruit));
    }

    @Override
    public boolean isWindfall(ItemStack itemstack) {
        return false;
    }

    @Override
    public boolean plantSaplingAt(PlayerEntity player, ItemStack germling, World world, BlockPos pos) {
        return BlockUtil.setBlockWithPlaceSound(world, pos, stem.getDefaultState());
    }

}
