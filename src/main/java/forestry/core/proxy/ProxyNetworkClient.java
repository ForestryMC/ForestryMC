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
package forestry.core.proxy;

import com.google.common.base.Preconditions;
import forestry.core.network.IForestryPacketServer;
import net.minecraft.client.network.NetHandlerPlayClient;

@SuppressWarnings("unused")
public class ProxyNetworkClient extends ProxyNetwork {

	@Override
	public void sendToServer(IForestryPacketServer packet) {
		NetHandlerPlayClient netHandler = Proxies.common.getClientInstance().getConnection();
		Preconditions.checkNotNull(netHandler, "Tried to send packet before netHandler (client world) exists.");
		netHandler.sendPacket(packet.getPacket());
	}
}
