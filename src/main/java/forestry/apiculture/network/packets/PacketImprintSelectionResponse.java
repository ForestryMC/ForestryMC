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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.apiculture.gui.ContainerImprinter;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;

public class PacketImprintSelectionResponse extends ForestryPacket implements IForestryPacketClient {
	private final int primary;
	private final int secondary;

	public PacketImprintSelectionResponse(int primaryIndex, int secondaryIndex) {
		this.primary = primaryIndex;
		this.secondary = secondaryIndex;
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.IMPRINT_SELECTION_RESPONSE;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeVarInt(primary);
		data.writeVarInt(secondary);
	}

	@SideOnly(Side.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) {
			Container container = player.openContainer;
			if (container instanceof ContainerImprinter) {
				int primaryIndex = data.readVarInt();
				int secondaryIndex = data.readVarInt();

				((ContainerImprinter) container).setSelection(primaryIndex, secondaryIndex);
			}
		}
	}
}
