/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.mail.proxy;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.core.network.PacketIds;
import forestry.core.proxy.Proxies;
import forestry.mail.POBoxInfo;
import forestry.mail.network.PacketPOBoxInfo;

public class ProxyMail {

	public void clearMailboxInfo() {
	}

	public void resetMailboxInfo() {
	}

	public void setPOBoxInfo(World world, GameProfile playername, POBoxInfo info) {
		for (int i = 0; i < world.playerEntities.size(); i++) {
			EntityPlayerMP player = (EntityPlayerMP) world.playerEntities.get(i);
			if (!player.getGameProfile().equals(playername))
				continue;

			Proxies.net.sendToPlayer(new PacketPOBoxInfo(PacketIds.POBOX_INFO, info), player);
			break;
		}
	}

	public void addLocalizations() {
	}

}
