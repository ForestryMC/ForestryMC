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
import forestry.factory.gadgets.MachineBottler;

public class ContainerBottler extends ContainerLiquidTanks<MachineBottler> {

	public ContainerBottler(InventoryPlayer player, MachineBottler tile) {
		super(tile, player, 8, 84);

		this.addSlotToContainer(new SlotFiltered(tile, MachineBottler.SLOT_INPUT_EMPTY_CAN, 116, 19));
		this.addSlotToContainer(new SlotOutput(tile, MachineBottler.SLOT_OUTPUT, 116, 55));
		this.addSlotToContainer(new SlotFiltered(tile, MachineBottler.SLOT_INPUT_FULL_CAN, 26, 38));
	}
}
