/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
