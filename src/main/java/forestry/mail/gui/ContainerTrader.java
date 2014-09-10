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

import forestry.core.gui.ContainerForestry;
import forestry.core.gui.slots.SlotCustom;
import forestry.mail.TradeStation;
import forestry.mail.gadgets.MachineTrader;
import forestry.mail.items.ItemStamps;
import forestry.api.mail.IMailAddress;

public class ContainerTrader extends ContainerForestry {

	private final MachineTrader machine;

	public ContainerTrader(InventoryPlayer player, MachineTrader tile) {
		super(tile);

		machine = tile;
		IInventory inventory = machine.getOrCreateTradeInventory();

		// Trade good
		this.addSlot(new Slot(inventory, TradeStation.SLOT_TRADEGOOD, 78, 109));

		// Exchange
		for (int i = 0; i < 2; i++)
			for (int j = 0; j < 2; j++)
				addSlot(new Slot(inventory, TradeStation.SLOT_EXCHANGE_1 + j + i * 2, 69 + j * 18, 55 + i * 18));

		// Stamps
		for (int i = 0; i < 2; i++)
			for (int j = 0; j < 2; j++)
				addSlot(new SlotCustom(inventory, TradeStation.SLOT_STAMPS_1 + j + i * 2, 15 + j * 18, 37 + i * 18, new Object[] { ItemStamps.class }));

		// Letters
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 2; j++)
				addSlot(new SlotCustom(inventory, TradeStation.SLOT_LETTERS_1 + j + i * 2, 15 + j * 18, 73 + i * 18, new Object[] { Items.paper }));

		// Buffers
		final int bufCols = 5;
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < bufCols; j++)
				addSlot(new Slot(inventory, TradeStation.SLOT_RECEIVE_BUFFER + j + i * bufCols, 123 + j * 18, 19 + i * 18));

		for (int i = 0; i < 2; i++)
			for (int j = 0; j < bufCols; j++)
				addSlot(new Slot(inventory, TradeStation.SLOT_SEND_BUFFER + j + i * bufCols, 123 + j * 18, (19 + (18 * 4)) + i * 18));

		// Player inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				addSlot(new Slot(player, j + i * 9 + 9, 33 + j * 18, 138 + i * 18));
		// Player hotbar
		for (int i = 0; i < 9; i++)
			addSlot(new Slot(player, i, 33 + i * 18, 196));

	}

	public boolean isLinked() {
		return machine.isLinked();
	}

	public IMailAddress getAddress() {
		return machine.getAddress();
	}

}
