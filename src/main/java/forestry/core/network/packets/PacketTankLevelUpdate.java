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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fluids.FluidStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

	@SideOnly(Side.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {

		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) {
			BlockPos pos = data.readBlockPos();
			int tankIndex = data.readVarInt();
			FluidStack contents = data.readFluidStack();

			TileUtil.actOnTile(player.world, pos, ILiquidTankTile.class, tile -> {
				ITankManager tankManager = tile.getTankManager();
				tankManager.processTankUpdate(tankIndex, contents);
			});
		}
	}
}
