/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.mail.proxy;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import forestry.core.network.PacketIds;
import forestry.core.proxy.Proxies;
import forestry.mail.POBoxInfo;
import forestry.mail.network.PacketPOBoxInfo;

public class ProxyMail {

	public void clearMailboxInfo() {
	}

	public void resetMailboxInfo() {
	}

	public void setPOBoxInfo(World world, String playername, POBoxInfo info) {
		for (int i = 0; i < world.playerEntities.size(); i++) {
			EntityPlayerMP player = (EntityPlayerMP) world.playerEntities.get(i);
			if (!player.getGameProfile().getId().equals(playername))
				continue;

			Proxies.net.sendToPlayer(new PacketPOBoxInfo(PacketIds.POBOX_INFO, info), player);
			break;
		}
	}
	
	public void addLocalizations() {
	}

}
