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

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

import forestry.api.mail.IMailAddress;
import forestry.core.gui.ContainerTile;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotForestry;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.tiles.TileUtil;
import forestry.mail.ModuleMail;
import forestry.mail.TradeStation;
import forestry.mail.tiles.TileTrader;

public class ContainerTrader extends ContainerTile<TileTrader> {

	public static ContainerTrader fromNetwork(int windowId, PlayerInventory inv, PacketBuffer data) {
		TileTrader tile = TileUtil.getTile(inv.player.world, data.readBlockPos(), TileTrader.class);
		return new ContainerTrader(windowId, inv, tile);    //TODO nullability.
	}

	public ContainerTrader(int windowId, PlayerInventory inv, TileTrader tile) {
		super(windowId, ModuleMail.getContainerTypes().TRADER, inv, tile, 33, 138);

		// Trade good
		this.addSlot(new SlotForestry(this.tile, TradeStation.SLOT_TRADEGOOD, 78, 109).blockShift());

		// Exchange
		for (int row = 0; row < 2; row++) {
			for (int col = 0; col < 2; col++) {
				addSlot(new SlotForestry(this.tile, TradeStation.SLOT_EXCHANGE_1 + col + row * 2, 69 + col * 18, 55 + row * 18).blockShift());
			}
		}

		// Stamps
		for (int row = 0; row < 2; row++) {
			for (int col = 0; col < 2; col++) {
				addSlot(new SlotFiltered(this.tile, TradeStation.SLOT_STAMPS_1 + col + row * 2, 15 + col * 18, 37 + row * 18));
			}
		}

		// Letters
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 2; col++) {
				addSlot(new SlotFiltered(this.tile, TradeStation.SLOT_LETTERS_1 + col + row * 2, 15 + col * 18, 73 + row * 18));
			}
		}

		// Buffers
		final int bufCols = 5;
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < bufCols; col++) {
				addSlot(new SlotOutput(this.tile, TradeStation.SLOT_RECEIVE_BUFFER + col + row * bufCols, 123 + col * 18, 19 + row * 18));
			}
		}

		for (int row = 0; row < 2; row++) {
			for (int col = 0; col < bufCols; col++) {
				addSlot(new SlotFiltered(this.tile, TradeStation.SLOT_SEND_BUFFER + col + row * bufCols, 123 + col * 18, 19 + 18 * 4 + row * 18));
			}
		}
	}

	public IMailAddress getAddress() {
		return tile.getAddress();
	}

}
