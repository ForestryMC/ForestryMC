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
package forestry.arboriculture.network;

import java.io.IOException;

import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;
import forestry.core.tiles.TileUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketRipeningUpdate extends ForestryPacket implements IForestryPacketClient {
	private final BlockPos pos;
	private final int value;

	public PacketRipeningUpdate(TileLeaves leaves) {
		this.pos = leaves.getPos();
		this.value = leaves.getFruitColour();
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.RIPENING_UPDATE;
	}

	@Override
	protected void writeData(PacketBufferForestry data) throws IOException {
		data.writeBlockPos(pos);
		data.writeVarInt(value);
	}

	@SideOnly(Side.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) throws IOException {
			BlockPos pos = data.readBlockPos();
			int value = data.readVarInt();

			TileUtil.actOnTile(player.world, pos, IRipeningPacketReceiver.class, tile -> tile.fromRipeningPacket(value));
		}
	}
}
