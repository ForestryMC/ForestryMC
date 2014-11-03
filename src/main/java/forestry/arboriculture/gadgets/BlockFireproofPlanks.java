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

import forestry.arboriculture.IWoodFireproof;
import forestry.core.config.ForestryBlock;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockFireproofPlanks extends BlockPlanks implements IWoodFireproof {

	public static Material fireproofWood = new Material(MapColor.woodColor);

	public BlockFireproofPlanks(PlankCat cat) {
		super(cat, fireproofWood);
	}

	public static ForestryBlock getFireproofPlanks(BlockPlanks planks) {
		return getFireproofPlanks(planks.cat);
	}

	private static ForestryBlock getFireproofPlanks(PlankCat plankCat) {
		switch (plankCat) {
			case CAT0: return ForestryBlock.fireproofPlanks1;
			case CAT1: return ForestryBlock.fireproofPlanks2;
		}
		throw new IllegalArgumentException("No fireproof log for this category: " + plankCat);
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
