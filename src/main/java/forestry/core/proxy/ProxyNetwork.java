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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.FakePlayer;

import forestry.Forestry;
import forestry.core.config.Defaults;
import forestry.core.network.ForestryPacket;
import forestry.core.network.ILocatedPacket;

public class ProxyNetwork {

	public <P extends ForestryPacket & ILocatedPacket> void sendNetworkPacket(P packet) {
		if (packet == null) {
			return;
		}
		int x = packet.getPosX();
		int y = packet.getPosY();
		int z = packet.getPosZ();

		World[] worlds = DimensionManager.getWorlds();
		for (World world : worlds) {
			for (int j = 0; j < world.playerEntities.size(); j++) {
				EntityPlayerMP player = (EntityPlayerMP) world.playerEntities.get(j);
				if (isPlayerInRange(player, x, y, z)) {
					Forestry.packetHandler.sendPacket(packet.getPacket(), player);
				}
			}
		}
	}

	private static boolean isPlayerInRange(EntityPlayerMP player, int x, int y, int z) {
		return Math.abs(player.posX - x) <= Defaults.NET_MAX_UPDATE_DISTANCE &&
				Math.abs(player.posY - y) <= Defaults.NET_MAX_UPDATE_DISTANCE &&
				Math.abs(player.posZ - z) <= Defaults.NET_MAX_UPDATE_DISTANCE;
	}

	public void sendToPlayer(ForestryPacket packet, EntityPlayer entityplayer) {
		if (!(entityplayer instanceof EntityPlayerMP) || (entityplayer instanceof FakePlayer)) {
			return;
		}

		EntityPlayerMP player = (EntityPlayerMP) entityplayer;
		Forestry.packetHandler.sendPacket(packet.getPacket(), player);
	}

	public void sendToServer(ForestryPacket packet) {
	}

	public void inventoryChangeNotify(EntityPlayer player) {
		if (player instanceof EntityPlayerMP) {
			((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
		}
	}
}
