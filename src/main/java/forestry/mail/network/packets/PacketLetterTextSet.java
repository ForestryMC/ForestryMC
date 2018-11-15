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
package forestry.mail.network.packets;

import net.minecraft.entity.player.EntityPlayerMP;

import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketHandlerServer;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdServer;
import forestry.mail.gui.ContainerLetter;

public class PacketLetterTextSet extends ForestryPacket implements IForestryPacketServer {
	private final String string;

	public PacketLetterTextSet(String string) {
		this.string = string;
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.LETTER_TEXT_SET;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeString(string);
	}

	public static class Handler implements IForestryPacketHandlerServer {

		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayerMP player) {
			if (player.openContainer instanceof ContainerLetter) {
				String string = data.readString();
				((ContainerLetter) player.openContainer).handleSetText(string);
			}
		}
	}
}
