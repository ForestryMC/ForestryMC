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

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketIdServer;
import forestry.core.network.packets.PacketCoordinates;
import forestry.core.proxy.Proxies;
import forestry.factory.gui.ContainerWorktable;
import forestry.factory.recipes.MemorizedRecipe;
import forestry.factory.tiles.TileWorktable;

/**
 * Used to sync the worktable crafting grid from Client to Server.
 */
public class PacketWorktableRecipeRequest extends PacketCoordinates implements IForestryPacketServer {
	private MemorizedRecipe recipe;

	public PacketWorktableRecipeRequest() {
	}

	public PacketWorktableRecipeRequest(TileWorktable worktable, MemorizedRecipe recipe) {
		super(worktable);
		this.recipe = recipe;
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
	public void onPacketData(DataInputStreamForestry data, EntityPlayerMP player) throws IOException {
		TileEntity tile = getTarget(player.worldObj);
		if (tile instanceof TileWorktable) {
			TileWorktable worktable = (TileWorktable) tile;
			worktable.setCurrentRecipe(recipe);

			if (player.openContainer instanceof ContainerWorktable) {
				ContainerWorktable containerWorktable = (ContainerWorktable) player.openContainer;
				containerWorktable.updateCraftMatrix();
			}

			Proxies.net.sendNetworkPacket(new PacketWorktableRecipeUpdate(worktable), player.worldObj);
		}
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.WORKTABLE_RECIPE_REQUEST;
	}
}
