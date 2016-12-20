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
package forestry.apiculture.network.packets;

import java.io.IOException;

import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.apiculture.BeekeepingLogic;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.PacketIdClient;
import forestry.core.proxy.Proxies;
import net.minecraft.util.math.BlockPos;

public class PacketBeeLogicActive extends ForestryPacket implements IForestryPacketClient {
	private final BlockPos tilePos;
	private final BeekeepingLogic beekeepingLogic;

	public PacketBeeLogicActive(IBeeHousing tile) {
		this.tilePos = tile.getCoordinates();
		this.beekeepingLogic = (BeekeepingLogic) tile.getBeekeepingLogic();
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.BEE_LOGIC_ACTIVE;
	}

	@Override
	protected void writeData(PacketBufferForestry data) throws IOException {
		data.writeBlockPos(tilePos);
		beekeepingLogic.writeData(data);
	}

	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) throws IOException {
			BlockPos tilePos = data.readBlockPos();

			TileEntity tile = Proxies.common.getRenderWorld().getTileEntity(tilePos);
			if (tile instanceof IBeeHousing) {
				IBeeHousing beeHousing = (IBeeHousing) tile;
				IBeekeepingLogic beekeepingLogic = beeHousing.getBeekeepingLogic();
				if (beekeepingLogic instanceof BeekeepingLogic) {
					((BeekeepingLogic) beekeepingLogic).readData(data);
				}
			}
		}
	}
}
