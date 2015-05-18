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

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.factory.gadgets.MachineSqueezer;

public class ContainerSqueezer extends ContainerLiquidTanks<MachineSqueezer> {

	public ContainerSqueezer(InventoryPlayer player, MachineSqueezer tile) {
		super(tile, player, 8, 84);

		// Resource inventory
		for (int l = 0; l < 3; l++) {
			for (int k = 0; k < 3; k++) {
				addSlotToContainer(new SlotFiltered(tile, k + l * 3, 19 + k * 18, 18 + l * 18));
			}
		}

		// Remnants slot
		this.addSlotToContainer(new SlotOutput(tile, MachineSqueezer.SLOT_REMNANT, 123, 19));

		// Can slot
		this.addSlotToContainer(new SlotFiltered(tile, MachineSqueezer.SLOT_CAN_INPUT, 106, 55));
		// Output slot
		this.addSlotToContainer(new SlotOutput(tile, MachineSqueezer.SLOT_CAN_OUTPUT, 140, 55));
	}
}
