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
package forestry.arboriculture.blocks;

import java.util.List;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import forestry.api.arboriculture.EnumWoodType;
import forestry.api.arboriculture.TreeManager;

public class BlockLog extends BlockWood {

	public static final PropertyEnum AXIS = PropertyEnum.create("axis", Axis.class);
	
	public static enum Axis implements IStringSerializable {
		NORMAL, SIDE, SIDE_90;

		@Override
		public String getName() {
			return name().toLowerCase();
		}
	}
	
	public BlockLog(boolean fireproof) {
		super("log", fireproof);
		setResistance(5.0F);
		
		harvestTool = new String[EnumWoodType.values().length];
		harvestLevel = new int[harvestTool.length];
		for(int i = 0;i < harvestTool.length;i++){
			harvestLevel[i] = -1;
		}
		
		setHarvestLevel("axe", 0);
		setDefaultState(this.blockState.getBaseState().withProperty(AXIS, Axis.NORMAL).withProperty(EnumWoodType.WOODTYPE, EnumWoodType.LARCH));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(AXIS, Axis.values()[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((Axis) state.getValue(AXIS)).ordinal();
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { EnumWoodType.WOODTYPE, AXIS });
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		super.breakBlock(world, pos, state);
		
		byte radius = 4;
		int boundary = radius + 1;
		
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		if (world.isAreaLoaded(new BlockPos(x - boundary, y - boundary, z - boundary), new BlockPos(x + boundary, y + boundary, z + boundary))) {
			for (int i = -radius; i <= radius; ++i) {
				for (int j = -radius; j <= radius; ++j) {
					for (int k = -radius; k <= radius; ++k) {
						IBlockState neighbor = world.getBlockState(new BlockPos(x + i, y + j, z + k));

						neighbor.getBlock().beginLeavesDecay(world, new BlockPos(x + i, y + j, z + k));
					}
				}
			}
		}
	}
	
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		byte b0 = 0;

		switch (facing) {
			case DOWN:
			case UP:
				b0 = 0;
				break;
			case NORTH:
			case SOUTH:
				b0 = 1;
				break;
			case WEST:
			case EAST:
				b0 = 2;
		}

		return getStateFromMeta(b0);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (EnumWoodType woodType : EnumWoodType.VALUES) {
			list.add(TreeManager.woodItemAccess.getLog(woodType, isFireproof()));
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
