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

import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.core.network.ForestryPacket;
import forestry.core.network.PacketIds;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Localization;
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
		if (Proxies.common.getClientInstance().thePlayer == null)
			return;

		GuiMailboxInfo.instance = new GuiMailboxInfo();

		if (!Proxies.common.isSimulating(Proxies.common.getRenderWorld()))
			Proxies.net.sendToServer(new ForestryPacket(PacketIds.POBOX_INFO_REQUEST));
		else {
			POBox pobox = PostRegistry.getPOBox(Proxies.common.getRenderWorld(), Proxies.common.getClientInstance().thePlayer.getGameProfile());
			if (pobox != null)
				setPOBoxInfo(Proxies.common.getRenderWorld(), Proxies.common.getClientInstance().thePlayer.getGameProfile(), pobox.getPOBoxInfo());
		}
	}

	@Override
	public void setPOBoxInfo(World world, GameProfile playername, POBoxInfo info) {
		if (Proxies.common.getClientInstance().thePlayer == null || !Proxies.common.getClientInstance().thePlayer.getGameProfile().equals(playername))
			return;

		GuiMailboxInfo.instance.setPOBoxInfo(info);
	}

	@Override
	public void addLocalizations() {
		Localization.instance.addLocalization("/lang/forestry/mail/");
	}

}
