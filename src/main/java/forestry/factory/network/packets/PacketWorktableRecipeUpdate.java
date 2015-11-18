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
import forestry.factory.recipes.MemorizedRecipe;
import forestry.factory.tiles.TileWorktable;

/**
 * Used to sync the worktable crafting result from Server to Client.
 */
public class PacketWorktableRecipeUpdate extends PacketCoordinates implements IForestryPacketClient {
	private MemorizedRecipe recipe;

	public PacketWorktableRecipeUpdate() {
	}

	public PacketWorktableRecipeUpdate(TileWorktable worktable) {
		super(worktable);
		this.recipe = worktable.getCurrentRecipe();
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeStreamable(recipe);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		recipe = data.readStreamable(MemorizedRecipe.class);
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		TileEntity tile = getTarget(Proxies.common.getRenderWorld());
		if (tile instanceof TileWorktable) {
			((TileWorktable) tile).setCurrentRecipe(recipe);
		}
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.WORKTABLE_CRAFTING_UPDATE;
	}
}
