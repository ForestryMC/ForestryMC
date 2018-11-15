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
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.apiculture.tiles.TileCandle;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;
import forestry.core.tiles.TileUtil;

public class PacketCandleUpdate extends ForestryPacket implements IForestryPacketClient {
	private final BlockPos pos;
	private final int colour;
	private final boolean lit;

	public PacketCandleUpdate(TileCandle tileCandle) {
		pos = tileCandle.getPos();
		colour = tileCandle.getColour();
		lit = tileCandle.isLit();
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.CANDLE_UPDATE;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeBlockPos(pos);
		data.writeInt(colour);
		data.writeBoolean(lit);
	}

	@SideOnly(Side.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) {
			BlockPos pos = data.readBlockPos();
			int colour = data.readInt();
			boolean lit = data.readBoolean();

			TileUtil.actOnTile(player.world, pos, TileCandle.class, tile -> tile.onPacketUpdate(colour, lit));
		}
	}
}
