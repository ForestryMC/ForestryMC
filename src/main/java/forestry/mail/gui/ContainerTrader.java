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
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import forestry.api.mail.IMailAddress;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.slots.SlotClosed;
import forestry.core.gui.slots.SlotCustom;
import forestry.core.gui.slots.SlotForestry;
import forestry.mail.TradeStation;
import forestry.mail.gadgets.MachineTrader;
import forestry.mail.items.ItemStamps;

public class ContainerTrader extends ContainerForestry {

	private final MachineTrader machine;

	public ContainerTrader(InventoryPlayer player, MachineTrader tile) {
		super(tile);

		machine = tile;
		IInventory inventory = machine.getOrCreateTradeInventory();

		// Trade good
		this.addSlotToContainer(new SlotForestry(inventory, TradeStation.SLOT_TRADEGOOD, 78, 109).blockShift());

		// Exchange
		for (int row = 0; row < 2; row++) {
			for (int col = 0; col < 2; col++) {
				addSlotToContainer(new SlotForestry(inventory, TradeStation.SLOT_EXCHANGE_1 + col + row * 2, 69 + col * 18, 55 + row * 18).blockShift());
			}
		}

		// Stamps
		for (int row = 0; row < 2; row++) {
			for (int col = 0; col < 2; col++) {
				addSlotToContainer(new SlotCustom(inventory, TradeStation.SLOT_STAMPS_1 + col + row * 2, 15 + col * 18, 37 + row * 18, ItemStamps.class));
			}
		}

		// Letters
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 2; col++) {
				addSlotToContainer(new SlotCustom(inventory, TradeStation.SLOT_LETTERS_1 + col + row * 2, 15 + col * 18, 73 + row * 18, Items.paper));
			}
		}

		// Buffers
		final int bufCols = 5;
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < bufCols; col++) {
				addSlotToContainer(new SlotClosed(inventory, TradeStation.SLOT_RECEIVE_BUFFER + col + row * bufCols, 123 + col * 18, 19 + row * 18));
			}
		}

		for (int row = 0; row < 2; row++) {
			for (int col = 0; col < bufCols; col++) {
				addSlotToContainer(new Slot(inventory, TradeStation.SLOT_SEND_BUFFER + col + row * bufCols, 123 + col * 18, (19 + (18 * 4)) + row * 18));
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
