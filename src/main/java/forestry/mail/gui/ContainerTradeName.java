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

import forestry.api.mail.IMailAddress;
import forestry.api.mail.PostManager;
import forestry.core.gui.ContainerForestry;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;
import forestry.mail.gadgets.MachineTrader;
import net.minecraft.entity.player.InventoryPlayer;
import org.apache.commons.lang3.StringUtils;

public class ContainerTradeName extends ContainerForestry {

	boolean isLinked;
	MachineTrader machine;

	public ContainerTradeName(InventoryPlayer player, MachineTrader tile) {
		super(new InventoryAdapter(0, "Empty"));
		machine = tile;
		isLinked = machine.isLinked();
	}

	public IMailAddress getAddress() {
		return machine.getAddress();
	}

	public void setAddress(String addressName) {

		if (StringUtils.isBlank(addressName))
			return;

		PacketPayload payload = new PacketPayload(0, 0, 1);
		payload.stringPayload[0] = addressName;

		PacketUpdate packet = new PacketUpdate(PacketIds.TRADING_ADDRESS_SET, payload);
		Proxies.net.sendToServer(packet);

		IMailAddress address = PostManager.postRegistry.getValidMailAddress(machine.getWorld(), addressName);
		machine.setAddress(address);
	}

	public void handleSetAddress(PacketUpdate packet) {
		String addressName = packet.payload.stringPayload[0];
		IMailAddress address = PostManager.postRegistry.getValidMailAddress(machine.getWorld(), addressName);
		machine.setAddress(address);
	}

}
