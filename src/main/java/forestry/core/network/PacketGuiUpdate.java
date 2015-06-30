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
package forestry.core.network;

import java.io.IOException;

import net.minecraft.tileentity.TileEntity;

import forestry.core.proxy.Proxies;

public class PacketGuiUpdate extends PacketCoordinates {

	private IStreamableGui guiDataTile;

	public static void onPacketData(DataInputStreamForestry data) throws IOException {
		new PacketGuiUpdate(data);
	}

	private PacketGuiUpdate(DataInputStreamForestry data) throws IOException {
		super(data);
	}

	public PacketGuiUpdate(IStreamableGui guiDataTile) {
		super(PacketId.TILE_FORESTRY_GUI_OPENED, guiDataTile.getCoordinates());
		this.guiDataTile = guiDataTile;
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		guiDataTile.writeGuiData(data);
	}

	@Override
	protected void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);

		TileEntity tile = getTarget(Proxies.common.getRenderWorld());
		if (tile instanceof IStreamableGui) {
			((IStreamableGui) tile).readGuiData(data);
		}
	}
}
