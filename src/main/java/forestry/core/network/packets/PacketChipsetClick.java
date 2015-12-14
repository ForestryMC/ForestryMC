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

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import forestry.core.circuits.ItemCircuitBoard;
import forestry.core.gui.IContainerSocketed;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketIdServer;

public class PacketChipsetClick extends PacketSlotClick implements IForestryPacketServer {

	public PacketChipsetClick() {
	}

	public PacketChipsetClick(TileEntity tile, int slot) {
		super(tile, slot);
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayerMP player) throws IOException {
		if (!(player.openContainer instanceof IContainerSocketed)) {
			return;
		}
		ItemStack itemstack = player.inventory.getItemStack();
		if (!(itemstack.getItem() instanceof ItemCircuitBoard)) {
			return;
		}

		((IContainerSocketed) player.openContainer).handleChipsetClickServer(getSlot(), player, itemstack);
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.CHIPSET_CLICK;
	}
}
