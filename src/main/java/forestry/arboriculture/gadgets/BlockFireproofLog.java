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

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.arboriculture.IWoodFireproof;
import forestry.core.config.ForestryBlock;

public class BlockFireproofLog extends BlockLog implements IWoodFireproof {

	private static final Material fireproofWood = new Material(MapColor.woodColor);

	public BlockFireproofLog(LogCat cat) {
		super(cat, fireproofWood);
	}

	public static ForestryBlock getFireproofLog(BlockLog log) {
		return getFireproofLog(log.cat);
	}

	private static ForestryBlock getFireproofLog(BlockLog.LogCat logCat) {
		switch (logCat) {
			case CAT0:
				return ForestryBlock.fireproofLog1;
			case CAT1:
				return ForestryBlock.fireproofLog2;
			case CAT2:
				return ForestryBlock.fireproofLog3;
			case CAT3:
				return ForestryBlock.fireproofLog4;
			case CAT4:
				return ForestryBlock.fireproofLog5;
			case CAT5:
				return ForestryBlock.fireproofLog6;
			case CAT6:
				return ForestryBlock.fireproofLog7;
			case CAT7:
				return ForestryBlock.fireproofLog8;
		}
		throw new IllegalArgumentException("No fireproof log for this category: " + logCat);
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return 0;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return false;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return 0;
	}
}
