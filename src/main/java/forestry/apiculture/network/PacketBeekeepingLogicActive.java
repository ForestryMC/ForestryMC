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
package forestry.apiculture.network;

import java.io.IOException;

import net.minecraft.tileentity.TileEntity;

import forestry.api.apiculture.IBeeHousing;
import forestry.apiculture.BeekeepingLogic;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.PacketCoordinates;
import forestry.core.network.PacketId;
import forestry.core.proxy.Proxies;

public class PacketBeekeepingLogicActive extends PacketCoordinates {
	private BeekeepingLogic beekeepingLogic;

	public static void onPacketData(DataInputStreamForestry data) throws IOException {
		new PacketBeekeepingLogicActive(data);
	}

	private PacketBeekeepingLogicActive(DataInputStreamForestry data) throws IOException {
		super(data);
	}

	public PacketBeekeepingLogicActive(IBeeHousing tile) {
		super(PacketId.BEE_LOGIC_ACTIVE, tile.getCoordinates());
		this.beekeepingLogic = (BeekeepingLogic) tile.getBeekeepingLogic();
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		beekeepingLogic.writeData(data);
	}

	@Override
	protected void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);

		TileEntity tile = getTarget(Proxies.common.getRenderWorld());
		if (tile instanceof IBeeHousing) {
			IBeeHousing beeHousing = (IBeeHousing) tile;
			BeekeepingLogic beekeepingLogic = (BeekeepingLogic) beeHousing.getBeekeepingLogic();
			beekeepingLogic.readData(data);
		}
	}
}
