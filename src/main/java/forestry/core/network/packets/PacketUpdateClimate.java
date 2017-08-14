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

import forestry.api.greenhouse.IClimateHousing;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.core.climate.ClimateContainer;
import forestry.core.multiblock.MultiblockUtil;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;
import forestry.greenhouse.api.climate.IClimateContainer;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;

public class PacketUpdateClimate extends ForestryPacket implements IForestryPacketClient {

	BlockPos pos;
	ClimateContainer container;
	
	public PacketUpdateClimate(BlockPos pos, ClimateContainer container) {
		this.pos = pos;
		this.container = container;
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.UPDATE_CLIMATE;
	}

	@Override
	protected void writeData(PacketBufferForestry data) throws IOException {
		data.writeBlockPos(pos);
		container.writeData(data);
	}

	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) throws IOException {
			BlockPos position = data.readBlockPos();
			//TODO:Greenhouse Api
			/*IClimateHousing housing = TileUtil.getTile(player.world, position, IClimateHousing.class);
			if(housing != null){
				IClimateContainer container = housing.getClimateContainer();
				if(container != null && container instanceof IStreamable){
					IStreamable streamable = (IStreamable) container;
					streamable.readData(data);
					IClimateHousing parent = container.getParent();
					parent.onUpdateClimate();
				}
			}*/
			IGreenhouseControllerInternal controller = MultiblockUtil.getController(player.world, position, IGreenhouseComponent.class);
			if(controller == null){
				return;
			}
			IClimateContainer container = controller.getClimateContainer();
			if(container != null && container instanceof IStreamable){
				IStreamable streamable = (IStreamable) container;
				streamable.readData(data);
				IClimateHousing parent = container.getParent();
				parent.onUpdateClimate();
			}
		}
	}
}
