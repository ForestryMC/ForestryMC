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
package forestry.factory.network;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.network.IPacketRegistry;
import forestry.core.network.PacketIdClient;
import forestry.core.network.PacketIdServer;
import forestry.factory.network.packets.PacketRecipeTransferRequest;
import forestry.factory.network.packets.PacketRecipeTransferUpdate;

public class PacketRegistryFactory implements IPacketRegistry {
	@Override
	public void registerPacketsServer() {
		PacketIdServer.RECIPE_TRANSFER_REQUEST.setPacketHandler(new PacketRecipeTransferRequest.Handler());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerPacketsClient() {
		PacketIdClient.RECIPE_TRANSFER_UPDATE.setPacketHandler(new PacketRecipeTransferUpdate.Handler());
	}
}
