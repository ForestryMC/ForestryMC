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
package forestry.core.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import forestry.api.climate.IClimateHousing;
import forestry.api.climate.IClimateLogic;
import forestry.core.climate.ClimateLogic;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;
import forestry.core.tiles.TileUtil;

public class PacketClimateUpdate extends ForestryPacket implements IForestryPacketClient {

	private BlockPos pos;
	private ClimateLogic container;
	
	public PacketClimateUpdate(BlockPos pos, ClimateLogic container) {
		this.pos = pos;
		this.container = container;
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.UPDATE_CLIMATE;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeBlockPos(pos);
		container.writeData(data);
	}

	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) throws IOException {
			BlockPos position = data.readBlockPos();
			IClimateHousing housing = TileUtil.getTile(player.world, position, IClimateHousing.class);
			if(housing == null){
				return;
			}
			IClimateLogic logic = housing.getLogic();
			if(logic instanceof IStreamable){
				IStreamable streamable = (IStreamable) logic;
				streamable.readData(data);
			}
			//housing.onUpdateClimate();
		}
	}
}
