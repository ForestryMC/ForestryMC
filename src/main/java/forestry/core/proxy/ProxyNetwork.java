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
import net.minecraft.world.WorldServer;

import net.minecraftforge.common.util.FakePlayer;

import forestry.Forestry;
import forestry.core.network.ForestryPacket;
import forestry.core.network.ILocatedPacket;

public class ProxyNetwork {

	public <P extends ForestryPacket & ILocatedPacket> void sendNetworkPacket(P packet, World world) {
		if (packet == null || !(world instanceof WorldServer)) {
			return;
		}
		int x = packet.getPosX();
		int y = packet.getPosY();
		int z = packet.getPosZ();

		WorldServer worldServer = (WorldServer) world;
		int viewDistance = (worldServer.func_73046_m().getConfigurationManager().getViewDistance() + 1) * 16;

		for (Object playerObj : world.playerEntities) {
			if (playerObj instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) playerObj;

				if (isPlayerInRange(player, viewDistance, x, y, z)) {
					Forestry.packetHandler.sendPacket(packet.getPacket(), player);
				}
			}
		}
	}

	private static boolean isPlayerInRange(EntityPlayerMP player, int viewDistance, int x, int y, int z) {
		double distX = player.posX - x;
		double distY = player.posY - y;
		double distZ = player.posZ - z;
		return (distX * distX) + (distY * distY) + (distZ * distZ) < (viewDistance * viewDistance);
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
