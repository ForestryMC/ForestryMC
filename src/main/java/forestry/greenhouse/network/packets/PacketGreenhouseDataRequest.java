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
package forestry.greenhouse.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.multiblock.IGreenhouseComponent;
import forestry.core.multiblock.MultiblockUtil;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketHandlerServer;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdServer;
import forestry.core.utils.NetworkUtil;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;
import forestry.greenhouse.multiblock.blocks.storage.GreenhouseProviderServer;

public class PacketGreenhouseDataRequest extends ForestryPacket implements IForestryPacketServer {

	private BlockPos position;

	public PacketGreenhouseDataRequest(BlockPos position) {
		this.position = position;
	}

	@Override
	protected void writeData(PacketBufferForestry data) throws IOException {
		data.writeBlockPos(position);
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.GREENHOUSE_DATA_REQUEST;
	}

	public static class Handler implements IForestryPacketHandlerServer {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayerMP player) throws IOException {
			BlockPos position = data.readBlockPos();
			World world = player.world;
			IGreenhouseControllerInternal controller = MultiblockUtil.getController(world, position, IGreenhouseComponent.class);
			if (controller != null) {
				NetworkUtil.sendToPlayer(new PacketGreenhouseData(position, (GreenhouseProviderServer) controller.getProvider()), player);
			}
		}
	}
}
