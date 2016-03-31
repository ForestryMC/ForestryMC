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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.apiculture.multiblock.IAlvearyControllerInternal;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.PacketIdClient;
import forestry.core.network.packets.PacketCoordinates;
import forestry.core.proxy.Proxies;

public class PacketAlveryChange extends PacketCoordinates implements IForestryPacketClient {

	public PacketAlveryChange() {
	}

	public PacketAlveryChange(IAlvearyControllerInternal controller) {
		super(controller.getReferenceCoord());
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.ALVERAY_CONTROLLER_CHANGE;
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) {
		TileEntity tile = getTarget(Proxies.common.getRenderWorld());
		if (tile instanceof IMultiblockComponent) {
			((IMultiblockComponent) tile).getMultiblockLogic().getController().reassemble();;
		}
	}
}
