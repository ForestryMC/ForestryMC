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
import net.minecraft.inventory.Slot;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.factory.gadgets.MachineFermenter;

public class ContainerFermenter extends ContainerLiquidTanks {

	protected final MachineFermenter fermenter;

	public ContainerFermenter(InventoryPlayer player, MachineFermenter fermenter) {
		super(fermenter);

		this.fermenter = fermenter;

		this.addSlotToContainer(new SlotFiltered(fermenter, MachineFermenter.SLOT_RESOURCE, 85, 23));
		this.addSlotToContainer(new SlotFiltered(fermenter, MachineFermenter.SLOT_FUEL, 75, 57));
		this.addSlotToContainer(new SlotOutput(fermenter, MachineFermenter.SLOT_CAN_OUTPUT, 150, 58));
		this.addSlotToContainer(new SlotFiltered(fermenter, MachineFermenter.SLOT_CAN_INPUT, 150, 22));
		this.addSlotToContainer(new SlotFiltered(fermenter, MachineFermenter.SLOT_INPUT, 10, 40));

		for (int i = 0; i < 3; ++i) {
			for (int var4 = 0; var4 < 9; ++var4) {
				this.addSlotToContainer(new Slot(player, var4 + i * 9 + 9, 8 + var4 * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(player, i, 8 + i * 18, 142));
		}

	}

}
