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
package forestry.mail.inventory;

import net.minecraft.item.ItemStack;

import forestry.api.mail.IStamps;
import forestry.core.inventory.InventoryAdapterTile;
import forestry.mail.tiles.TileStampCollector;

public class InventoryStampCollector extends InventoryAdapterTile<TileStampCollector> {
	public static final short SLOT_FILTER = 0;
	public static final short SLOT_BUFFER_1 = 1;
	public static final short SLOT_BUFFER_COUNT = 27;

	public InventoryStampCollector(TileStampCollector tile) {
		super(tile, 28, "INV");
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		return itemStack.getItem() instanceof IStamps;
	}
}
