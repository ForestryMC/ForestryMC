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

import cpw.mods.fml.common.network.internal.FMLProxyPacket;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.IForestryPacket;

public abstract class PacketDummy implements IForestryPacket {
	protected PacketDummy() {

	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {

	}

	@Override
	public FMLProxyPacket getPacket() {
		return null;
	}

}
