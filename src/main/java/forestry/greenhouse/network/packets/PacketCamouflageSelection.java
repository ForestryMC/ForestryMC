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
package forestry.greenhouse.network.packets;

import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import forestry.api.core.ICamouflageHandler;
import forestry.core.network.ForestryPacket;
import forestry.core.network.PacketBufferForestry;
import forestry.greenhouse.camouflage.CamouflageHandlerType;

public abstract class PacketCamouflageSelection extends ForestryPacket {
	protected final BlockPos pos;
	protected final ItemStack camouflageStack;
	protected final CamouflageHandlerType handlerType;

	public PacketCamouflageSelection(ICamouflageHandler handler, CamouflageHandlerType handlerType) {
		this.pos = handler.getCoordinates();
		this.camouflageStack = handler.getCamouflageBlock();
		this.handlerType = handlerType;
	}

	@Override
	protected void writeData(PacketBufferForestry data) throws IOException {
		data.writeBlockPos(pos);
		data.writeShort(handlerType.ordinal());
		data.writeItemStack(camouflageStack);
	}
}
