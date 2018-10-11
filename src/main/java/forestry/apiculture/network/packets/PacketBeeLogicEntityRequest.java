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

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketHandlerServer;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdServer;

public class PacketBeeLogicEntityRequest extends ForestryPacket implements IForestryPacketServer {
	private final Entity entity;

	public PacketBeeLogicEntityRequest(Entity entity) {
		this.entity = entity;
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.BEE_LOGIC_ACTIVE_ENTITY_REQUEST;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeEntityById(entity);
	}

	public static class Handler implements IForestryPacketHandlerServer {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayerMP player) {
			Entity entity = data.readEntityById(player.world);
			if (entity instanceof IBeeHousing) {
				IBeeHousing beeHousing = (IBeeHousing) entity;
				IBeekeepingLogic beekeepingLogic = beeHousing.getBeekeepingLogic();
				beekeepingLogic.syncToClient(player);
			}
		}
	}
}
