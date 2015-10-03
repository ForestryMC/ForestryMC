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

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import forestry.core.gui.slots.SlotCraftMatrix;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.IPacketHandler;
import forestry.core.network.PacketId;
import forestry.core.network.PacketNBT;
import forestry.core.recipes.nei.SetRecipeCommandHandler;
import forestry.factory.gui.ContainerWorktable;

public class PacketHandlerFactory implements IPacketHandler {

	private static final SetRecipeCommandHandler worktableNEISelectHandler = new SetRecipeCommandHandler(ContainerWorktable.class, SlotCraftMatrix.class);

	@Override
	public boolean onPacketData(PacketId packetID, DataInputStreamForestry data, EntityPlayer player) throws IOException {

		switch (packetID) {
			case WORKTABLE_MEMORY_UPDATE: {
				PacketWorktableMemoryUpdate.onPacketData(data);
				return true;
			}
			case WORKTABLE_NEI_SELECT: {
				PacketNBT packet = new PacketNBT(data);
				worktableNEISelectHandler.handle(packet.getTagCompound(), (EntityPlayerMP) player);
				return true;
			}
		}

		return false;
	}
}
