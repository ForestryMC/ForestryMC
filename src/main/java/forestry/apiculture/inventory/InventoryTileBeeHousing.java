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
package forestry.apiculture.inventory;

import forestry.apiculture.InventoryBeeHousing;
import forestry.apiculture.tiles.TileAbstractBeeHousing;

public class InventoryTileBeeHousing extends InventoryBeeHousing {
	private final TileAbstractBeeHousing tile;

	public InventoryTileBeeHousing(TileAbstractBeeHousing tile, int size, String name) {
		super(size, name, tile.getAccessHandler());
		this.tile = tile;
	}

	@Override
	public void markDirty() {
		super.markDirty();
		tile.markDirty();
	}
}
