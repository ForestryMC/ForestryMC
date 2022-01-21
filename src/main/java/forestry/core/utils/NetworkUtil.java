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
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;

import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketHandlerClient;
import forestry.core.network.PacketHandlerServer;

//import net.minecraft.server.management.PlayerChunkMap;

public class NetworkUtil {
	public static <P extends IForestryPacketClient> void sendNetworkPacket(P packet, BlockPos pos, Level world) {
		if (!(world instanceof ServerLevel)) {
			return;
		}

		ServerLevel worldServer = (ServerLevel) world;
		//		PlayerChunkMap playerManager = worldServer.getPlayerChunkMap();

		//		int chunkX = pos.getX() >> 4;
		//		int chunkZ = pos.getZ() >> 4;

		for (Player playerObj : world.players()) {
			if (playerObj instanceof ServerPlayer) {
				ServerPlayer player = (ServerPlayer) playerObj;

				if (true) {//TODO packet spam - playerManager.isPlayerWatchingChunk(player, chunkX, chunkZ)) {
					sendToPlayer(packet, player);
				}
			}
		}
	}

	public static void sendToPlayer(IForestryPacketClient packet, Player PlayerEntity) {
		if (!(PlayerEntity instanceof ServerPlayer) || PlayerEntity instanceof FakePlayer) {
			return;
		}

		ServerPlayer player = (ServerPlayer) PlayerEntity;
		PacketHandlerServer.sendPacket(packet, player);
	}

	public static void inventoryChangeNotify(Player player, AbstractContainerMenu container) {
		if (player instanceof ServerPlayer) {
			((ServerPlayer) player).refreshContainer(container);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void sendToServer(IForestryPacketServer packet) {
		ClientPacketListener netHandler = Minecraft.getInstance().getConnection();
		Preconditions.checkNotNull(netHandler, "Tried to send packet before netHandler (client world) exists.");
		PacketHandlerClient.sendPacket(packet);
	}
}
