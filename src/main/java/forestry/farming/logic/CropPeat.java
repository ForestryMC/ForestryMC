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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.core.blocks.BlockSoil;
import forestry.core.proxy.Proxies;
import forestry.core.utils.vect.Vect;
import forestry.plugins.PluginCore;

public class CropPeat extends Crop {

	public CropPeat(World world, Vect position) {
		super(world, position);
	}

	@Override
	protected boolean isCrop(Vect pos) {
		Block block = getBlock(pos);
		if (!(block instanceof BlockSoil)) {
			return false;
		}

		BlockSoil blockSoil = (BlockSoil) block;
		BlockSoil.SoilType soilType = blockSoil.getTypeFromMeta(getBlockMeta(pos));
		return soilType == BlockSoil.SoilType.PEAT;
	}

	@Override
	protected Collection<ItemStack> harvestBlock(Vect pos) {
		List<ItemStack> drops = new ArrayList<>();
		drops.add(PluginCore.items.peat.getItemStack());

		Proxies.common.addBlockDestroyEffects(world, pos.x, pos.y, pos.z, world.getBlock(pos.x, pos.y, pos.z), 0);
		setBlock(pos, Blocks.dirt, 0);
		return drops;
	}

}
