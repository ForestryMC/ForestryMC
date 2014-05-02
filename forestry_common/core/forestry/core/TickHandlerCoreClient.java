/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core;

import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;

import forestry.core.config.Config;
import forestry.core.config.Version;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Utils;

public class TickHandlerCoreClient {
	public TickHandlerCoreClient() {
		FMLCommonHandler.instance().bus().register(this);
	}

	private static ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<String>();

	public void queueChatMessage(String message) {
		messages.add(message);
	}
	private boolean naggedVersion, naggedVerify;
	private boolean hasNaturalistView;

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		if (event.phase != Phase.END) return;

		EntityPlayer player = Proxies.common.getClientInstance().thePlayer;
		boolean hasNaturalistEye = Utils.hasNaturalistEye(player);
		if (hasNaturalistEye != hasNaturalistView) {
			hasNaturalistView = !hasNaturalistView;
			Proxies.common.getClientInstance().renderGlobal.markBlockRangeForRenderUpdate(
					(int) player.posX - 32, (int) player.posY - 32, (int) player.posZ - 32,
					(int) player.posX + 32, (int) player.posY + 32, (int) player.posZ + 32);
		}

		if (messages.size() > 0) {
			String message;
			while ((message = messages.poll()) != null) {
				player.addChatMessage(new ChatComponentText(message));
			}
		}

		/*
		 if (!Config.disableNags)
		 if (Proxies.common.isModLoaded("GregTech_Addon"))
		 queueChatMessage("\u00A76Forestry may have been modified by GregTech. It may behave unexpectedly and some config options may not work. Do not report issues with this install! You can disable this nag in the config.");
		 */

		if (!naggedVersion && !Config.disableVersionCheck && Version.needsUpdateNoticeAndMarkAsSeen()) {
			queueChatMessage(String.format("\u00A7cNew version of Forestry available: %s for Minecraft %s", Version.getRecommendedVersion(),
					Proxies.common.getMinecraftVersion()));
			queueChatMessage("\u00A7cThis message only displays once. Type '/forestry version' to see the changelog.");
			naggedVersion = true;
		}

		if (!naggedVerify && Config.invalidFingerprint) {
			queueChatMessage("\u00A76Forestry's jar file was tampered with. Some machines have shut down and beekeeping has grown dangerous. Get a new jar from the official download page to fix that!");
			naggedVerify = true;
		}
	}
}
