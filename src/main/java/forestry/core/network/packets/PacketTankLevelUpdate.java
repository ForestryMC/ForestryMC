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

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import forestry.core.fluids.ITankManager;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TileUtil;

public class PacketTankLevelUpdate extends ForestryPacket implements IForestryPacketClient {
	private final BlockPos pos;
	private final int tankIndex;
	@Nullable
	private final FluidStack contents;

	public PacketTankLevelUpdate(ILiquidTankTile tileEntity, int tankIndex, @Nullable FluidStack contents) {
		this.pos = tileEntity.getCoordinates();
		this.tankIndex = tankIndex;
		this.contents = contents;
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.TANK_LEVEL_UPDATE;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeBlockPos(pos);
		data.writeVarInt(tankIndex);
		data.writeFluidStack(contents);
	}

	@OnlyIn(Dist.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {

		@Override
		public void onPacketData(PacketBufferForestry data, Player player) {
			BlockPos pos = data.readBlockPos();
			int tankIndex = data.readVarInt();
			FluidStack contents = data.readFluidStack();

			TileUtil.actOnTile(player.level, pos, ILiquidTankTile.class, tile -> {
				ITankManager tankManager = tile.getTankManager();
				tankManager.processTankUpdate(tankIndex, contents);
			});
		}
	}
}
