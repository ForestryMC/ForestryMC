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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import forestry.core.circuits.ISocketable;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.PacketIdClient;
import forestry.core.proxy.Proxies;

public class PacketSocketUpdate extends PacketCoordinates implements IForestryPacketClient {

	private ItemStack[] itemStacks;

	public PacketSocketUpdate() {
	}

	public <T extends TileEntity & ISocketable> PacketSocketUpdate(T tile) {
		super(tile);

		itemStacks = new ItemStack[tile.getSocketCount()];
		for (int i = 0; i < tile.getSocketCount(); i++) {
			itemStacks[i] = tile.getSocket(i);
		}
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeItemStacks(itemStacks);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		itemStacks = data.readItemStacks();
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		TileEntity tile = getTarget(Proxies.common.getRenderWorld());
		if (!(tile instanceof ISocketable)) {
			return;
		}

		ISocketable socketable = (ISocketable) tile;
		for (int i = 0; i < itemStacks.length; i++) {
			socketable.setSocket(i, itemStacks[i]);
		}
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.SOCKET_UPDATE;
	}
}
