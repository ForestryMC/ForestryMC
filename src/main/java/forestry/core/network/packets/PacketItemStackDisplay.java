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
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;
import forestry.core.tiles.IItemStackDisplay;
import forestry.core.tiles.TileForestry;
import forestry.core.tiles.TileUtil;

public class PacketItemStackDisplay extends ForestryPacket implements IForestryPacketClient {
	private final BlockPos pos;
	private final ItemStack itemStack;

	public <T extends TileForestry & IItemStackDisplay> PacketItemStackDisplay(T tile, ItemStack itemStack) {
		this.pos = tile.getPos();
		this.itemStack = itemStack;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeBlockPos(pos);
		data.writeItemStack(itemStack);
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.ITEMSTACK_DISPLAY;
	}

	@SideOnly(Side.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) throws IOException {
			BlockPos pos = data.readBlockPos();
			ItemStack itemStack = data.readItemStack();

			TileUtil.actOnTile(player.world, pos, IItemStackDisplay.class, tile -> tile.handleItemStackForDisplay(itemStack));
		}
	}
}
