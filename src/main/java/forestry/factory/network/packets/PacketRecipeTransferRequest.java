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

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketHandlerServer;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdServer;
import forestry.core.tiles.TileBase;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.NetworkUtil;
import forestry.factory.tiles.TileCarpenter;
import forestry.factory.tiles.TileFabricator;

public class PacketRecipeTransferRequest extends ForestryPacket implements IForestryPacketServer {
	private final BlockPos pos;
	private final NonNullList<ItemStack> craftingInventory;

	public PacketRecipeTransferRequest(TileBase base, NonNullList<ItemStack> craftingInventory) {
		this.pos = base.getPos();
		this.craftingInventory = craftingInventory;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeBlockPos(pos);
		data.writeItemStacks(craftingInventory);
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.RECIPE_TRANSFER_REQUEST;
	}

	public static class Handler implements IForestryPacketHandlerServer {
		@Override
		public void onPacketData(PacketBufferForestry data, ServerPlayerEntity player) throws IOException {
			BlockPos pos = data.readBlockPos();
			NonNullList<ItemStack> craftingInventory = data.readItemStacks();

			TileEntity tile = TileUtil.getTile(player.world, pos);
			if (tile instanceof TileCarpenter) {
				TileCarpenter carpenter = (TileCarpenter) tile;
				int index = 0;
				for (ItemStack stack : craftingInventory) {
					carpenter.getCraftingInventory().setInventorySlotContents(index, stack);
					index++;
				}

				NetworkUtil.sendNetworkPacket(new PacketRecipeTransferUpdate(carpenter, craftingInventory), pos, player.world);
			} else if (tile instanceof TileFabricator) {
				TileFabricator fabricator = (TileFabricator) tile;
				int index = 0;
				for (ItemStack stack : craftingInventory) {
					fabricator.getCraftingInventory().setInventorySlotContents(index, stack);
					index++;
				}

				NetworkUtil.sendNetworkPacket(new PacketRecipeTransferUpdate(fabricator, craftingInventory), pos, player.world);
			}
		}
	}
}
