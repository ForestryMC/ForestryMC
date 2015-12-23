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
package forestry.factory.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.PacketIdClient;
import forestry.core.network.packets.PacketCoordinates;
import forestry.core.proxy.Proxies;
import forestry.factory.recipes.RecipeMemory;
import forestry.factory.tiles.TileWorktable;

public class PacketWorktableMemoryUpdate extends PacketCoordinates implements IForestryPacketClient {

	private RecipeMemory recipeMemory;

	public PacketWorktableMemoryUpdate() {
	}

	public PacketWorktableMemoryUpdate(TileWorktable worktable) {
		super(worktable);
		this.recipeMemory = worktable.getMemory();
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		recipeMemory.writeData(data);
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		TileEntity tile = getTarget(Proxies.common.getRenderWorld());
		if (tile instanceof TileWorktable) {
			((TileWorktable) tile).getMemory().readData(data);
		}
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.WORKTABLE_MEMORY_UPDATE;
	}
}
