/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import net.minecraftforge.common.DimensionManager;

import forestry.Forestry;
import forestry.core.config.Defaults;
import forestry.core.network.ForestryPacket;

public class ProxyNetwork {

	public void sendNetworkPacket(ForestryPacket packet, int x, int y, int z) {
		if (packet == null)
			return;

		World[] worlds = DimensionManager.getWorlds();
		for (int i = 0; i < worlds.length; i++)
			for (int j = 0; j < worlds[i].playerEntities.size(); j++) {
				EntityPlayerMP player = (EntityPlayerMP) worlds[i].playerEntities.get(j);

				if (Math.abs(player.posX - x) <= Defaults.NET_MAX_UPDATE_DISTANCE && Math.abs(player.posY - y) <= Defaults.NET_MAX_UPDATE_DISTANCE
						&& Math.abs(player.posZ - z) <= Defaults.NET_MAX_UPDATE_DISTANCE)
					Forestry.packetHandler.sendPacket(packet.getPacket(), player);
			}
	}

	public void sendToPlayer(ForestryPacket packet, EntityPlayer entityplayer) {
		if (!(entityplayer instanceof EntityPlayerMP))
			return;

		EntityPlayerMP player = (EntityPlayerMP) entityplayer;
		Forestry.packetHandler.sendPacket(packet.getPacket(), player);
	}

	public void sendToServer(ForestryPacket packet) {
	}

	public void inventoryChangeNotify(EntityPlayer player) {
		if (player instanceof EntityPlayerMP)
			((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
	}
}
