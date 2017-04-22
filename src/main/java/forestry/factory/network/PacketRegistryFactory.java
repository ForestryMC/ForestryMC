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

import forestry.core.network.IPacketRegistry;
import forestry.core.network.PacketIdClient;
import forestry.core.network.PacketIdServer;
import forestry.factory.network.packets.PacketRecipeTransferRequest;
import forestry.factory.network.packets.PacketRecipeTransferUpdate;
import forestry.factory.network.packets.PacketWorktableMemoryUpdate;
import forestry.factory.network.packets.PacketWorktableRecipeRequest;
import forestry.factory.network.packets.PacketWorktableRecipeUpdate;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketRegistryFactory implements IPacketRegistry {
	@Override
	public void registerPacketsServer() {
		PacketIdServer.WORKTABLE_RECIPE_REQUEST.setPacketHandler(new PacketWorktableRecipeRequest.Handler());
		PacketIdServer.RECIPE_TRANSFER_REQUEST.setPacketHandler(new PacketRecipeTransferRequest.Handler());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerPacketsClient() {
		PacketIdClient.WORKTABLE_MEMORY_UPDATE.setPacketHandler(new PacketWorktableMemoryUpdate.Handler());
		PacketIdClient.WORKTABLE_CRAFTING_UPDATE.setPacketHandler(new PacketWorktableRecipeUpdate.Handler());
		PacketIdClient.RECIPE_TRANSFER_UPDATE.setPacketHandler(new PacketRecipeTransferUpdate.Handler());
	}
}
