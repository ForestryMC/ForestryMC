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
package forestry.core.utils;

import com.google.common.base.Preconditions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import net.minecraftforge.common.util.FakePlayer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.Forestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketServer;

public class NetworkUtil {
	public static <P extends IForestryPacketClient> void sendNetworkPacket(P packet, BlockPos pos, World world) {
		if (!(world instanceof WorldServer)) {
			return;
		}

		WorldServer worldServer = (WorldServer) world;
		PlayerChunkMap playerManager = worldServer.getPlayerChunkMap();

		int chunkX = pos.getX() >> 4;
		int chunkZ = pos.getZ() >> 4;

		for (Object playerObj : world.playerEntities) {
			if (playerObj instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) playerObj;

				if (playerManager.isPlayerWatchingChunk(player, chunkX, chunkZ)) {
					sendToPlayer(packet, player);
				}
			}
		}
	}

	public static void sendToPlayer(IForestryPacketClient packet, EntityPlayer entityplayer) {
		if (!(entityplayer instanceof EntityPlayerMP) || entityplayer instanceof FakePlayer) {
			return;
		}

		EntityPlayerMP player = (EntityPlayerMP) entityplayer;
		Forestry.getPacketHandler().sendPacket(packet.getPacket(), player);
	}

	public static void inventoryChangeNotify(EntityPlayer player) {
		if (player instanceof EntityPlayerMP) {
			((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void sendToServer(IForestryPacketServer packet) {
		NetHandlerPlayClient netHandler = Minecraft.getMinecraft().getConnection();
		Preconditions.checkNotNull(netHandler, "Tried to send packet before netHandler (client world) exists.");
		netHandler.sendPacket(packet.getPacket());
	}
}
