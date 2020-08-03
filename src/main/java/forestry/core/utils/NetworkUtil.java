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
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketHandlerClient;
import forestry.core.network.PacketHandlerServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;

//import net.minecraft.server.management.PlayerChunkMap;

public class NetworkUtil {
    public static <P extends IForestryPacketClient> void sendNetworkPacket(P packet, BlockPos pos, World world) {
        if (!(world instanceof ServerWorld)) {
            return;
        }

        ServerWorld worldServer = (ServerWorld) world;
        //		PlayerChunkMap playerManager = worldServer.getPlayerChunkMap();

        //		int chunkX = pos.getX() >> 4;
        //		int chunkZ = pos.getZ() >> 4;

        for (PlayerEntity playerObj : world.getPlayers()) {
            if (playerObj instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) playerObj;

                if (true) {//TODO packet spam - playerManager.isPlayerWatchingChunk(player, chunkX, chunkZ)) {
                    sendToPlayer(packet, player);
                }
            }
        }
    }

    public static void sendToPlayer(IForestryPacketClient packet, PlayerEntity PlayerEntity) {
        if (!(PlayerEntity instanceof ServerPlayerEntity) || PlayerEntity instanceof FakePlayer) {
            return;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) PlayerEntity;
        PacketHandlerServer.sendPacket(packet, player);
    }

    public static void inventoryChangeNotify(PlayerEntity player, Container container) {
        if (player instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity) player).sendContainerToPlayer(container);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendToServer(IForestryPacketServer packet) {
        ClientPlayNetHandler netHandler = Minecraft.getInstance().getConnection();
        Preconditions.checkNotNull(netHandler, "Tried to send packet before netHandler (client world) exists.");
        PacketHandlerClient.sendPacket(packet);
    }
}
