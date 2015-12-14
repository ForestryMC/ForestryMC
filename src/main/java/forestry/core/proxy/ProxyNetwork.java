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
import net.minecraft.server.management.PlayerManager;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import net.minecraftforge.common.util.FakePlayer;

import forestry.Forestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.ILocatedPacket;

public class ProxyNetwork {

	public <P extends IForestryPacketClient & ILocatedPacket> void sendNetworkPacket(P packet, World world) {
		if (packet == null || !(world instanceof WorldServer)) {
			return;
		}

		WorldServer worldServer = (WorldServer) world;
		PlayerManager playerManager = worldServer.getPlayerManager();

		int chunkX = packet.getPosX() >> 4;
		int chunkZ = packet.getPosZ() >> 4;

		for (Object playerObj : world.playerEntities) {
			if (playerObj instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) playerObj;

				if (playerManager.isPlayerWatchingChunk(player, chunkX, chunkZ)) {
					sendToPlayer(packet, player);
				}
			}
		}
	}

	public void sendToPlayer(IForestryPacketClient packet, EntityPlayer entityplayer) {
		if (!(entityplayer instanceof EntityPlayerMP) || (entityplayer instanceof FakePlayer)) {
			return;
		}

		EntityPlayerMP player = (EntityPlayerMP) entityplayer;
		Forestry.packetHandler.sendPacket(packet.getPacket(), player);
	}

	public void sendToServer(IForestryPacketServer packet) {
	}

	public void inventoryChangeNotify(EntityPlayer player) {
		if (player instanceof EntityPlayerMP) {
			((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
		}
	}
}
