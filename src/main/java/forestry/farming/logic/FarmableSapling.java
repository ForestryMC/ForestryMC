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
package forestry.farming.logic;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.NetworkUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FarmableSapling implements IFarmable {
    protected final ItemStack germling;
    protected final boolean ignoreMetadata;
    protected final Block saplingBlock;
    protected final ItemStack[] windfall;

    public FarmableSapling(final ItemStack germling, final ItemStack[] windfall) {
        this(germling, windfall, true);
    }

    public FarmableSapling(final ItemStack germling, final ItemStack[] windfall, boolean addSubItems) {
        this.germling = germling;
        this.windfall = windfall;
        this.saplingBlock = ItemStackUtil.getBlock(germling);
        this.ignoreMetadata = addSubItems;
    }

    @Override
    public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
        ItemStack copy = germling.copy();
        player.setHeldItem(EnumHand.MAIN_HAND, copy);
        EnumActionResult actionResult = copy.onItemUse(player, world, pos.down(), EnumHand.MAIN_HAND, EnumFacing.UP, 0, 0, 0);
        player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
        if (actionResult == EnumActionResult.SUCCESS) {
            PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.SoundFXType.BLOCK_PLACE, pos, Blocks.SAPLING.getDefaultState());
            NetworkUtil.sendNetworkPacket(packet, pos, world);
            return true;
        }
        return false;
    }

    @Override
    public boolean isSaplingAt(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == this.saplingBlock;
    }

    @Override
    public ICrop getCropAt(World world, BlockPos pos, IBlockState blockState) {
        Block block = blockState.getBlock();
        if (!block.isWood(world, pos)) {
            return null;
        }

        return new CropDestroy(world, blockState, pos, null);
    }

    @Override
    public boolean isGermling(ItemStack itemstack) {
        if (ignoreMetadata) {
            return ItemStack.areItemsEqual(germling, new ItemStack((itemstack.getItem())));
        }
        return ItemStack.areItemsEqual(germling, itemstack);
    }

    @Override
    public boolean isWindfall(ItemStack itemstack) {
        for (ItemStack drop : windfall) {
            if (drop.isItemEqual(itemstack)) {
                return true;
            }
        }
        return false;
    }
}
