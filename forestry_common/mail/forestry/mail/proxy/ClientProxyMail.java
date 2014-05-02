/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.mail.proxy;

import net.minecraft.world.World;

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
			POBox pobox = PostRegistry.getPOBox(Proxies.common.getRenderWorld(), Proxies.common.getClientInstance().thePlayer.getGameProfile().getId());
			if (pobox != null)
				setPOBoxInfo(Proxies.common.getRenderWorld(), Proxies.common.getClientInstance().thePlayer.getGameProfile().getId(), pobox.getPOBoxInfo());
		}
	}

	@Override
	public void setPOBoxInfo(World world, String playername, POBoxInfo info) {
		if (Proxies.common.getClientInstance().thePlayer == null || !Proxies.common.getClientInstance().thePlayer.getGameProfile().getId().equals(playername))
			return;

		GuiMailboxInfo.instance.setPOBoxInfo(info);
	}

	@Override
	public void addLocalizations() {
		Localization.instance.addLocalization("/lang/forestry/mail/");
	}

}
