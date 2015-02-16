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
package forestry.mail.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.mail.IMailAddress;
import forestry.api.mail.PostManager;
import forestry.core.network.ForestryPacket;
import forestry.core.network.PacketIds;
import forestry.core.proxy.Proxies;
import forestry.core.utils.PlayerUtil;
import forestry.mail.POBox;
import forestry.mail.POBoxInfo;
import forestry.mail.PostRegistry;
import forestry.mail.gui.GuiMailboxInfo;

public class ClientProxyMail extends ProxyMail {

	@Override
	public void clearMailboxInfo() {
		GuiMailboxInfo.instance = null;
	}

	@Override
	public void resetMailboxInfo() {
		if (Proxies.common.getClientInstance().thePlayer == null || Proxies.common.getClientInstance().theWorld == null) {
			return;
		}

		GuiMailboxInfo.instance = new GuiMailboxInfo();

		if (!Proxies.common.isSimulating(Proxies.common.getRenderWorld())) {
			Proxies.net.sendToServer(new ForestryPacket(PacketIds.POBOX_INFO_REQUEST));
		} else {
			GameProfile profile = Proxies.common.getClientInstance().thePlayer.getGameProfile();
			IMailAddress address = PostManager.postRegistry.getMailAddress(profile);
			POBox pobox = PostRegistry.getPOBox(Proxies.common.getRenderWorld(), address);
			if (pobox != null) {
				setPOBoxInfo(Proxies.common.getRenderWorld(), address, pobox.getPOBoxInfo());
			}
		}
	}

	@Override
	public void setPOBoxInfo(World world, IMailAddress address, POBoxInfo info) {
		EntityPlayer clientPlayer = Proxies.common.getPlayer();
		if (PlayerUtil.isSameGameProfile(clientPlayer.getGameProfile(), address.getPlayerProfile())) {
			GuiMailboxInfo.instance.setPOBoxInfo(info);
		}
	}
}
