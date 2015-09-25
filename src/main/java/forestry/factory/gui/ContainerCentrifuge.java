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
package forestry.factory.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.core.gui.ContainerSocketed;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.factory.tiles.TileCentrifuge;

public class ContainerCentrifuge extends ContainerSocketed<TileCentrifuge> {

	public ContainerCentrifuge(InventoryPlayer player, TileCentrifuge tile) {
		super(tile, player, 8, 84);

		// Resource
		this.addSlotToContainer(new SlotFiltered(tile, 0, 30, 37));

		// Product Inventory
		for (int l = 0; l < 3; l++) {
			for (int k = 0; k < 3; k++) {
				addSlotToContainer(new SlotOutput(tile, 1 + k + l * 3, 98 + k * 18, 19 + l * 18));
			}
		}
	}

}
