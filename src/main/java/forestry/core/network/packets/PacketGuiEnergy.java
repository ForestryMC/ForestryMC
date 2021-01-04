/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.core.network.packets;

import forestry.core.gui.ContainerTile;
import forestry.core.network.*;

import net.minecraft.entity.player.PlayerEntity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PacketGuiEnergy extends ForestryPacket implements IForestryPacketClient {
	private final int windowId;
	private final int value;

	public PacketGuiEnergy(int windowId, int value) {
		this.windowId = windowId;
		this.value = value;
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.GUI_ENERGY;
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		data.writeVarInt(windowId);
		data.writeVarInt(value);
	}

	@OnlyIn(Dist.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, PlayerEntity player) {
			int windowId = data.readVarInt();
			int value = data.readVarInt();
			if (player.openContainer instanceof ContainerTile && player.openContainer.windowId == windowId) {
				((ContainerTile) player.openContainer).onGuiEnergy(value);
			}
		}
	}
}
