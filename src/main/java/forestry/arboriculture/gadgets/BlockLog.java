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
package forestry.arboriculture.gadgets;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import forestry.arboriculture.WoodType;

public class BlockLog extends BlockWood {

	public BlockLog(boolean fireproof) {
		super("log", fireproof, "logs");
		setResistance(5.0F);
		setHarvestLevel("axe", 0);
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		byte radius = 4;
		int boundary = radius + 1;
		
		if (world.isAreaLoaded(new BlockPos(pos.getX() - boundary, pos.getY() - boundary, pos.getZ() - boundary), new BlockPos(pos.getX() + boundary, pos.getY() + boundary, pos.getZ() + boundary))) {
			for (int i = -radius; i <= radius; ++i) {
				for (int j = -radius; j <= radius; ++j) {
					for (int k = -radius; k <= radius; ++k) {
						Block neighbor = world.getBlockState(new BlockPos(pos.getX() + i, pos.getY() + j, pos.getZ() + k)).getBlock();

						neighbor.beginLeavesDecay(world, new BlockPos(pos.getX() + i, pos.getY() + j, pos.getZ() + k));
					}
				}
			}
		}
	}
	
	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		byte b0 = 0;

		switch (side.ordinal()) {
			case 0:
			case 1:
				b0 = 0;
				break;
			case 2:
			case 3:
				b0 = 8;
				break;
			case 4:
			case 5:
				b0 = 4;
		}

		return getStateFromMeta(getMetaFromState(world.getBlockState(pos)) | b0);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (WoodType woodType : WoodType.VALUES) {
			list.add(woodType.getLog(isFireproof()));
		}
	}

	/* PROPERTIES */
	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if (isFireproof()) {
			return 0;
		} else if (face == EnumFacing.DOWN) {
			return 20;
		} else if (face != EnumFacing.UP) {
			return 10;
		} else {
			return 5;
		}
	}
	
	@Override
	public boolean canSustainLeaves(IBlockAccess world, BlockPos pos) {
		return true;
	}
	
	@Override
	public boolean isWood(IBlockAccess world, BlockPos pos) {
		return true;
	}

}
