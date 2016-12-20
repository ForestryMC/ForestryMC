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

import forestry.core.circuits.ISocketable;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

public class PacketSocketUpdate extends ForestryPacket implements IForestryPacketClient {
	private final BlockPos pos;
	private final NonNullList<ItemStack> itemStacks;

	public <T extends TileEntity & ISocketable> PacketSocketUpdate(T tile) {
		this.pos = tile.getPos();

		this.itemStacks = NonNullList.withSize(tile.getSocketCount(), ItemStack.EMPTY);
		for (int i = 0; i < tile.getSocketCount(); i++) {
			this.itemStacks.set(i, tile.getSocket(i));
		}
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.SOCKET_UPDATE;
	}

	@Override
	protected void writeData(PacketBufferForestry data) throws IOException {
		data.writeBlockPos(pos);
		data.writeItemStacks(itemStacks);
	}

	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) throws IOException {
			BlockPos pos = data.readBlockPos();
			NonNullList<ItemStack> itemStacks = data.readItemStacks();

			TileEntity tile = player.world.getTileEntity(pos);
			if (tile instanceof ISocketable) {
				ISocketable socketable = (ISocketable) tile;
				for (int i = 0; i < itemStacks.size(); i++) {
					socketable.setSocket(i, itemStacks.get(i));
				}
			}
		}
	}
}
