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

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;

import forestry.apiculture.multiblock.IAlvearyController;
import forestry.apiculture.multiblock.TileAlveary;
import forestry.core.tiles.IRestrictedAccessTile;
import forestry.farming.multiblock.IFarmController;
import forestry.farming.tiles.TileFarm;

public class PacketAccessSwitch extends PacketCoordinates implements IForestryPacketServer {

	public PacketAccessSwitch() {
	}

	public PacketAccessSwitch(ChunkCoordinates coordinates) {
		super(PacketIdServer.ACCESS_SWITCH, coordinates);
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayerMP player) throws IOException {
		TileEntity tile = getTarget(player.worldObj);

		if (tile instanceof TileAlveary) {
			TileAlveary tileAlveary = (TileAlveary) tile;
			IAlvearyController alvearyController = tileAlveary.getAlvearyController();
			alvearyController.getAccessHandler().switchAccessRule(player);
		} else if (tile instanceof TileFarm) {
			TileFarm tileFarm = (TileFarm) tile;
			IFarmController farmController = tileFarm.getFarmController();
			farmController.getAccessHandler().switchAccessRule(player);
		} else if (tile instanceof IRestrictedAccessTile) {
			IRestrictedAccessTile restrictedAccessTile = (IRestrictedAccessTile) tile;
			restrictedAccessTile.getAccessHandler().switchAccessRule(player);
		}
	}
}
