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
package forestry.core.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;

import forestry.core.gui.IGuiSelectable;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketIdServer;

public class PacketGuiSelectRequest extends PacketGuiSelect implements IForestryPacketServer {

	public PacketGuiSelectRequest() {
	}

	public PacketGuiSelectRequest(int primaryIndex, int secondaryIndex) {
		super(primaryIndex, secondaryIndex);
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayerMP player) throws IOException {
		Container container = player.openContainer;
		if (!(container instanceof IGuiSelectable)) {
			return;
		}

		((IGuiSelectable) container).handleSelectionRequest(player, this);
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.GUI_SELECTION_REQUEST;
	}
}
