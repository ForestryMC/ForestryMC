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
import net.minecraft.world.World;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.ForestryPacket;
import forestry.core.network.ILocatedPacket;

public abstract class PacketEntityUpdate extends ForestryPacket implements ILocatedPacket {
	// sending
	private Entity entity;
	// receiving
	private int entityId;

	public PacketEntityUpdate() {
	}

	public PacketEntityUpdate(Entity entity) {
		this.entity = entity;
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeVarInt(entity.getEntityId());
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		entityId = data.readVarInt();
	}

	public Entity getTarget(World world) {
		return world.getEntityByID(entityId);
	}

	@Override
	public int getPosX() {
		return (int) entity.posX;
	}

	@Override
	public int getPosY() {
		return (int) entity.posY;
	}

	@Override
	public int getPosZ() {
		return (int) entity.posZ;
	}
}
