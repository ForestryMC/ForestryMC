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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.multiblock.IMultiblockComponent;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;
import forestry.core.tiles.TileUtil;

public class PacketAlvearyChange extends ForestryPacket implements IForestryPacketClient {
	private final BlockPos controllerPos;

	public PacketAlvearyChange(BlockPos controllerPos) {
		this.controllerPos = controllerPos;
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.ALVERAY_CONTROLLER_CHANGE;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeBlockPos(controllerPos);
	}

	@OnlyIn(Dist.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, PlayerEntity player) {
			BlockPos pos = data.readBlockPos();
			TileUtil.actOnTile(player.world, pos, IMultiblockComponent.class, tile -> tile.getMultiblockLogic().getController().reassemble());
		}
	}
}
