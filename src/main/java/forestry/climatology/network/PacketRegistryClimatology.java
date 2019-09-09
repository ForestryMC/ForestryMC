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
package forestry.climatology.network;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.climatology.network.packets.PacketSelectClimateTargeted;
import forestry.core.network.IPacketRegistry;
import forestry.core.network.PacketIdServer;

public class PacketRegistryClimatology implements IPacketRegistry {
	@Override
	public void registerPacketsServer() {
		PacketIdServer.SELECT_CLIMATE_TARGETED.setPacketHandler(new PacketSelectClimateTargeted.Handler());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerPacketsClient() {
	}
}
