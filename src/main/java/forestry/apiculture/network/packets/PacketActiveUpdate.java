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

import java.io.IOException;

import forestry.api.multiblock.IMultiblockComponent;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;
import forestry.core.tiles.IActivatable;
import forestry.core.tiles.TileUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketActiveUpdate extends ForestryPacket implements IForestryPacketClient {
	private final BlockPos pos;
	private final boolean active;

	public PacketActiveUpdate(IActivatable tile) {
		this.pos = tile.getCoordinates();
		this.active = tile.isActive();
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.TILE_FORESTRY_ACTIVE;
	}

	@Override
	protected void writeData(PacketBufferForestry data) throws IOException {
		data.writeBlockPos(pos);
		data.writeBoolean(active);
	}

	@SideOnly(Side.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) {
			BlockPos pos = data.readBlockPos();
			boolean active = data.readBoolean();

			WorldClient world = Minecraft.getMinecraft().world;
			TileEntity tile = TileUtil.getTile(world, pos);
			if (tile instanceof IActivatable) {
				((IActivatable) tile).setActive(active);
			} else if (tile instanceof IMultiblockComponent) {
				IMultiblockComponent component = (IMultiblockComponent) tile;
				if (component.getMultiblockLogic().isConnected() && component.getMultiblockLogic().getController() instanceof IActivatable) {
					((IActivatable) component.getMultiblockLogic().getController()).setActive(active);
				}
			}
		}
	}
}
