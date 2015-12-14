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

import forestry.core.gui.ContainerLiquidTanksSocketed;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.factory.inventory.InventorySqueezer;
import forestry.factory.tiles.TileSqueezer;

public class ContainerSqueezer extends ContainerLiquidTanksSocketed<TileSqueezer> {

	public ContainerSqueezer(InventoryPlayer player, TileSqueezer tile) {
		super(tile, player, 8, 84);

		// Resource inventory
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 3; column++) {
				addSlotToContainer(new SlotFiltered(tile, column + row * 3, 17 + column * 18, 21 + row * 18));
			}
		}

		// Remnants slot
		this.addSlotToContainer(new SlotOutput(tile, InventorySqueezer.SLOT_REMNANT, 97, 60));

		// Can slot
		this.addSlotToContainer(new SlotFiltered(tile, InventorySqueezer.SLOT_CAN_INPUT, 147, 24));
		// Output slot
		this.addSlotToContainer(new SlotOutput(tile, InventorySqueezer.SLOT_CAN_OUTPUT, 147, 60));
	}
}
