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

import javax.annotation.Nullable;
import java.io.IOException;

import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;
import forestry.core.tiles.TileUtil;
import forestry.worktable.recipes.MemorizedRecipe;
import forestry.worktable.tiles.TileWorktable;

/**
 * Used to sync the worktable crafting result from Server to Client.
 */
public class PacketWorktableRecipeUpdate extends ForestryPacket implements IForestryPacketClient {
	private final BlockPos pos;
	@Nullable
	private final MemorizedRecipe recipe;

	public PacketWorktableRecipeUpdate(TileWorktable worktable) {
		this.pos = worktable.getBlockPos();
		this.recipe = worktable.getCurrentRecipe();
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.WORKTABLE_CRAFTING_UPDATE;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeBlockPos(pos);
		data.writeStreamable(recipe);
	}

	@OnlyIn(Dist.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, Player player) throws IOException {
			BlockPos pos = data.readBlockPos();
			MemorizedRecipe recipe = data.readStreamable(MemorizedRecipe::new);

			TileUtil.actOnTile(player.level, pos, TileWorktable.class, tile -> tile.setCurrentRecipe(recipe));
		}
	}
}
