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
package forestry.apiculture.flowers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.world.World;

import forestry.api.apiculture.FlowerManager;
import forestry.api.genetics.IFlowerGrowthHelper;
import forestry.api.genetics.IFlowerGrowthRule;
import forestry.api.genetics.IFlowerRegistry;
import forestry.api.genetics.IIndividual;

public class GrowthRuleFlowerPot implements IFlowerGrowthRule {

	@Override
	public boolean growFlower(IFlowerRegistry fr, String flowerType, World world, IIndividual individual, int x, int y, int z) {
		return growFlower(flowerType, world, x, y, z);
	}

	@Override
	public boolean growFlower(IFlowerGrowthHelper helper, String flowerType, World world, int x, int y, int z) {
		return growFlower(flowerType, world, x, y, z);
	}

	private static boolean growFlower(String flowerType, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (!(tile instanceof TileEntityFlowerPot)) {
			return false;
		}

		TileEntityFlowerPot flowerPotTile = (TileEntityFlowerPot) tile;
		if (flowerPotTile.getFlowerPotItem() != null) {
			return false;
		}

		Block block = world.getBlock(x, y, z);
		if (!(block instanceof BlockFlowerPot)) {
			return false;
		}

		BlockFlowerPot flowerPot = (BlockFlowerPot) block;

		int flower;
		switch (flowerType) {
			case FlowerManager.FlowerTypeVanilla:
			case FlowerManager.FlowerTypeSnow:
				flower = world.rand.nextInt(2) + 1;
				break;
			case FlowerManager.FlowerTypeJungle:
				flower = 6;
				break;
			case FlowerManager.FlowerTypeCacti:
				flower = world.rand.nextInt(2) + 9;
				break;
			case FlowerManager.FlowerTypeMushrooms:
				flower = world.rand.nextInt(2) + 7;
				break;
			default:
				return false;
		}

		TileEntityFlowerPot newTile = (TileEntityFlowerPot) flowerPot.createNewTileEntity(world, flower);

		flowerPotTile.func_145964_a(newTile.getFlowerPotItem(), newTile.getFlowerPotData());
		flowerPotTile.markDirty();

		if (!world.setBlockMetadataWithNotify(x, y, z, 1, 2)) {
			world.markBlockForUpdate(x, y, z);
		}

		return true;
	}
}
