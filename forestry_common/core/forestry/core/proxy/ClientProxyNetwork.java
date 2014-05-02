/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.proxy;

import forestry.core.network.ForestryPacket;

public class ClientProxyNetwork extends ProxyNetwork {

	@Override
	public void sendToServer(ForestryPacket packet) {
		Proxies.common.getClientInstance().getNetHandler().addToSendQueue(packet.getPacket());
	}
}
