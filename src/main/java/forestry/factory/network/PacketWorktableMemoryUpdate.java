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

import net.minecraft.tileentity.TileEntity;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.PacketCoordinates;
import forestry.core.network.PacketId;
import forestry.core.proxy.Proxies;
import forestry.factory.recipes.RecipeMemory;
import forestry.factory.tiles.TileWorktable;

public class PacketWorktableMemoryUpdate extends PacketCoordinates {

	private RecipeMemory recipeMemory;

	public static void onPacketData(DataInputStreamForestry data) throws IOException {
		new PacketWorktableMemoryUpdate(data);
	}

	private PacketWorktableMemoryUpdate(DataInputStreamForestry data) throws IOException {
		super(data);
	}

	public PacketWorktableMemoryUpdate(TileWorktable worktable) {
		super(PacketId.WORKTABLE_MEMORY_UPDATE, worktable);
		this.recipeMemory = worktable.getMemory();
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		recipeMemory.writeData(data);
	}

	@Override
	protected void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);

		TileEntity tile = getTarget(Proxies.common.getRenderWorld());
		if (tile instanceof TileWorktable) {
			((TileWorktable) tile).getMemory().readData(data);
		}
	}
}
