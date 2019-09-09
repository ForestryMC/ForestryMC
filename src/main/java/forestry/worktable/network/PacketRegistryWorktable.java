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
package forestry.worktable.network;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.network.IPacketRegistry;
import forestry.core.network.PacketIdClient;
import forestry.core.network.PacketIdServer;
import forestry.worktable.network.packets.PacketWorktableMemoryUpdate;
import forestry.worktable.network.packets.PacketWorktableRecipeRequest;
import forestry.worktable.network.packets.PacketWorktableRecipeUpdate;

public class PacketRegistryWorktable implements IPacketRegistry {
	@Override
	public void registerPacketsServer() {
		PacketIdServer.WORKTABLE_RECIPE_REQUEST.setPacketHandler(new PacketWorktableRecipeRequest.Handler());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerPacketsClient() {
		PacketIdClient.WORKTABLE_MEMORY_UPDATE.setPacketHandler(new PacketWorktableMemoryUpdate.Handler());
		PacketIdClient.WORKTABLE_CRAFTING_UPDATE.setPacketHandler(new PacketWorktableRecipeUpdate.Handler());
	}
}
