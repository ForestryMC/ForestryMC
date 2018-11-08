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
package forestry.core.items;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.blocks.IBlockRotatable;
import forestry.core.blocks.IBlockWithMeta;
import forestry.core.tiles.TileForestry;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemTooltipUtil;

public class ItemBlockForestry<B extends Block> extends ItemBlock {

	public ItemBlockForestry(B block) {
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public B getBlock() {
		//noinspection unchecked
		return (B) super.getBlock();
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

	@Override
	public String getTranslationKey(ItemStack itemstack) {
		Block block = getBlock();
		if (block instanceof IBlockWithMeta) {
			IBlockWithMeta blockMeta = (IBlockWithMeta) block;
			int meta = itemstack.getMetadata();
			return block.getTranslationKey() + "." + blockMeta.getNameFromMeta(meta);
		}
		return block.getTranslationKey();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
		super.addInformation(stack, world, tooltip, advanced);
		ItemTooltipUtil.addInformation(stack, world, tooltip, advanced);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
		boolean placed = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);

		if (placed) {
			if (block.hasTileEntity(newState)) {
				if (stack.getItem() instanceof ItemBlockNBT && stack.getTagCompound() != null) {
					TileForestry tile = TileUtil.getTile(world, pos, TileForestry.class);
					if (tile != null) {
						tile.readFromNBT(stack.getTagCompound());
						tile.setPos(pos);
					}
				}
			}

			if (block instanceof IBlockRotatable) {
				((IBlockRotatable) block).rotateAfterPlacement(player, world, pos, side);
			}
		}

		return placed;
	}
}
