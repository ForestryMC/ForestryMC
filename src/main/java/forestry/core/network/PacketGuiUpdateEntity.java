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
package forestry.core.network;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import forestry.core.proxy.Proxies;

public class PacketGuiUpdateEntity extends PacketEntityUpdate implements IForestryPacketClient {
	private IStreamableGui streamableGui;

	public PacketGuiUpdateEntity() {
	}

	public PacketGuiUpdateEntity(IStreamableGui streamableGui, Entity entity) {
		super(PacketIdClient.GUI_UPDATE_ENTITY, entity);
		this.streamableGui = streamableGui;
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		streamableGui.writeGuiData(data);
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		Entity entity = getTarget(Proxies.common.getRenderWorld());
		if (entity instanceof IStreamableGui) {
			((IStreamableGui) entity).readGuiData(data);
		}
	}
}
