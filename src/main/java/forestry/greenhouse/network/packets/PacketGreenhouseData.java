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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import forestry.api.greenhouse.IGreenhouseProvider;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.api.multiblock.IGreenhouseController;
import forestry.core.multiblock.MultiblockUtil;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;
import forestry.greenhouse.multiblock.blocks.storage.GreenhouseProvider;
import forestry.greenhouse.multiblock.blocks.storage.GreenhouseProviderClient;
import forestry.greenhouse.multiblock.blocks.storage.GreenhouseProviderServer;

public class PacketGreenhouseData extends ForestryPacket implements IForestryPacketClient {

	BlockPos pos;
	GreenhouseProviderServer provider;

	public PacketGreenhouseData(BlockPos pos, GreenhouseProviderServer provider) {
		this.pos = pos;
		this.provider = provider;
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.GREENHOUSE_DATA;
	}

	@Override
	protected void writeData(PacketBufferForestry data) throws IOException {
		data.writeBlockPos(pos);
		provider.writeData(data);
	}

	public static class Handler implements IForestryPacketHandlerClient {

		public Handler() {
		}

		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) throws IOException {
			BlockPos pos = data.readBlockPos();
			IGreenhouseController controller = MultiblockUtil.getController(player.world, pos, IGreenhouseComponent.class);
			if (controller == null) {
				return;
			}
			IGreenhouseProvider provider = controller.getProvider();
			if (provider instanceof GreenhouseProvider) {
				GreenhouseProviderClient providerClient = (GreenhouseProviderClient) provider;
				providerClient.readData(data);
			}
			//GreenhouseBlockManager manager = GreenhouseBlockManager.getInstance();
			provider.onBlockChange();
			//manager.markProviderDirty(player.world, controller.getProvider().getCenterPos(), provider);
		}
	}
}
