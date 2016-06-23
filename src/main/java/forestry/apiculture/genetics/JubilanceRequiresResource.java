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
package forestry.apiculture.genetics;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IJubilanceProvider;
import forestry.core.utils.ItemStackUtil;

public class JubilanceRequiresResource implements IJubilanceProvider {

	private final ItemStack blockRequired;

	public JubilanceRequiresResource(Block block, int meta) {
		this.blockRequired = new ItemStack(block, meta);
	}

	@Override
	public boolean isJubilant(IAlleleBeeSpecies species, IBeeGenome genome, IBeeHousing housing) {
		World world = housing.getWorld();
		ChunkCoordinates housingCoords = housing.getCoordinates();
		Block block;
		TileEntity tile;
		int meta;
		int i = 1;
		do {
			block = world.getBlock(housingCoords.posX, housingCoords.posY - i, housingCoords.posZ);
			meta = world.getBlockMetadata(housingCoords.posX, housingCoords.posY - i, housingCoords.posZ);
			tile = world.getTileEntity(housingCoords.posX, housingCoords.posY - i, housingCoords.posZ);
			i++;
		} while (tile instanceof IBeeHousing);

		return ItemStackUtil.equals(block, meta, blockRequired);
	}

}
