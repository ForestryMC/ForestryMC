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
package forestry.core.utils;

import net.minecraft.entity.player.EntityPlayer;

import forestry.core.gadgets.TileForestry;

public class TileInventoryAdapter extends InventoryAdapter {

	TileForestry tile;

	public TileInventoryAdapter(TileForestry tile, int size, String name) {
		super(size, name);
		this.tile = tile;
	}

	public TileInventoryAdapter(TileForestry tile, int size, String name, int stackLimit) {
		super(size, name, stackLimit);
		this.tile = tile;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return tile.isUseableByPlayer(entityplayer);
	}

	@Override
	public void markDirty() {
		super.markDirty();
		tile.markDirty();
	}
}
