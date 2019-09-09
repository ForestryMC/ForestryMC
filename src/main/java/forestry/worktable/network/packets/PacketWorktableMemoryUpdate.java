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
package forestry.worktable.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;
import forestry.core.tiles.TileUtil;
import forestry.worktable.recipes.RecipeMemory;
import forestry.worktable.tiles.TileWorktable;

public class PacketWorktableMemoryUpdate extends ForestryPacket implements IForestryPacketClient {
	private final BlockPos pos;
	private final RecipeMemory recipeMemory;

	public PacketWorktableMemoryUpdate(TileWorktable worktable) {
		this.pos = worktable.getPos();
		this.recipeMemory = worktable.getMemory();
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.WORKTABLE_MEMORY_UPDATE;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeBlockPos(pos);
		recipeMemory.writeData(data);
	}

	@OnlyIn(Dist.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, PlayerEntity player) throws IOException {
			BlockPos pos = data.readBlockPos();

			TileWorktable tile = TileUtil.getTile(player.world, pos, TileWorktable.class);
			if (tile != null) {
				tile.getMemory().readData(data);
			}
		}
	}
}
