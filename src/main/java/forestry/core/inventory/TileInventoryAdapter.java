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

import forestry.core.gadgets.TileForestry;
import forestry.core.utils.EnumAccess;
import forestry.core.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class TileInventoryAdapter extends InventoryAdapter {

	private final TileForestry tile;

	public TileInventoryAdapter(TileForestry tile, int size, String name) {
		this(tile, size, name, 64);
	}

	public TileInventoryAdapter(TileForestry tile, int size, String name, int stackLimit) {
		super(size, name, stackLimit);
		this.tile = tile;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		if (!Utils.isUseableByPlayer(player, tile))
			return false;
		return tile.allowsInteraction(player);
	}

	@Override
	public void markDirty() {
		super.markDirty();
		tile.markDirty();
	}

	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemstack) {
		return tile.getAccess() == EnumAccess.SHARED;
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack stack, int side) {
		return tile.getAccess() == EnumAccess.SHARED;
	}
}
