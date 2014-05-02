/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.mail.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

import forestry.core.gui.ContainerForestry;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;
import forestry.core.utils.InventoryAdapter;
import forestry.mail.gadgets.MachineTrader;

public class ContainerTradeName extends ContainerForestry {

	boolean isLinked;
	MachineTrader machine;

	public ContainerTradeName(InventoryPlayer player, MachineTrader tile) {
		super(new InventoryAdapter(0, "Empty"));
		machine = tile;
		isLinked = machine.isLinked();
	}

	public String getMoniker() {
		return machine.getMoniker();
	}

	public void setMoniker(String moniker) {

		if (moniker == null || moniker.isEmpty())
			return;

		PacketPayload payload = new PacketPayload(0, 0, 1);
		payload.stringPayload[0] = moniker;

		PacketUpdate packet = new PacketUpdate(PacketIds.TRADING_MONIKER_SET, payload);
		Proxies.net.sendToServer(packet);

		machine.setMoniker(moniker);
	}

	public void handleSetMoniker(PacketUpdate packet) {
		machine.setMoniker(packet.payload.stringPayload[0]);
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

}
