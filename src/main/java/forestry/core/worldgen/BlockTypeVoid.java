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
package forestry.core.worldgen;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BlockTypeVoid extends BlockType {

	public BlockTypeVoid() {
		super(Blocks.air, 0);
	}

	@Override
	public void setBlock(World world, int x, int y, int z) {
		world.setBlockToAir(x, y, z);
	}

}
