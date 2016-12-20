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

import forestry.core.circuits.ContainerSolderingIron;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class PacketGuiLayoutSelect extends ForestryPacket implements IForestryPacketClient {
	private final String string;

	public PacketGuiLayoutSelect(String string) {
		this.string = string;
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.GUI_LAYOUT_SELECT;
	}

	@Override
	protected void writeData(PacketBufferForestry data) throws IOException {
		data.writeString(string);
	}

	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) throws IOException {
			String string = data.readString();
			Container container = player.openContainer;
			if (!(container instanceof ContainerSolderingIron)) {
				return;
			}

			((ContainerSolderingIron) container).setLayout(string);
		}
	}
}
