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
package forestry.apiculture.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import forestry.apiculture.gui.ContainerImprinter;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.PacketIdClient;
import forestry.core.network.packets.PacketGuiSelect;

public class PacketImprintSelectionResponse extends PacketGuiSelect implements IForestryPacketClient {

	public PacketImprintSelectionResponse() {
	}

	public PacketImprintSelectionResponse(int primaryIndex, int secondaryIndex) {
		super(primaryIndex, secondaryIndex);
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.IMPRINT_SELECTION_RESPONSE;
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		Container container = player.openContainer;
		if (!(container instanceof ContainerImprinter)) {
			return;
		}

		((ContainerImprinter) container).setSelection(this);
	}
}
