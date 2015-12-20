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
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.fluids.FluidStack;

import forestry.core.fluids.ITankManager;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.PacketIdClient;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.ILiquidTankTile;

public class PacketTankLevelUpdate extends PacketCoordinates implements IForestryPacketClient {

	private int tankIndex;
	private FluidStack contents;

	public PacketTankLevelUpdate() {
	}

	public PacketTankLevelUpdate(ILiquidTankTile tileEntity, int tankIndex, FluidStack contents) {
		super(tileEntity.getCoordinates());
		this.tankIndex = tankIndex;
		this.contents = contents;
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeVarInt(tankIndex);
		data.writeFluidStack(contents);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		tankIndex = data.readVarInt();
		contents = data.readFluidStack();
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		TileEntity tileEntity = getTarget(Proxies.common.getRenderWorld());
		if (tileEntity instanceof ILiquidTankTile) {
			ITankManager tankManager = ((ILiquidTankTile) tileEntity).getTankManager();
			tankManager.processTankUpdate(tankIndex, contents);
		}
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.TANK_LEVEL_UPDATE;
	}
}
