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

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import forestry.core.access.IRestrictedAccess;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketIdServer;

public class PacketAccessSwitchEntity extends PacketEntityUpdate implements IForestryPacketServer {

	public PacketAccessSwitchEntity() {
	}

	public PacketAccessSwitchEntity(Entity entity) {
		super(entity);
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayerMP player) throws IOException {
		Entity target = getTarget(player.worldObj);
		if (target instanceof IRestrictedAccess) {
			IRestrictedAccess restrictedAccessTile = (IRestrictedAccess) target;
			restrictedAccessTile.getAccessHandler().switchAccess(player);
		}
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.ACCESS_SWITCH_ENTITY;
	}
}
