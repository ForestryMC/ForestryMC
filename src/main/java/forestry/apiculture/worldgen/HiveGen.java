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
package forestry.apiculture.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import forestry.api.apiculture.hives.IHiveGen;

public abstract class HiveGen implements IHiveGen {

	@Override
	public boolean canReplace(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		Material material = block.getMaterial();
		return (material.isReplaceable() && !material.isLiquid()) ||
				material == Material.air ||
				material == Material.plants;
	}
}
