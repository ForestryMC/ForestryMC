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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import forestry.core.circuits.ContainerSolderingIron;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.PacketIdClient;

public class PacketGuiLayoutSelect extends PacketString implements IForestryPacketClient {

	public PacketGuiLayoutSelect() {
	}

	public PacketGuiLayoutSelect(String string) {
		super(string);
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		Container container = player.openContainer;
		if (!(container instanceof ContainerSolderingIron)) {
			return;
		}

		((ContainerSolderingIron) container).setLayout(getString());
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.GUI_LAYOUT_SELECT;
	}
}
