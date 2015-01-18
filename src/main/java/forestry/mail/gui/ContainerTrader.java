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
package forestry.mail.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import forestry.api.mail.IMailAddress;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotForestry;
import forestry.core.gui.slots.SlotOutput;
import forestry.mail.TradeStation;
import forestry.mail.gadgets.MachineTrader;

public class ContainerTrader extends ContainerForestry {

	private final MachineTrader machine;

	public ContainerTrader(InventoryPlayer player, MachineTrader tile) {
		super(tile);

		machine = tile;

		// Trade good
		this.addSlotToContainer(new SlotForestry(machine, TradeStation.SLOT_TRADEGOOD, 78, 109).blockShift());

		// Exchange
		for (int row = 0; row < 2; row++) {
			for (int col = 0; col < 2; col++) {
				addSlotToContainer(new SlotForestry(machine, TradeStation.SLOT_EXCHANGE_1 + col + row * 2, 69 + col * 18, 55 + row * 18).blockShift());
			}
		}

		// Stamps
		for (int row = 0; row < 2; row++) {
			for (int col = 0; col < 2; col++) {
				addSlotToContainer(new SlotFiltered(machine, TradeStation.SLOT_STAMPS_1 + col + row * 2, 15 + col * 18, 37 + row * 18));
			}
		}

		// Letters
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 2; col++) {
				addSlotToContainer(new SlotFiltered(machine, TradeStation.SLOT_LETTERS_1 + col + row * 2, 15 + col * 18, 73 + row * 18));
			}
		}

		// Buffers
		final int bufCols = 5;
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < bufCols; col++) {
				addSlotToContainer(new SlotOutput(machine, TradeStation.SLOT_RECEIVE_BUFFER + col + row * bufCols, 123 + col * 18, 19 + row * 18));
			}
		}

		for (int row = 0; row < 2; row++) {
			for (int col = 0; col < bufCols; col++) {
				addSlotToContainer(new SlotFiltered(machine, TradeStation.SLOT_SEND_BUFFER + col + row * bufCols, 123 + col * 18, (19 + (18 * 4)) + row * 18));
			}
		}

		// Player inventory
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(player, j + i * 9 + 9, 33 + j * 18, 138 + i * 18));
			}
		}
		// Player hotbar
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(player, i, 33 + i * 18, 196));
		}
	}

	public IMailAddress getAddress() {
		return machine.getAddress();
	}

}
