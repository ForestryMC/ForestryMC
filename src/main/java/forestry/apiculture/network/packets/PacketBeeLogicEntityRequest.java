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

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketIdServer;
import forestry.core.network.packets.PacketEntityUpdate;

public class PacketBeeLogicEntityRequest extends PacketEntityUpdate implements IForestryPacketServer {

	public PacketBeeLogicEntityRequest() {
	}

	public PacketBeeLogicEntityRequest(Entity entity) {
		super(entity);
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.BEE_LOGIC_ACTIVE_ENTITY_REQUEST;
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayerMP player) throws IOException {
		Entity entity = getTarget(player.worldObj);
		if (entity instanceof IBeeHousing) {
			IBeeHousing beeHousing = (IBeeHousing) entity;
			IBeekeepingLogic beekeepingLogic = beeHousing.getBeekeepingLogic();
			if (beekeepingLogic != null) {
				beekeepingLogic.syncToClient(player);
			}
		}
	}

}
