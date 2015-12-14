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
import net.minecraft.entity.player.EntityPlayer;

import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorLogicSource;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.PacketIdClient;
import forestry.core.proxy.Proxies;

public class PacketErrorUpdateEntity extends PacketEntityUpdate implements IForestryPacketClient {
	private IErrorLogic errorLogic;

	public PacketErrorUpdateEntity() {
	}

	public PacketErrorUpdateEntity(Entity entity, IErrorLogicSource errorLogicSource) {
		super(entity);
		this.errorLogic = errorLogicSource.getErrorLogic();
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		errorLogic.writeData(data);
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		Entity entity = getTarget(Proxies.common.getRenderWorld());
		if (entity instanceof IErrorLogicSource) {
			IErrorLogicSource errorSourceTile = (IErrorLogicSource) entity;
			errorLogic = errorSourceTile.getErrorLogic();
			errorLogic.readData(data);
		}
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.ERROR_UPDATE_ENTITY;
	}
}
