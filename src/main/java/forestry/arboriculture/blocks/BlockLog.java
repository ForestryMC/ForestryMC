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

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.arboriculture.render.IconProviderWood;
import forestry.arboriculture.tiles.TileWood;

public class BlockLog extends BlockWood {

	public BlockLog(boolean fireproof) {
		super("log", fireproof);
		setResistance(5.0F);
		setHarvestLevel("axe", 0);
	}

	@Override
	public int getRenderType() {
		return Blocks.log.getRenderType();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		super.breakBlock(world, x, y, z, block, meta);

		byte radius = 4;
		int boundary = radius + 1;

		if (world.checkChunksExist(x - boundary, y - boundary, z - boundary, x + boundary, y + boundary, z + boundary)) {
			for (int i = -radius; i <= radius; ++i) {
				for (int j = -radius; j <= radius; ++j) {
					for (int k = -radius; k <= radius; ++k) {
						Block neighbor = world.getBlock(x + i, y + j, z + k);

						neighbor.beginLeavesDecay(world, x + i, y + j, z + k);
					}
				}
			}
		}
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float par6, float par7, float par8, int meta) {
		byte b0 = 0;

		switch (side) {
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

		return meta | b0;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (EnumWoodType woodType : EnumWoodType.VALUES) {
			list.add(TreeManager.woodItemAccess.getLog(woodType, isFireproof()));
		}
	}

	/* ICONS */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return IconProviderWood.getLogIcon(EnumWoodType.LARCH, meta, side);
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		int meta = world.getBlockMetadata(x, y, z);
		TileWood wood = TileWood.getWoodTile(world, x, y, z);
		EnumWoodType type = wood.getWoodType();
		if (type == null) {
			return getIcon(side, meta);
		}
		return IconProviderWood.getLogIcon(type, meta, side);
	}

	/* PROPERTIES */
	@Override
	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		if (isFireproof()) {
			return 0;
		} else if (face == ForgeDirection.DOWN) {
			return 20;
		} else if (face != ForgeDirection.UP) {
			return 10;
		} else {
			return 5;
		}
	}

	@Override
	public boolean canSustainLeaves(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public boolean isWood(IBlockAccess world, int x, int y, int z) {
		return true;
	}

}
