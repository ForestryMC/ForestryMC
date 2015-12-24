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
package forestry.energy.tiles;

import net.minecraft.world.IBlockAccess;

import forestry.core.blocks.IMachinePropertiesTESR;
import forestry.core.tiles.MachineDefinition;
import forestry.core.tiles.TileEngine;
import forestry.core.tiles.TileUtil;

public class EngineDefinition extends MachineDefinition {

	public EngineDefinition(IMachinePropertiesTESR properties) {
		super(properties);
	}

	@Override
	public boolean isSolidOnSide(IBlockAccess world, int x, int y, int z, int side) {
		TileEngine tile = TileUtil.getTile(world, x, y, z, TileEngine.class);
		if (tile == null) {
			return false;
		}

		return tile.getOrientation().getOpposite().ordinal() == side;
	}
}
