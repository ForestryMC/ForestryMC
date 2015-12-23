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

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketIdServer;
import forestry.core.network.packets.PacketString;
import forestry.mail.gui.ContainerLetter;

public class PacketLetterTextSet extends PacketString implements IForestryPacketServer {

	public PacketLetterTextSet() {
	}

	public PacketLetterTextSet(String string) {
		super(string);
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.LETTER_TEXT_SET;
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayerMP player) throws IOException {
		if (!(player.openContainer instanceof ContainerLetter)) {
			return;
		}

		((ContainerLetter) player.openContainer).handleSetText(this);
	}
}
