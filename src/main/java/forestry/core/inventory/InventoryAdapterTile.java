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
package forestry.core.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import forestry.core.access.IRestrictedAccess;

public class InventoryAdapterTile<T extends TileEntity & IRestrictedAccess> extends InventoryAdapterRestricted {

	protected final T tile;

	public InventoryAdapterTile(T tile, int size, String name) {
		this(tile, size, name, 64);
	}

	public InventoryAdapterTile(T tile, int size, String name, int stackLimit) {
		super(size, name, stackLimit, tile.getAccessHandler());
		this.tile = tile;
	}

	@Override
	public void markDirty() {
		super.markDirty();
		tile.markDirty();
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack stack, int side) {
		return false;
	}
}
