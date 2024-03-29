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

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.circuits.ISocketable;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;
import forestry.core.tiles.TileUtil;

public class PacketSocketUpdate extends ForestryPacket implements IForestryPacketClient {
	private final BlockPos pos;
	private final NonNullList<ItemStack> itemStacks;

	public <T extends BlockEntity & ISocketable> PacketSocketUpdate(T tile) {
		this.pos = tile.getBlockPos();

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
	protected void writeData(PacketBufferForestry data) {
		data.writeBlockPos(pos);
		data.writeItemStacks(itemStacks);
	}

	@OnlyIn(Dist.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, Player player) throws IOException {
			BlockPos pos = data.readBlockPos();
			NonNullList<ItemStack> itemStacks = data.readItemStacks();

			TileUtil.actOnTile(player.level, pos, ISocketable.class, socketable -> {
				for (int i = 0; i < itemStacks.size(); i++) {
					socketable.setSocket(i, itemStacks.get(i));
				}
			});
		}
	}
}
