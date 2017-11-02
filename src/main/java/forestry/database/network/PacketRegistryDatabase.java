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
package forestry.database.network;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.network.IPacketRegistry;
import forestry.core.network.PacketIdServer;
import forestry.database.network.packets.PacketExtractItem;
import forestry.database.network.packets.PacketInsertItem;

public class PacketRegistryDatabase implements IPacketRegistry {
	@Override
	public void registerPacketsServer() {
		PacketIdServer.INSERT_ITEM.setPacketHandler(new PacketInsertItem.Handler());
		PacketIdServer.EXTRACT_ITEM.setPacketHandler(new PacketExtractItem.Handler());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerPacketsClient() {
	}
}
