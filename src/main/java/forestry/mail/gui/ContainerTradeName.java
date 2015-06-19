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

import org.apache.commons.lang3.StringUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

import forestry.api.mail.IMailAddress;
import forestry.api.mail.PostManager;
import forestry.core.network.PacketId;
import forestry.core.network.PacketString;
import forestry.core.proxy.Proxies;
import forestry.mail.gadgets.MachineTrader;

public class ContainerTradeName extends Container {

	protected final MachineTrader machine;

	public ContainerTradeName(InventoryPlayer player, MachineTrader tile) {
		machine = tile;
	}

	public IMailAddress getAddress() {
		return machine.getAddress();
	}

	public void setAddress(String addressName) {

		if (StringUtils.isBlank(addressName)) {
			return;
		}

		PacketString packet = new PacketString(PacketId.TRADING_ADDRESS_SET, addressName);
		Proxies.net.sendToServer(packet);

		IMailAddress address = PostManager.postRegistry.getMailAddress(addressName);
		machine.setAddress(address);
	}

	public void handleSetAddress(PacketString packet) {
		String addressName = packet.getString();
		IMailAddress address = PostManager.postRegistry.getMailAddress(addressName);
		machine.setAddress(address);
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityPlayer) {
		return machine.isOwner(entityPlayer);
	}
}
