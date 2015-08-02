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

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import forestry.core.gadgets.TileForestry;
import forestry.core.interfaces.IItemStackDisplay;
import forestry.core.proxy.Proxies;

public class PacketItemStackDisplay extends PacketCoordinates {

	private ItemStack itemStack;

	public static void onPacketData(DataInputStreamForestry data) throws IOException {
		new PacketItemStackDisplay(data);
	}

	private PacketItemStackDisplay(DataInputStreamForestry data) throws IOException {
		super(data);
	}

	public <T extends TileForestry & IItemStackDisplay> PacketItemStackDisplay(T tile, ItemStack itemStack) {
		super(PacketId.GUI_ITEMSTACK, tile);
		this.itemStack = itemStack;
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeItemStack(itemStack);
	}

	@Override
	protected void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		itemStack = data.readItemStack();

		TileEntity tile = getTarget(Proxies.common.getRenderWorld());
		if (tile instanceof IItemStackDisplay) {
			((IItemStackDisplay) tile).handleItemStackForDisplay(itemStack);
		}
	}
}
