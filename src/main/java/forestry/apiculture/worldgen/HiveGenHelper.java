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

import forestry.api.apiculture.hives.IHiveGen;
import forestry.api.apiculture.hives.IHiveGenHelper;

public class HiveGenHelper implements IHiveGenHelper {

	private static final IHiveGen treeGen = new HiveGenTree();

	@Override
	public IHiveGen ground(Block... validGroundBlocks) {
		return new HiveGenGround(validGroundBlocks);
	}

	@Override
	public IHiveGen tree() {
		return treeGen;
	}
}
