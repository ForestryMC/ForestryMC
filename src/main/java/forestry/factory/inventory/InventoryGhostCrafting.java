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
package forestry.factory.inventory;

import net.minecraft.tileentity.TileEntity;

import forestry.core.access.IRestrictedAccess;
import forestry.core.inventory.InventoryAdapterTile;

public class InventoryGhostCrafting<T extends TileEntity & IRestrictedAccess> extends InventoryAdapterTile<T> {
	public final static int SLOT_CRAFTING_1 = 0;
	public final static int SLOT_CRAFTING_COUNT = 9;
	public final static int SLOT_CRAFTING_RESULT = 9;

	public InventoryGhostCrafting(T tile, int size) {
		super(tile, size, "CraftItems");
	}
}
