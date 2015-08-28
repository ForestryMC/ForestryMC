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
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import forestry.api.apiculture.FlowerManager;
import forestry.api.genetics.IFlowerGrowthRule;
import forestry.api.genetics.IFlowerRegistry;
import forestry.api.genetics.IIndividual;

public class VanillaFlowerPotGrowthRule implements IFlowerGrowthRule {

	@Override
	public boolean growFlower(IFlowerRegistry fr, String flowerType, World world, IIndividual individual, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof TileEntityFlowerPot)) {
			return false;
		}

		TileEntityFlowerPot flowerPotTile = (TileEntityFlowerPot) tile;
		if (flowerPotTile.getFlowerPotItem() != null) {
			return false;
		}

		Block block = state.getBlock();
		if (!(block instanceof BlockFlowerPot)) {
			return false;
		}

		BlockFlowerPot flowerPot = (BlockFlowerPot) block;

		int flower;
		if (flowerType.equals(FlowerManager.FlowerTypeVanilla) || flowerType.equals(FlowerManager.FlowerTypeSnow)) {
			flower = world.rand.nextInt(2) + 1;
		} else if (flowerType.equals(FlowerManager.FlowerTypeJungle)) {
			flower = 6;
		} else if (flowerType.equals(FlowerManager.FlowerTypeCacti)) {
			flower = world.rand.nextInt(2) + 9;
		} else if (flowerType.equals(FlowerManager.FlowerTypeMushrooms)) {
			flower = world.rand.nextInt(2) + 7;
		} else {
			return false;
		}

		TileEntityFlowerPot newTile = (TileEntityFlowerPot) flowerPot.createNewTileEntity(world, flower);

		flowerPotTile.setFlowerPotData(newTile.getFlowerPotItem(), newTile.getFlowerPotData());
		flowerPotTile.markDirty();

		if (!world.setBlockState(pos, block.getStateFromMeta(1), 2)) {
			world.markBlockForUpdate(pos);
		}

		return true;
	}
}
